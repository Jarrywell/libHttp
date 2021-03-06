package com.android.test.http;

import com.android.test.http.retrofit.CacheRetrofit;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Github";

    private Button mBtnRetrofit, mBtnMockRetrofit, mBtnCacheRetrofit;
    private Handler mHandler;
    private Looper mLooper;

    private GithubService mGithubService;
    private MockGithubService mMockGithubService;
    private CacheGithubService mCacheGithubService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread thread = new HandlerThread("test-retrofit");
        thread.start();
        mLooper = thread.getLooper();
        mHandler = new Handler(mLooper);

        mBtnRetrofit = (Button) findViewById(R.id.id_btn_retrofit);
        mBtnRetrofit.setOnClickListener(this);

        mBtnMockRetrofit = (Button) findViewById(R.id.id_btn_mock_retrofit);
        mBtnMockRetrofit.setOnClickListener(this);

        mBtnCacheRetrofit = (Button) findViewById(R.id.id_btn_cache_retrofit);
        mBtnCacheRetrofit.setOnClickListener(this);

        mGithubService = new GithubService();
        mMockGithubService = new MockGithubService();
        mCacheGithubService = new CacheGithubService();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnRetrofit) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mGithubService.contributors("square", "retrofit");
                    } catch (Exception e) {
                        Log.i(TAG, "test()", e);
                    }
                }
            });
        } else if (v == mBtnMockRetrofit) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mMockGithubService.mockContributors();
                    } catch (Exception e) {
                        Log.i(TAG, "mockContributors()", e);
                    }
                }
            });
        } else if (v == mBtnCacheRetrofit) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCacheGithubService.cacheContributors();
                    } catch (Exception e) {
                        Log.i(TAG, "test()", e);
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLooper != null) {
            mLooper.quit();
            mLooper = null;
        }
        mHandler = null;
    }
}
