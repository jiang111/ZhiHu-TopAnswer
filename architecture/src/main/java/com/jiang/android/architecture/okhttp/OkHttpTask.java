/**
 * created by jiang, 16/5/14
 * Copyright (c) 2016, jyuesong@gmail.com All Rights Reserved.
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

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiang.android.architecture.okhttp.callback.BaseCallBack;
import com.jiang.android.architecture.okhttp.cookie.CookieJarImpl;
import com.jiang.android.architecture.okhttp.cookie.store.CookieStore;
import com.jiang.android.architecture.okhttp.cookie.store.HasCookieStore;
import com.jiang.android.architecture.okhttp.cookie.store.MemoryCookieStore;
import com.jiang.android.architecture.okhttp.exception.Exceptions;
import com.jiang.android.architecture.okhttp.log.HttpLoggingInterceptor;
import com.jiang.android.architecture.okhttp.request.CountingRequestBody;
import com.jiang.android.architecture.okhttp.request.DeleteRequest;
import com.jiang.android.architecture.okhttp.request.PostRequest;
import com.jiang.android.architecture.okhttp.request.PutRequest;
import com.jiang.android.architecture.okhttp.request.UploadRequest;
import com.jiang.android.architecture.okhttp.request.getRequest;
import com.jiang.android.architecture.okhttp.utils.HttpsUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jiang on 16/5/14.
 */
public class OkHttpTask {

    public static final int TYPE_GET = 30;  //get请求
    public static final int TYPE_POST = 60; // post请求
    public static final int TYPE_PUT = 70; // post请求
    public static final int TYPE_DELETE = 90; // delete请求
    public static final int EXIT_LOGIN = 1010;
    private static int[] exitLoginCode = null;
    private static OkHttpTask mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;
    private static boolean isDebug = false;


    final static class ERROR_OPTIONS {
        public static final String EROR_REQUEST_ERROR = "请求失败,请重试";
        public static final String EROR_REQUEST_UNKNOWN = "未知错误";
        public static final String EROR_REQUEST_CREATEDIRFAIL = "创建文件失败,请检查权限";
        public static final String EROR_REQUEST_IO = "IO异常，或者本次任务被取消";


        public static final String EROR_REQUEST_EXITLOGIN = "请重新登录";
    }


