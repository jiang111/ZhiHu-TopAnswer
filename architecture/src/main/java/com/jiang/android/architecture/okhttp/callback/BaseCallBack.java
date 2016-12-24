/**
 * created by jiang, 10/25/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */

package com.jiang.android.architecture.okhttp.callback;

import com.google.gson.internal.$Gson$Types;
import com.jiang.android.architecture.okhttp.ws_ret;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;


/**
 * 所有网络请求的统一回调
 * Created by jiang on 10/25/15.
 */
public abstract class BaseCallBack<T> {

    /**
     * 失败
     *
     * @param ret 数据
     */
    public abstract void onFail(ws_ret ret);  //失败

    /**
     * 成功
     *
     * @param t 数据
     */
    public abstract void onSuccess(T t);  //成功


    /**
     * 无数据， 只在get中用到
     *
     * @param ret 数据
     */
    public abstract void onNoData(ws_ret ret); //无数据， 注意： 当是post请求的时候该方法不会回调

    /**
     * 网络请求开始时执行
     */
    public abstract void onBefore();

    /**
     * 网络请求结束时执行， 比如停止下拉刷新控件的执行
     */
    public abstract void onAfter();

    /**
     * 所有的网络请求成功以后都会走这个方法，
     *
     * @param response 数据
     */
    public abstract void onFinishResponse(Response response);

    /**
     * 下载文件事 的进度
     *
     * @param progress 进度
     */
    public abstract void onProgress(long progress);

    /**
     * 获得泛型的类型
     */
    public Type mType;

    public BaseCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("泛型参数不能为空");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
}
