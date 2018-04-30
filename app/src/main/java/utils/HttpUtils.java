package utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/29.
 */

public class HttpUtils {
    private OkHttpClient client = null;
    private Map<String, Object> mParam;
    private String mUrl;
    private IHttpResponseListener listener;

    public interface IHttpResponseListener {
        void onSuccess(String s);

        void onFail(String error);
    }

    public HttpUtils(IHttpResponseListener listener) {
        this.listener = listener;
    }

    public void sendPostHttp(String url, Map<String, Object> param) {
        sendHttp(url, param, true);
    }

    public void sendGetHttp(String url, Map<String, Object> param) {
        sendHttp(url, param, false);
    }

    private void sendHttp(String url, Map<String, Object> param, boolean isPost) {
        this.mParam = param;
        this.mUrl = url;

        //请求创建
        Request request = createRequest(isPost);
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        //创建请求队列
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //超时异常
                    listener.onFail("连接超时");
                } else if (e instanceof ConnectException) {
                    //连接异常
                    listener.onFail("连接超时");
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (listener == null) return;
                try {
                    assert response.body() != null;
                    listener.onSuccess(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败code" + response);
                }
            }
        });
    }

    private Request createRequest(boolean isPost) {
        Request request = null;
        if (isPost) {
            //post请求
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            //解析参数
            Iterator<Map.Entry<String, Object>> iterator = mParam.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> map = iterator.next();
                multipartBody.addFormDataPart(map.getKey(), map.getValue().toString());
            }
            request = new Request.Builder().url(mUrl).post(multipartBody.build()).build();
        } else {
            //get请求
            mUrl = mUrl + "?" + getUrl();
            request = new Request.Builder().url(mUrl).build();
        }
        return request;
    }

    private String getUrl() {
        String str;
        StringBuilder s = new StringBuilder();
        Iterator<Map.Entry<String, Object>> iterator = mParam.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> map = iterator.next();
            s.append(map.getKey() + "=" + map.getValue() + "&");
        }
        str = s.substring(0, s.length() - 1);
        return str;
    }
}
