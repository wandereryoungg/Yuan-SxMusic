package com.example.musicplayer.model;

import android.util.Log;

import com.example.musicplayer.app.Constant;
import com.example.musicplayer.contract.ISearchContentContract;
import com.example.musicplayer.entiy.Album;
import com.example.musicplayer.entiy.SearchSong;
import com.example.musicplayer.model.https.RetrofitFactory;
import com.example.musicplayer.util.RxApiManager;

import java.net.UnknownHostException;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 残渊 on 2018/11/21.
 */

public class SearchContentModel implements ISearchContentContract.Model {
    private static final String TAG = "SearchContentModel";
    private ISearchContentContract.Presenter mPresenter;

    public SearchContentModel(ISearchContentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void search(String seek, int offset) {
        RetrofitFactory.createRequest().search(seek, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchSong>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RxApiManager.get().add(Constant.SEARCH_SONG, d);
                    }

                    @Override
                    public void onNext(SearchSong value) {
                        mPresenter.searchSuccess((ArrayList<SearchSong.DataBean.ListBean>) value.getData().getList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof UnknownHostException) {
                            mPresenter.networkError();
                        }
                        e.printStackTrace();
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void searchMore(String seek, int offset) {
        RetrofitFactory.createRequest().search(seek, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchSong>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RxApiManager.get().add(Constant.SEARCH_SONG_MORE, d);
                    }

                    @Override
                    public void onNext(SearchSong value) {
                        if (value.getCode() == 200) {
                            if (value.getData().getList().size() == 0) {
                                mPresenter.searchMoreError();
                            } else {
                                mPresenter.searchMoreSuccess((ArrayList<SearchSong.DataBean.ListBean>) value.getData().getList());
                            }
                        } else {
                            mPresenter.searchMoreError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPresenter.showSearchMoreNetworkError();
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void searchAlbum(String seek, int offset) {
        RetrofitFactory.createRequest().searchAlbum(seek, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Album>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RxApiManager.get().add(Constant.SEARCH_ALBUM, d);
                    }

                    @Override
                    public void onNext(Album value) {
                        if (value.getCode() == 200) {
                            mPresenter.searchAlbumSuccess(value.getData().getList());
                        } else {
                            mPresenter.searchAlbumError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof UnknownHostException) {
                            mPresenter.networkError();
                        } else {
                            mPresenter.searchAlbumError();
                        }
                        e.printStackTrace();
                        Log.d(TAG, "onError: searchAlbumError" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void searchAlbumMore(String seek, int offset) {
        RetrofitFactory.createRequest().searchAlbum(seek, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Album>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RxApiManager.get().add(Constant.SEARCH_ALBUM_MORE, d);
                    }

                    @Override
                    public void onNext(Album value) {
                        if (value.getCode()==200) {
                            Log.d(TAG, "onNext: success");
                            if (value.getData().getCurnum() == 0) {
                                mPresenter.searchMoreError();
                            } else {
                                mPresenter.searchAlbumMoreSuccess(value.getData().getList());
                            }
                        } else {
                            mPresenter.searchMoreError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPresenter.showSearchMoreNetworkError();
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