    public static OkHttpTask getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpTask.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpTask(null);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpTask getInstance(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpTask.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpTask(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    private OkHttpTask(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            //cookie enabled
            okHttpClientBuilder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            if (isDebug) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message, String json) {
                        LogUtils.i(message);
                        if (!TextUtils.isEmpty(json)) {
                            LogUtils.i("--------json---------\n");
                            LogUtils.json(json);
                            LogUtils.i("--------end----------\n");
                        }
                    }

                });
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(loggingInterceptor);
            }
            mOkHttpClient = okHttpClientBuilder.build();
        } else {
            mOkHttpClient = okHttpClient;
        }


        init();
    }

    public Gson getmGson() {
        return mGson;
    }

    public static void debug(boolean isdebug) {
        isDebug = isdebug;
    }

    public static void exitLoginCode(int... code) {
        exitLoginCode = code;
    }

    private void init() {
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    /**
     * @param url      url
     * @param params   需要传递的参数  get请求? 后面的参数也可以通过param传递
     * @param callBack 返回的回调
     * @param tag      唯一的key， 可以通过这个唯一的key来取消网络请求
     * @param type     请求的类型
     * @param headers  需要特殊处理的请求头
     */
    public void filterData(String url, Object tag, Map<String, String> params, final BaseCallBack callBack, Map<String, String> headers, int type) {
        doJobNormal(url, params, callBack, tag, type, headers);
    }


    /**
     * 上传文件
     *
     * @param url      url
     * @param headers  验证
     * @param callBack 回调
     * @param tag      tag
     */
    void uploadFile(String url, Map<String, String> headers, List<String> files, final BaseCallBack callBack, Object tag) {


        if (isDebug) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("------upload file-------").append("\n")
                    .append("url: ").append(url).append("\n");
            if (headers != null) {
                logBuilder.append("headers: ").append(headers.toString()).append("\n");
            }
            if (files != null) {
                logBuilder.append("files: ").append(files.toString()).append("\n");
            }
            LogUtils.i(logBuilder.toString());
        }


        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (files != null && files.size() > 0) {
            final int fileSize = files.size();

            final long fileProgress[] = new long[fileSize];
            for (int i = 0; i < fileSize; i++) {
                final File file = new File(files.get(i));

                CountingRequestBody countingRequestBody = new CountingRequestBody(RequestBody.create(MediaType.parse("application/octet-stream"), file), new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(final long bytesWritten, final long contentLength, final int position) {
                        long progress = bytesWritten * 100 / contentLength;
                        if (progress < 95 && progress - fileProgress[position] < 4)
                            return; //为了尽量少走回调
                        fileProgress[position] = progress;
                        int result = 0;
                        for (int i = 0; i < fileProgress.length; i++) {
                            result += fileProgress[i];
                        }
                        result = result / fileSize;
                        progressCallBack(result, callBack);

                    }
                }, i);
                // 索引到最后一个斜杠
                String resultName = files.get(i).substring(files.get(i).lastIndexOf("/") + 1);
                builder.addFormDataPart("upload", resultName, countingRequestBody);
                if (isDebug) {
                    LogUtils.i("开始上传文件 file: " + file.toString());
                }
            }
        } else {
            failCallBack(WS_State.OTHERS, "没有文件可上传", callBack);
            return;
        }

        final Call call = mOkHttpClient.newCall(UploadRequest.buildPostRequest(url, headers, tag, builder.build()));
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                dealFailResponse(ERROR_OPTIONS.EROR_REQUEST_ERROR, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int code = response.code();
                if (code == 200) {
                    dealSuccessResponse(response, callBack);
                } else {
                    String msg = response.message();
                    if (TextUtils.isEmpty(msg)) {
                        msg = "上传失败,请重试";
                    }
                    failCallBack(code, msg, callBack);
                }

            }
        });


    }

    public void downLoadFile(final String url, final String destFileDir, final String fileName, final BaseCallBack callback, Object tag, Map<String, String> headers) {
        if (isDebug) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("------download file-------").append("\n")
                    .append("url: ").append(url).append("\n")
                    .append("destFileDir: ").append(destFileDir).append("\n")
                    .append("fileName: ").append(fileName).append("\n");
            if (headers != null) {
                logBuilder.append("headers: ").append(headers.toString()).append("\n");
            }
            LogUtils.i(logBuilder.toString());
        }


        int type = TYPE_GET;
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(destFileDir) || callback == null || TextUtils.isEmpty(fileName))
            return;
        callback.onBefore();
        doJob(url, null, tag, type, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dealFailResponse(ERROR_OPTIONS.EROR_REQUEST_ERROR, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if (code == 200) {
                    FileOutputStream fos = null;
                    InputStream is = null;
                    try {
                        byte[] buf = new byte[2048];

                        is = response.body().byteStream();
                        long fileLongth = (int) response.body().contentLength();
                        int len;
                        long totalLength = 0;
                        long lastProgress = -1;

                        File dir = new File(destFileDir);
                        if (!dir.exists()) {
                            boolean createDirSuccess = dir.mkdirs();
                            if (!createDirSuccess) {  //创建文件夹失败
                                failCallBack(WS_State.EXCEPTION, ERROR_OPTIONS.EROR_REQUEST_CREATEDIRFAIL, callback);
                                return;
                            }
                        }
                        File file = new File(dir, fileName);
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            totalLength += len;
                            long progress = totalLength * 100 / fileLongth;

                            if (lastProgress != progress) {
                                progressCallBack(progress, callback);
                            }
                            lastProgress = progress;

                        }
                        fos.flush();
                        //如果下载文件成功，第一个参数为文件的绝对路径

                        successCallBack("下载成功", callback);
                    } catch (IOException e) {
                        failCallBack(WS_State.EXCEPTION, ERROR_OPTIONS.EROR_REQUEST_IO, callback);
                    } catch (Exception e) {
                        failCallBack(WS_State.EXCEPTION, ERROR_OPTIONS.EROR_REQUEST_UNKNOWN, callback);
                    } finally {
                        try {
                            if (is != null) is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    failCallBack(code, ERROR_OPTIONS.EROR_REQUEST_ERROR, callback);
                }
            }
        }, headers);

    }

    public void doJobNormal(final String url, Map<String, String> params, final BaseCallBack callBack, Object tag, final int TYPE, Map<String, String> headers) {

        callBack.onBefore();

        doJob(url, params, tag, TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dealFailResponse(ERROR_OPTIONS.EROR_REQUEST_ERROR, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onFinishResponse(response);
                dealSuccessResponse(response, callBack);
            }
        }, headers);
    }

    private void doJob(String url, Map<String, String> params, Object tag, int type, Callback callback, Map<String, String> headers) {

        Request request = null;
        if (type == TYPE_POST) {
            request = PostRequest.buildPostRequest(url, params, tag, headers);  //拿到一个post的request
        } else if (TYPE_GET == type) {
            request = getRequest.buildGetRequest(url, params, tag, headers);//拿到一个get的request
        } else if (TYPE_PUT == type) {
            request = PutRequest.buildOtherRequest(url, params, tag, headers, type);
        } else if (type == TYPE_DELETE) {
            request = DeleteRequest.buildDeleteRequest(url, params, tag, headers);
        } else {
            Exceptions.illegalArgument("只支持 get post put delete");
        }

        final Call call = mOkHttpClient.newCall(request);  //获得一个 call， 这是okhttp核心的类

        call.enqueue(callback);  //加入队列
    }


    //*********************************处理返回的结果********************************************************
    private void dealSuccessResponse(Response response, BaseCallBack callBack) {
        try {
            int status = response.code();
            if (containExitLoginCode(status)) {
                EventBus.getDefault().post(exitLoginCode);
                failCallBack(EXIT_LOGIN, ERROR_OPTIONS.EROR_REQUEST_EXITLOGIN, callBack);
            } else {
                final String string = response.body().string();
                if (status == 200) {
                    Object o = mGson.fromJson(string, callBack.mType);
                    successCallBack(o, callBack);
                } else if (status == 204) {
                    emptyCallBack(WS_State.NODATA, "暂无数据", callBack);
                } else {
                    ws_ret o = mGson.fromJson(string, ws_ret.class);
                    if (TextUtils.isEmpty(o.getMsg())) {
                        failCallBack(status, ERROR_OPTIONS.EROR_REQUEST_ERROR, callBack);
                    } else {
                        failCallBack(status, o.getMsg(), callBack);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isDebug) {
                LogUtils.d("exception info: " + e.toString());
            }
            failCallBack(WS_State.EXCEPTION, ERROR_OPTIONS.EROR_REQUEST_ERROR, callBack);
        } finally {
            try {
                response.body().close();
            } catch (Exception e) {
                if (isDebug) {
                    LogUtils.d("解析Body失败: " + e.toString());
                }
            }
        }

    }

    private boolean containExitLoginCode(int status) {
        if (exitLoginCode == null)
            return false;
        for (int i = 0; i < exitLoginCode.length; i++) {
            if (status == exitLoginCode[i]) {
                return true;
            }
        }
        return false;
    }


    private void dealFailResponse(String msg, BaseCallBack callBack) {
        failCallBack(WS_State.SERVER_ERROR, msg, callBack);
    }

    private void failCallBack(final int state, final String msg, final BaseCallBack callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                ws_ret ret = new ws_ret(state, msg);
                callback.onFail(ret);
                callback.onAfter();
            }
        });
    }


    private void successCallBack(final Object o, final BaseCallBack ret) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                ret.onSuccess(o);
                ret.onAfter();
            }
        });
    }

    private void progressCallBack(final long l, final BaseCallBack callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onProgress(l);
            }
        });

    }

    private void emptyCallBack(final int state, final String msg, final BaseCallBack callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                ws_ret ret = new ws_ret(state, msg);
                callback.onNoData(ret);
                callback.onAfter();
            }
        });
    }

    public void cancelTask(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    public CookieStore getCookieStore() {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null) {
            Exceptions.illegalArgument("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        }
        if (cookieJar instanceof HasCookieStore) {
            return ((HasCookieStore) cookieJar).getCookieStore();
        } else {
            return null;
        }
    }


    /**
     * for https-way authentication
     *
     * @param certificates
     */
    public void setCertificates(InputStream... certificates) {
        SSLSocketFactory sslSocketFactory = HttpsUtils.getSslSocketFactory(certificates, null, null);

        OkHttpClient.Builder builder = getOkHttpClient().newBuilder();
        builder = builder.sslSocketFactory(sslSocketFactory);
        mOkHttpClient = builder.build();


    }

    /**
     * for https mutual authentication
     *
     * @param certificates
     * @param bksFile
     * @param password
     */
    public void setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, bksFile, password))
                .build();
    }


}
