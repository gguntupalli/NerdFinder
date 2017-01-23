package com.bignerdranch.android.nerdfinder.web;

import android.content.Context;
import android.util.Log;

import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gguntupalli on 23/01/17.
 */

public class DataManager {
    private static final String TAG = "DataManager";
    private static final String FOURSQUARE_ENDPOINT
            = "https://api.foursquare.com/v2/";
    private static final String CLIENT_ID
            = "2UALBKC00LORSNUOR1MECIAB0ROSXV21CXXS5WVMVNBCAQFU";
    private static final String CLIENT_SECRET
            = "GW42YNNXTEXQUNR5DY3IDSXIYNQHGDR4XK3C2HYUT4AQCUKX";
    private static final String FOURSQUARE_VERSION = "20150406";
    private static final String FOURSQUARE_MODE = "foursquare";
    private static final String TEST_LAT_LNG = "33.759,-84.332";

    protected static DataManager sDataManager;
    private static TokenStore sTokenStore;
    private Retrofit mRetrofit;
    private List<Venue> mVenueList;
    private List<VenueSearchListener> mSearchListenerList;

    public static DataManager get(Context context) {
        if (sDataManager == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(sRequestInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FOURSQUARE_ENDPOINT)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            TokenStore tokenStore = TokenStore.get(context);
            sDataManager = new DataManager(tokenStore, retrofit);
        }
        return sDataManager;
    }
    DataManager() {
        throw new InstantiationError("Default constructor called for singleton");
    }
    DataManager(TokenStore tokenStore, Retrofit retrofit) {
        sTokenStore = tokenStore;
        mRetrofit = retrofit;
        mSearchListenerList = new ArrayList<>();
    }

    private static Interceptor sRequestInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            HttpUrl url = chain.request().url().newBuilder()
                    .addQueryParameter("client_id", CLIENT_ID)
                    .addQueryParameter("client_secret", CLIENT_SECRET)
                    .addQueryParameter("v", FOURSQUARE_VERSION)
                    .addQueryParameter("m", FOURSQUARE_MODE)
                    .build();

            Request request = chain.request().newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        }
    };

    public void fetchVenueSearch(){
        VenueInterface venueInterface = mRetrofit.create(VenueInterface.class);

        venueInterface.venueSearch(TEST_LAT_LNG)
                .enqueue(new Callback<VenueSearchResponse>() {
                    @Override
                    public void onResponse(Call<VenueSearchResponse> call, retrofit2.Response<VenueSearchResponse> response) {
                        mVenueList = response.body().getVenueList();
                        notifySearchListeners();
                    }

                    @Override
                    public void onFailure(Call<VenueSearchResponse> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch venue search", t);
                    }
                });
    }

    public List<Venue> getVenueList(){
        return mVenueList;
    }

    public void addVenueSearchListener(VenueSearchListener listener){
        mSearchListenerList.add(listener);
    }

    public void removeVenueSearchListener(VenueSearchListener listener){
        mSearchListenerList.remove(listener);
    }

    private void notifySearchListeners() {
        for (VenueSearchListener listener : mSearchListenerList) {
            listener.onVenueSearchFinished();
        }
    }
}
