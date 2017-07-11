package com.cortrium.cortriumc3.ApiConnection;

import com.cortrium.cortriumc3.ApiConnection.models.Recordings;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Kike Bodi on 29/06/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public interface CortriumAPI {

    @GET("recordings/{id}/")
    Call<Recordings> getRecording(@Path("id") String id);

    @DELETE("recordings/{id}/")
    Call<ResponseBody> deleteRecording(@Path("id") String id);

    //@GET("recordings/")
    //Call<List<ECGRecording>> getRecordings();

    @Headers({"Cache-Control: max-age=640000", "User-Agent: Retrofit-Android-App"})
    @POST("recordings/")
    Call<ResponseBody> saveRecording(@Body Recordings myRecording);

    @Multipart
    @POST("recordings/{id}/{rec_id}/")
    Call<ResponseBody> uploadRecording(@Path("id") String id, @Path("rec_id") String rec_id, @Part MultipartBody.Part bleFile);

    @Multipart
    @POST("recordings/{id}/{rec_id}/")
    Call<ResponseBody> request(
            @Path("id") String id,
            @Path("rec_id") String rec_id,
            @PartMap Map<String, RequestBody> files
    );
}
