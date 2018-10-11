package com.akki.locationalarm.networking;

import com.akki.locationalarm.models.LocationData;
import com.akki.locationalarm.models.LocationHistoryResponse;
import com.akki.locationalarm.models.LocationSaveResponse;
import com.akki.locationalarm.models.LoginResponse;
import com.akki.locationalarm.models.UserCredentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public interface APIInterface {

    @POST("login")
    Call<LoginResponse> loginWithCredentials(@Header("Authorization") String authHeader,
                                             @Header("Content-Type") String contentTypeHeader,
                                             @Header("Accept") String acceptHeader,
                                             @Body UserCredentials data);

    @GET("locations")
    Call<LocationHistoryResponse> locationHistory(@Header("Authorization") String authHeader,
                                                     @Header("Content-Type") String contentTypeHeader,
                                                     @Header("Accept") String acceptHeader);

    @POST("location")
    Call<LocationSaveResponse> saveLocation(@Header("Authorization") String authHeader,
                                            @Header("Content-Type") String contentTypeHeader,
                                            @Header("Accept") String acceptHeader,
                                            @Body LocationData data);

}
