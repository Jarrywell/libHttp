package com.android.test.http.retrofit;

import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;

/**
 * des:
 * author: libingyan
 * Date: 18-6-1 15:06
 */
public class CacheRetrofit {
    private final Retrofit retrofit;


    private CacheRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public <T> CacheDelegate<T> create(Class<T> service) {
        return new CacheDelegate<>(retrofit, service);
    }

    public static final class Builder {
        private final Retrofit retrofit;

        @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
        public Builder(Retrofit retrofit) {
            if (retrofit == null) throw new NullPointerException("retrofit == null");
            this.retrofit = retrofit;
        }

        public CacheRetrofit build() {
            return new CacheRetrofit(retrofit);
        }
    }
}
