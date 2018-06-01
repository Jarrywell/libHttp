package com.android.test.http;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.test.http.GithubInterfaces.API_URL;

/**
 * des:
 * author: libingyan
 * Date: 18-5-31 20:34
 */
public class GithubService {

    private static final String TAG = "Github";

    private GithubInterfaces mInterfaces;

    public GithubService() {

        mInterfaces = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GithubInterfaces.class);
    }

    public void contributors(String owner, String repo) throws IOException {
        Call<List<Contributor>> call =  mInterfaces.contributors(owner, repo);

        List<Contributor> contributors = call.execute().body();
        for (Contributor contributor : contributors) {
            Log.i(TAG, contributor.login + " (" + contributor.contributions + ")");
        }
    }
}
