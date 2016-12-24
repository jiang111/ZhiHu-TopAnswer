/**
 * created by jiang, 11/9/15
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

package com.jiang.android.architecture.okhttp;

import android.text.TextUtils;

import com.jiang.android.architecture.okhttp.callback.BaseCallBack;
import com.jiang.android.architecture.okhttp.exception.NotPermissionException;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiang on 11/9/15.
 */
public class OkHttpRequest {

    /**
     * @param url      url
     * @param params   需要传递的参数  get请求? 后面的参数也可以通过param传递
     * @param callBack 返回的回调
     * @param tag      唯一的key， 可以通过这个唯一的key来取消网络请求
     * @param type     请求的类型
     * @param headers  需要特殊处理的请求头
     */
    public static void doJob(String url, Object tag, Map<String, String> params, final BaseCallBack callBack, Map<String, String> headers, int type) {
        OkHttpTask.getInstance().filterData(url, tag, params, callBack, headers, type);
    }

    public static void downLoadFile(String url, String path, String fileName, BaseCallBack callBack, Object tag) {
        OkHttpTask.getInstance().downLoadFile(url, path, fileName, callBack, tag, null);
    }

    public static void uploadFile(String url, Map<String, String> headers, List<String> files, BaseCallBack callBack, Object tag) {
        OkHttpTask.getInstance().uploadFile(url, headers, files, callBack, tag);
    }

    private static void cancel(Object tag) {
        OkHttpTask.getInstance().cancelTask(tag);
    }


    public static class Builder {
        private String url;
        private Object tag;
        private Map<String, String> headers;
        private Map<String, String> params;
        private List<String> files;
        private String path;
        private String fileName;
        private int type;
        private BaseCallBack callBack;

        public Builder build() {
            return this;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder files(List<String> files) {
            if (this.files == null)
                this.files = new ArrayList<>();
            this.files.addAll(files);
            return this;

        }

        public Builder file(String file) {
            if (files == null)
                files = new ArrayList<>();
            files.add(file);
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder addParams(String key, String val) {
            if (this.params == null) {
                params = new IdentityHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String val) {
            if (this.headers == null) {
                headers = new IdentityHashMap<>();
            }
            headers.put(key, val);
            return this;
        }


        public void get(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_GET;
            if (validateParams()) {
                doJob(url, tag, params, callBack, headers, OkHttpTask.TYPE_GET);
            }
        }


        public void post(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_POST;
            if (validateParams()) {
                doJob(url, tag, params, callBack, headers, OkHttpTask.TYPE_POST);
            }
        }

        public void put(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_PUT;
            if (validateParams()) {
                doJob(url, tag, params, callBack, headers, OkHttpTask.TYPE_PUT);
            }
        }

        public void delete(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_DELETE;
            if (validateParams()) {
                doJob(url, tag, params, callBack, headers, OkHttpTask.TYPE_DELETE);
            }
        }

        public void downLoad(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_GET;
            if (validateParams()) {
                downLoadFile(url, path, fileName, callBack, tag);
            }
        }

        public void execute(BaseCallBack c) {
            this.callBack = c;
            if (validateParams()) {
                doJob(url, tag, params, callBack, headers, type);
            }
        }

        /**
         * 上传文件必传
         * url,files,header(用来验证),callBack
         *
         * @param c
         */
        public void upload(BaseCallBack c) {
            this.callBack = c;
            type = OkHttpTask.TYPE_POST;
            if (validateParams()) {
                uploadFile(url, headers, files, callBack, tag);
            }
        }

        private boolean validateParams() {
            if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
                throw new NotPermissionException("url不合法");
            }
            if (type != OkHttpTask.TYPE_GET && type != OkHttpTask.TYPE_POST && type != OkHttpTask.TYPE_PUT && type != OkHttpTask.TYPE_DELETE) {
                throw new NotPermissionException("请先设置请求类型 支持 get,post,put,delete");
            }
            if (callBack == null) {
                throw new NotPermissionException("没有CallBack");
            }
            return true;
        }

    }


}
