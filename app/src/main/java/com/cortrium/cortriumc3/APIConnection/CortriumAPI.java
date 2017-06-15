package com.cortrium.cortriumc3.APIConnection;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Kike Bodi on 06/06/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public interface CortriumAPI {
    String ENDPOINT = "http://recordingservice-dev.eu-central-1.elasticbeanstalk.com/api/";

    @GET("recordings/")
    Single<List<ECGRecording>> getRecordings();

    @GET("recordings/{id}/")
    Single<ECGRecording> getRecording(@Path("id") String id);

    @POST()
    Single<ResponseBody> saveRecording();

    /*@GET("/repos/{owner}/{repo}/issues")
    Single<List<GithubIssue>> getIssues(@Path("owner") String owner, @Path("repo") String repository);

    @POST
    Single<ResponseBody> postComment(@Url String url, @Body GithubIssue issue);*/
}
