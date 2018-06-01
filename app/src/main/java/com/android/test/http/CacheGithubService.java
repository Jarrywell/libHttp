package com.android.test.http;

import com.android.test.http.retrofit.CacheDelegate;
import com.android.test.http.retrofit.CacheRetrofit;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.test.http.GithubInterfaces.API_URL;

/**
 * des:
 * author: libingyan
 * Date: 18-6-1 15:13
 */
public class CacheGithubService {
    private static final String TAG = "Github";

    private CacheGithubInterfaces mCacheGithubInterfaces;
    private GithubInterfaces mGithubInterfaces;

    public CacheGithubService() {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mGithubInterfaces = retrofit.create(GithubInterfaces.class);

        CacheRetrofit cacheRetrofit = new CacheRetrofit.Builder(retrofit).build();

        CacheDelegate<GithubInterfaces> delegate = cacheRetrofit.create(GithubInterfaces.class);
        mCacheGithubInterfaces = new CacheGithubInterfaces(delegate);
    }

    public void cacheContributors() throws IOException {
        printContributors(mCacheGithubInterfaces, "square", "retrofit");
        printContributors(mCacheGithubInterfaces, "square", "picasso");

        Log.i(TAG, "\"Adding more mock data...");
        mCacheGithubInterfaces.addContributor("square", "retrofit", "Foo Bar", 61);
        mCacheGithubInterfaces.addContributor("square", "picasso", "Kit Kat", 53);


        // Query for the contributors again so we can see the mock data that was added.
        printContributors(mCacheGithubInterfaces, "square", "retrofit");
        printContributors(mCacheGithubInterfaces, "square", "picasso");
    }

    private void printContributors(GithubInterfaces gitHub, final String owner, final String repo)
        throws IOException {
        Call<List<Contributor>> contributors = gitHub.contributors(owner, repo);
        contributors.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call,
                Response<List<Contributor>> response) {
                Log.d(TAG, String.format("== Contributors for %s/%s ==", owner, repo));
                for (Contributor contributor : response.body()) {
                    Log.i(TAG, "cache: " + contributor.login + " (" + contributor.contributions + ")");
                }
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {

            }
        });

    }


    final class CacheGithubInterfaces implements GithubInterfaces {

        private final CacheDelegate<GithubInterfaces> delegate;
        private final Map<String, Map<String, List<Contributor>>> ownerRepoContributors;

        public CacheGithubInterfaces(CacheDelegate<GithubInterfaces> delegate) {
            this.delegate = delegate;
            this.ownerRepoContributors = new LinkedHashMap<>();

            // Seed some mock data.
            addContributor("square", "retrofit", "John Doe", 12);
            addContributor("square", "retrofit", "Bob Smith", 2);
            addContributor("square", "retrofit", "Big Bird", 40);
            addContributor("square", "picasso", "Proposition Joe", 39);
            addContributor("square", "picasso", "Keiser Soze", 152);

        }

        @Override
        public Call<List<Contributor>> contributors(String owner, String repo) {
            List<Contributor> response = Collections.emptyList();
            Map<String, List<Contributor>> repoContributors = ownerRepoContributors.get(owner);
            if (repoContributors != null) {
                List<Contributor> contributors = repoContributors.get(repo);
                if (contributors != null) {
                    response = contributors;
                }
            }
            /**
             * 判断缓存是否存在，如果没有缓存则发起网络请求
             */
            if (response != null && response.size() > 0) {
                return delegate.returningResponse(response).contributors(owner, repo);
            } else {
                return mGithubInterfaces.contributors(owner, repo);
            }
        }

        void addContributor(String owner, String repo, String name, int contributions) {
            Map<String, List<Contributor>> repoContributors = ownerRepoContributors.get(owner);
            if (repoContributors == null) {
                repoContributors = new LinkedHashMap<>();
                ownerRepoContributors.put(owner, repoContributors);
            }
            List<Contributor> contributors = repoContributors.get(repo);
            if (contributors == null) {
                contributors = new ArrayList<>();
                repoContributors.put(repo, contributors);
            }
            contributors.add(new Contributor(name, contributions));
        }
    }
}
