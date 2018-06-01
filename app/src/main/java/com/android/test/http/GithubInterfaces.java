package com.android.test.http;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * des:
 * author: libingyan
 * Date: 18-5-31 20:29
 */
public interface GithubInterfaces {

    String API_URL = "https://api.github.com";

    /**
     *
     * @param owner
     * @param repo
     * @return
     */
    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner, @Path("repo") String repo);
}
