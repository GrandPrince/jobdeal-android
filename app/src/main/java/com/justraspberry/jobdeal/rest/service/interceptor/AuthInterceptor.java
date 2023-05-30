package com.justraspberry.jobdeal.rest.service.interceptor;

import com.justraspberry.jobdeal.core.SP;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        // Request customization: add request headers
        if(SP.getInstance().getJWT()!=null) {
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", SP.getInstance().getJWT()); // <-- this is the important line
            Request request = requestBuilder.build();
            return chain.proceed(request);
        } else {
            return chain.proceed(original);
        }
    }
}
