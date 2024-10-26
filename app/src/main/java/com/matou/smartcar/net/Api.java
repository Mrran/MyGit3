package com.matou.smartcar.net;

import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.bean.ParkBean;
import com.matou.smartcar.bean.TokenBean;
import com.matou.smartcar.bean.VersionBean;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author ranfeng
 */
public interface Api {
    @GET("api/app-upgrade/appUpgrade/pullPackge")
    Observable<BaseResult<VersionBean>> getVersion(@Query("appid") String appid,
                                                   @Query("timesnap") String timesnap,
                                                   @Query("sign") String sign);

    @GET("api/app-upgrade/appUpgrade/progress")
    Observable<BaseResult<Object>> uploadUpdateStatus(@Query("appid") String appid,
                                                   @Query("timesnap") String timesnap,
                                                   @Query("sign") String sign,
                                                      @Query("appuid") String appuid,
                                                      @Query("upgradeCode") String upgradeCode,
                                                      @Query("state") int state,
                                                      @Query("message") String message);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("api/V2XInformationManageService/obu/getToken")
    Observable<BaseResult<TokenBean>> getToken(@Body RequestBody body);

    @GET("api/app-upgrade/appUpgrade/pullParks")
    Observable<BaseResult<ParkBean>> getParks(@Query("appid") String appid,
                                              @Query("timesnap") String timesnap,
                                              @Query("sign") String sign,
                                              @Query("lng") double lng,
                                              @Query("lat") double lat,
                                              @Query("radius") double radius);

}
