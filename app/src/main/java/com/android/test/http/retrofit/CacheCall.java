package com.android.test.http.retrofit;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * des:
 * author: libingyan
 * Date: 18-6-1 15:24
 */
public class CacheCall<T> implements Call<T> {

    final Call<T> delegate;

    public CacheCall(Call<T> call) {
        this.delegate = call;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // We are a final type & this saves clearing state.
    @Override public Call<T> clone() {
        return new CacheCall<>(delegate.clone());
    }
    @Override
    public Response<T> execute() throws IOException {
        return null;
    }

    @Override
    public void enqueue(Callback<T> callback) {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }


    @Override
    public Request request() {
        return delegate.request();
    }
}
