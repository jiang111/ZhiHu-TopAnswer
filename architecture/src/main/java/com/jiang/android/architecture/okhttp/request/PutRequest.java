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

package com.jiang.android.architecture.okhttp.request;

import com.jiang.android.architecture.okhttp.OkHttpTask;
import com.jiang.android.architecture.okhttp.Param;
import com.jiang.android.architecture.okhttp.utils.HeaderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by jiang on 16/5/14.
 */
public class PutRequest {
    public static Request buildOtherRequest(String url, Map<String, String> params, Object tag, Map<String, String> headers, int type) {
        if (params == null) {
            params = new HashMap<>();
        }
        RequestBody requestBody = null;
        if (params == null || params.size() == 0) {
            requestBody = RequestBody.create(null, new byte[0]);
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            requestBody = builder.build();
        }
        Request.Builder reqBuilder = new Request.Builder();

        if (type == OkHttpTask.TYPE_DELETE) {
            reqBuilder.delete(requestBody).url(url);

        } else if (type == OkHttpTask.TYPE_PUT) {
            reqBuilder.put(requestBody).url(url);
        }

        List<Param> valdatedHeaders = HeaderUtils.validateHeaders(headers);
        if (valdatedHeaders != null && valdatedHeaders.size() > 0) {
            for (int i = 0; i < valdatedHeaders.size(); i++) {
                Param param = valdatedHeaders.get(i);
                String key = param.key;
                String value = param.value;
                reqBuilder.addHeader(key, value);
            }

        }

        if (tag != null) {
            reqBuilder.tag(tag);
        }
        return reqBuilder.build();


    }
}
