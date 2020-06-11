package com.zq.modulemvp.basemvp.api;

import com.zq.modulemvp.basemvp.util.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static com.zq.modulemvp.basemvp.api.bean.Result.SUCCESS_CODE;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
class EmptyConverter extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return (Converter<ResponseBody, Object>) body -> {
            if (body.contentLength() == 0) {
                JSONObject json = new JSONObject();
                try {
                    json.put("data", null);
                    json.put("code", SUCCESS_CODE);
                    json.put("message", "empty");
                } catch (JSONException e) {
                    AppLog.e("empty json response failed");
                }
                return json;
            }
            return delegate.convert(body);
        };
    }
}
