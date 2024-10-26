package com.matou.smartcar.net;

import android.net.Uri;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {

    public static final String APP_ID = "r-mirror";
    //    public static final String mBaseUrl = "http://cqxdq.i-vista.org:40001/";
    public static final String mBaseUrl = "http://cq-v2x.ljzhct.com:9000/";


    public static <T> T getApi(String baseUrl, Class<T> clazz) {


        Retrofit build = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getUnsafeOkHttpClient(baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        T t = build.create(clazz);
        return t;
    }

    public static String getAppId(){
        return APP_ID;
    }


    public static OkHttpClient getUnsafeOkHttpClient(final String baseUrl) {
//        if (mClient != null) return mClient;
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };


            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());


            final SSLSocketFactory sslSocketFactory;

            sslSocketFactory = sslContext.getSocketFactory();
//            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
//            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder = builder
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {


                            Request originalRequest = chain.request();
                            Request authorised = originalRequest.newBuilder()
//                                    .addHeader("Accept-Encoding", "gzip")

//                                    .addHeader("Authorization", getToken())
//                                    .addHeader("apiKey", apikey)
                                    .build();



                            Response response = chain.proceed(authorised);

                            if (response.code() == 200) {
                                //这里是网络拦截器，可以做错误处理
                                return response;
//                                MediaType mediaType = response.body().contentType();
//                                //当调用此方法java.lang.IllegalStateException: closed，原因为OkHttp请求回调中response.body().string()只能有效调用一次
//                                //String content = response.body().string();
//                                byte[] data = response.body().bytes();
//                                if (GZIPUtils.isGzip(response.headers())) {
//                                    //请求头显示有gzip，需要解压
//                                    data = GZIPUtils.uncompress(data);
//                                }
//
//
//                                //创建一个新的responseBody，返回进行处理
//                                return response.newBuilder()
//                                        .body(ResponseBody.create(mediaType, data))
//                                        .build();
                            } else {
                                return response;
                            }


//                        return chain.proceed(authorised);

                        }
                    });
//            if (!CommonApplication.isRelease) {
//                builder = builder.addNetworkInterceptor(logInterceptor);
//            }

//                    .sslSocketFactory(new TLSSocketFactory())
            builder.sslSocketFactory(sslSocketFactory)

                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            Uri uri = Uri.parse(baseUrl);
                            return TextUtils.equals(hostname, uri.getHost());

                        }
                    });
//            mClient = builder.build();
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }


//    public static RequestBody getUpLoadFileBody(String base64) {
//        List<String> base64s = new ArrayList<>();
//        base64s.add(base64);
//        return getUpLoadFileBody(base64s);
//
//    }

//    public static RequestBody getUpLoadFileBody(List<String> base64s) {
//        UpLoadFileBean upLoadFileBean = new UpLoadFileBean();
//        List<UpLoadFileBean.FileBean> fileBeans = new ArrayList<>();
//
//        for (String base64 : base64s) {
//            UpLoadFileBean.FileBean bean = new UpLoadFileBean.FileBean();
//            bean.setBase64Url(base64);
//            bean.setFileName("file" + System.currentTimeMillis());
//            fileBeans.add(bean);
//        }
//
//        upLoadFileBean.setFileList(fileBeans);
//        String json = new Gson().toJson(upLoadFileBean);
//        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
//    }


    /**
     * 获取TOKEN
     *
     * @return
     */
//    public static String getToken() {
//        if (TextUtils.isEmpty((String) SPUtil.get(Config.USER_FLAG, ""))) {
//            return "";
//        } else {
//            return (String) SPUtil.get(Config.USER_FLAG, "");
//        }
//    }
    public static RequestBody getBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map.isEmpty()) {
            return builder.build();
        }
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value != null && value.length() > 0) {
                builder.add(key, map.get(key));
            }
        }
        return builder.build();
    }

    /**
     * 获取sign
     *
     * @param params
     * @return
     */
//    public static String getSign(Map<String, String> params) {
//        String stringB = assemble(getParamsMap(getParamsMap(params))) + secretKey;
//        String stringBBase64 = EncodeUtils.base64Encode2String(stringB.getBytes());
//        String sign = EncryptUtils.encryptMD5ToString(stringBBase64).toUpperCase();
//        return sign;
//    }

    /**
     * 参数整理  获取sign用
     *
     * @param params
     * @return
     */
//    private static TreeMap<String, String> getParamsMap(Map<String, String> params) {
//        TreeMap<String, String> map = new TreeMap<>();
//        map.put("platform", "android");
//        map.put("brand", DeviceUtils.getModel());
//        map.put("channel", ChannelUtil.getChannel(CommonApplication.getContext()));
//        map.put("version", AppUtil.getVersionName());
//        map.put("device", Build.DEVICE);
//        map.put("token", getToken());
//        map.put("apiKey", apikey);
//
//        if (params != null && !params.isEmpty()) {
//            for (String key : params.keySet()) {
//                map.put(key, params.get(key));
//            }
//        }
//        return map;
//    }


    /**
     * 参数处理  sign用
     *
     * @param params
     * @return
     */
    private static String assemble(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (String kValue : params.keySet()) {
            String value = params.get(kValue);
            if (value != null && value.length() > 0) {
                sb.append(kValue).append("=").append(params.get(kValue)).append("&");
            }
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    private static MultipartBody getUploadBody(String fileType, List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("fileCount", String.valueOf(files.size()));
        builder.addFormDataPart("fileType", fileType);

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mime = fileNameMap.getContentTypeFor(file.getPath());
            RequestBody requestBody = RequestBody.create(MediaType.parse(mime), file);
            builder.addFormDataPart("files", file.getName(), requestBody);
        }

        return builder.build();
    }

    public static List<MultipartBody.Part> getUpLoadParts(String fileType, List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("fileType", fileType)
                .setType(MultipartBody.FORM);//表单类型
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);

            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
            String encode;
            try {
                encode = URLEncoder.encode(file.getName(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                encode = file.getName();
            }
            builder.addFormDataPart("files", encode, photoRequestBody);
        }
        return builder.build().parts();
    }
}
