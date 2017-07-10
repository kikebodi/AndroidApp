package com.cortrium.cortriumc3.ApiConnection;

import android.content.Context;
import android.util.Log;

import com.cortrium.cortriumc3.ApiConnection.models.Recordings;
import com.cortrium.cortriumc3.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kike Bodi on 29/06/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public class ApiConnectionManager {

    private static final String TAG = "API-Conn";
    private final CortriumAPI cortriumAPI;

    public ApiConnectionManager(Context con){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(con.getResources().getString(R.string.api_url)) // API url is hidden
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.build())
                .build();

        this.cortriumAPI = retrofit.create(CortriumAPI.class);
    }

    public void getRecordingFromAPI(String recordingId){
        Call<Recordings> call = cortriumAPI.getRecording(recordingId);
        call.enqueue(new Callback<Recordings>() {
            @Override
            public void onResponse(Call<Recordings> call, Response<Recordings> response) {
                Log.d(TAG,"onSuccess");
                int statusCode = response.code();
                // Use "recording" as you want
                Recordings recording = response.body();
            }

            @Override
            public void onFailure(Call<Recordings> call, Throwable t) {
                Log.d(TAG,"onFailure GET");
                t.printStackTrace();
            }
        });
    }

    public void postRecordingToAPI(Recordings myRecording, final File bleFile){
        Call<ResponseBody> response = cortriumAPI.saveRecording(myRecording);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG+"-Create",response.message());
                try {
                    String responseBody = response.body().string();
                    JSONObject jObj = new JSONObject(responseBody);
                    String id = jObj.getString("id");
                    uploadFile(id, bleFile);

                } catch (IOException e) { e.printStackTrace();
                } catch (JSONException e) { e.printStackTrace();
                } catch (Exception e){ e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"onFailure POST");
                t.printStackTrace();
            }
        });
    }

    private void uploadFile(String id, final File bleFile) {
        String recordingId = bleFile.getName().replace(".BLE","");
        Map<String, RequestBody> files = new HashMap<>();
        String key = String.format(Locale.getDefault(), "file\"; filename=\"%s", bleFile.getName());

        RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), bleFile);

        files.put(key, requestBody);

        Call<ResponseBody> call = cortriumAPI.request(id, recordingId, files);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG+"-Upload "+bleFile.getName(),response.message());
                try {
                    String info = response.body().string();
                    Log.d(TAG,info);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"FAILED");
                t.printStackTrace();
            }
        });

    }

    private void deleteRecording(String recordingId){
        Call<ResponseBody> call = cortriumAPI.deleteRecording(recordingId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG+" delete", "onResponse");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG+" delete", "onFailure");
                t.printStackTrace();
            }
        });
    }
}

