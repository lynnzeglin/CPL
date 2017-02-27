package com.lynndroid.cpl.workflow;

import com.lynndroid.cpl.model.Library;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * (Retrofit) client to hit the web api to get list of libraries in city of Chicago
 */

public interface LibraryClient {

    String BASE_URL = "https://data.cityofchicago.org/";

    @GET("resource/x8fc-8rcq.json")
    Call<List<Library>> getLibraries();
}
