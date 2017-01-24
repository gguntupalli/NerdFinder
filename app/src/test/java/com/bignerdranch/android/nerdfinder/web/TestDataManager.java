package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.model.TokenStore;

import retrofit2.Retrofit;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by gguntupalli on 24/01/17.
 */

public class TestDataManager extends DataManager {
    public static DataManager get(TokenStore tokenStore, Retrofit retrofit,
                                  Retrofit autheticatedRetrofit) {
        if(sDataManager == null) {
            sDataManager = new TestDataManager(tokenStore, retrofit, autheticatedRetrofit);
        }

        return sDataManager;
    }

    TestDataManager(TokenStore tokenStore, Retrofit retrofit, Retrofit authenticatedRetrofit) {
        super(tokenStore, retrofit, authenticatedRetrofit);
    }

    public static void reset() {
        sDataManager = null;
    }

    @Override
    Scheduler getSubscribeOnScheduler() {
        return Schedulers.immediate();
    }

    @Override
    Scheduler getObserveOnScheduler() {
        return Schedulers.immediate();
    }
}
