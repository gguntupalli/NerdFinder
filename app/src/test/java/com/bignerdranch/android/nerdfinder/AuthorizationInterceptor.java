package com.bignerdranch.android.nerdfinder;

import com.bignerdranch.android.nerdfinder.exception.UnauthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by gguntupalli on 24/01/17.
 */

public class AuthorizationInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new UnauthorizedException();
        }

        return response;
    }
}
