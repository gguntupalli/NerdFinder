package com.bignerdranch.android.nerdfinder.controllers;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;

import com.bignerdranch.android.nerdfinder.AuthorizationInterceptor;
import com.bignerdranch.android.nerdfinder.BuildConfig;
import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.SynchronousExecutorService;
import com.bignerdranch.android.nerdfinder.controller.VenueDetailActivity;
import com.bignerdranch.android.nerdfinder.controller.VenueDetailFragment;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.bignerdranch.android.nerdfinder.web.DataManager;
import com.bignerdranch.android.nerdfinder.web.TestDataManager;
import com.bignerdranch.android.nerdfinder.web.VenueListDeserializer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;


/**
 * Created by gguntupalli on 24/01/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, constants = BuildConfig.class)
public class VenueDetailFragmentTest {
    @Rule
    public WireMockRule mWireMockRule = new WireMockRule(1111);
    private String mEndpoint = "http://localhost:1111/";
    private DataManager mDataManager;
    private VenueDetailFragment mVenueDetailFragment;
    private VenueDetailActivity mVenueDetailActivity;

    @Before
    public void setUp() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VenueSearchResponse.class,
                        new VenueListDeserializer())
                .create();
        ExecutorService executorService = new SynchronousExecutorService();
        OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(executorService))
                .build();
        Retrofit basicRetrofit = new Retrofit.Builder()
                .baseUrl(mEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        OkHttpClient authenticatedClient = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(executorService))
                .addInterceptor(new AuthorizationInterceptor())
                .build();
        Retrofit authenticatedRetrofit = new Retrofit.Builder()
                .baseUrl(mEndpoint)
                .client(authenticatedClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
        tokenStore.setAccessToken("bogus token for testing");
        TestDataManager.reset();
        mDataManager = TestDataManager.get(tokenStore,
                basicRetrofit, authenticatedRetrofit);

        stubFor(get(urlMatching("/venues/search.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("search.json")));
        mDataManager.fetchVenueSearch();
    }
    @After
    public void tearDown() {
        TestDataManager.reset();
    }

    @Test
    public void toastShownOnSuccessfulCheckIn() {
        stubFor(post(urlMatching("/checkins/add.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        String venueId = "527c1d4f11d20f41ba39fc01";
        Intent detailIntent = mVenueDetailActivity.newIntent(RuntimeEnvironment.application, venueId);
        mVenueDetailActivity = Robolectric.buildActivity(VenueDetailActivity.class)
                .withIntent(detailIntent)
                .create().start().resume().get();
        mVenueDetailFragment = (VenueDetailFragment) mVenueDetailActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        Button checkInButton = (Button) mVenueDetailFragment.getView()
                .findViewById(R.id.fragment_venue_detail_check_in_button);

        checkInButton.performClick();

        String expectedToastText = mVenueDetailActivity
                .getString(R.string.successful_check_in_message);
        assertThat(ShadowToast.getTextOfLatestToast(), is(expectedToastText));
    }

    @Test
    public void errorDialogShownOnUnauthorizedException() {
        stubFor(post(urlMatching("/checkins/add.*"))
                .willReturn(aResponse()
                        .withStatus(401)));

        String venueId = "527c1d4f11d20f41ba39fc01";
        Intent detailIntent = mVenueDetailActivity.newIntent(RuntimeEnvironment.application, venueId);
        mVenueDetailActivity = Robolectric.buildActivity(VenueDetailActivity.class)
                .withIntent(detailIntent)
                .create().start().resume().get();
        mVenueDetailFragment = (VenueDetailFragment) mVenueDetailActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        Button checkInButton = (Button) mVenueDetailFragment.getView()
                .findViewById(R.id.fragment_venue_detail_check_in_button);

        checkInButton.performClick();

        ShadowLooper.idleMainLooper();
        AlertDialog errorDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(errorDialog, is(notNullValue()));

        ShadowAlertDialog alertDialog = shadowOf(errorDialog);

        String expectedDialogTitle = mVenueDetailActivity
                .getString(R.string.expired_token_dialog_title);
        String expectedDialogMessage = mVenueDetailActivity
                .getString(R.string.expired_token_dialog_message);
        assertThat(alertDialog.getTitle(), is(expectedDialogTitle));
        assertThat(alertDialog.getMessage(), is(expectedDialogMessage));
    }

    @Test
    public void errorDialogNotShownOnDifferentException() {
        stubFor(post(urlMatching("/checkins/add.*"))
                .willReturn(aResponse()
                        .withStatus(500)));

        String venueId = "527c1d4f11d20f41ba39fc01";
        Intent detailIntent = mVenueDetailActivity.newIntent(RuntimeEnvironment.application, venueId);
        mVenueDetailActivity = Robolectric.buildActivity(VenueDetailActivity.class)
                .withIntent(detailIntent)
                .create().start().resume().get();
        mVenueDetailFragment = (VenueDetailFragment) mVenueDetailActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        Button checkInButton = (Button) mVenueDetailFragment.getView()
                .findViewById(R.id.fragment_venue_detail_check_in_button);

        checkInButton.performClick();

        ShadowLooper.idleMainLooper();
        AlertDialog errorDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(errorDialog, is(nullValue()));
    }
}
