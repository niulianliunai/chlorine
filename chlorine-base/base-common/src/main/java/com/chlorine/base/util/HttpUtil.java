package com.chlorine.base.util;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gaochenyang
 * @title: OkHttpUtils
 * @projectName java_reptile
 * @description: TODO
 * @date 2020/6/210:42 下午
 */

@Component
@Slf4j
public class HttpUtil {
    private final static int READ_TIMEOUT = 10;
    private final static int CONNECT_TIMEOUT = 10;
    private final static int WRITE_TIMEOUT = 10;

    public OkHttpClient okHttpCline() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.hostnameVerifier((hostname, session) -> true);
        return builder.build();
    }

    public JSONObject okhttpGetJSONObject(String url, JSONObject param) {
        return JSONObject.parseObject(okhttpGetString(url, param));
    }

    public synchronized JSONObject okhttpGetJSONObjectSync(String url, JSONObject param) {
        return JSONObject.parseObject(okhttpGetString(url, param));
    }

    public String okhttpGetString(String url, JSONObject param) {
        String res = "";
        String paramStr = "";
        if (param != null) {
            List<String> params = new ArrayList<>();
            param.forEach((key, value) -> params.add(key + "=" + value));
            if (!params.isEmpty()) {
                paramStr = "?" + String.join("&", params);
            }
        }
        Request request = new Request.Builder()
                .get()
//                .header("X-Token","5491f5c6-8b1e-40a4-9ccc-43fa2b55edf5")
                .url(url + paramStr)
                .build();
        try {
            Response response = okHttpCline().newCall(request).execute();
            res = response.body().string();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return res;
    }

    public JSONObject okhttpPost(String url, JSONObject param) {
        JSONObject res = new JSONObject();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = okHttpCline().newCall(request).execute();
            res = JSONObject.parseObject(response.body().string());
            response.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return res;
    }

    public JSONObject okhttpPostFormData(String url, JSONObject param) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
            multipartBuilder.addFormDataPart(stringObjectEntry.getKey(), String.valueOf(stringObjectEntry.getValue()));
        }
        RequestBody multipartBody = multipartBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        JSONObject res;
        try {
            Response response = okHttpCline().newCall(request).execute();
            res = JSONObject.parseObject(response.body().string());
            response.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return res;
    }

    public Boolean okhttpPostBoolean(String url, JSONObject param) {
        boolean res = false;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = okHttpCline().newCall(request).execute();
            res = Boolean.parseBoolean(response.body().string());
            response.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }
}
