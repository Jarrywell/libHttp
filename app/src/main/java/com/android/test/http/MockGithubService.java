package com.android.test.http;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.android.test.http.GithubInterfaces.API_URL;

/**
 * des:
 * author: libingyan
 * Date: 18-5-31 20:35
 */
public class MockGithubService {

    private static final String TAG = "Github";

    private MockGithubInterfaces mMockGithubInterfaces;
    private NetworkBehavior mNetworkBehavior;

    public MockGithubService() {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mNetworkBehavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
            .networkBehavior(mNetworkBehavior)
            .build();

        BehaviorDelegate<GithubInterfaces> delegate = mockRetrofit.create(GithubInterfaces.class);
        mMockGithubInterfaces = new MockGithubInterfaces(delegate);
    }

    public void mockContributors() throws IOException {
        printContributors(mMockGithubInterfaces, "square", "retrofit");
        printContributors(mMockGithubInterfaces, "square", "picasso");

        Log.i(TAG, "\"Adding more mock data...");
        mMockGithubInterfaces.addContributor("square", "retrofit", "Foo Bar", 61);
        mMockGithubInterfaces.addContributor("square", "picasso", "Kit Kat", 53);


        // Reduce the delay to make the next calls complete faster.
        mNetworkBehavior.setDelay(500, TimeUnit.MILLISECONDS);

        // Query for the contributors again so we can see the mock data that was added.
        printContributors(mMockGithubInterfaces, "square", "retrofit");
        printContributors(mMockGithubInterfaces, "square", "picasso");
    }

    private static void printContributors(GithubInterfaces gitHub, String owner, String repo)
        throws IOException {
        Log.d(TAG, String.format("== Contributors for %s/%s ==", owner, repo));
        Call<List<Contributor>> contributors = gitHub.contributors(owner, repo);
        for (Contributor contributor : contributors.execute().body()) {
            Log.i(TAG, contributor.login + " (" + contributor.contributions + ")");
        }
    }


    static final class MockGithubInterfaces implements GithubInterfaces {
        private final BehaviorDelegate<GithubInterfaces> delegate;
        private final Map<String, Map<String, List<Contributor>>> ownerRepoContributors;

        MockGithubInterfaces(BehaviorDelegate<GithubInterfaces> delegate) {
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
            return delegate.returningResponse(response).contributors(owner, repo);
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
