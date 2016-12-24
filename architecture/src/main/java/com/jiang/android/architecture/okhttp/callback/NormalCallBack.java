package com.jiang.android.architecture.okhttp.callback;

import com.jiang.android.architecture.okhttp.ws_ret;

import okhttp3.Response;

/**
 * Created by jiang on 2016/11/29.
 */

public abstract class NormalCallBack<T> extends BaseCallBack<T> {


    @Override
    public void onNoData(ws_ret ret) {

    }

    @Override
    public void onBefore() {

    }

    @Override
    public void onAfter() {

    }

    @Override
    public void onFinishResponse(Response response) {

    }

    @Override
    public void onProgress(long progress) {

    }
}
