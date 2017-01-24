package com.bignerdranch.android.nerdfinder.controllers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.nerdfinder.BuildConfig;
import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.SynchronousExecutorService;
import com.bignerdranch.android.nerdfinder.controller.VenueListActivity;
import com.bignerdranch.android.nerdfinder.controller.VenueListFragment;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.bignerdranch.android.nerdfinder.web.DataManager;
import com.bignerdranch.android.nerdfinder.web.TestDataManager;
import com.bignerdranch.android.nerdfinder.web.VenueListDeserializer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by gguntupalli on 24/01/17.
 */


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, constants = BuildConfig.class)
public class VenueListFragmentTest {
    @Rule
    public WireMockRule mWireMockRule = new WireMockRule(1111);
    private String mEndpoint = "http://localhost:1111/";
    private DataManager mDataManager;
    private VenueListActivity mVenueListActivity;
    private VenueListFragment mVenueListFragment;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
        mDataManager = TestDataManager.get(tokenStore, retrofit, null);

        stubFor(get(urlMatching("/venues/search.*"))
        .willReturn(aResponse().withStatus(200).withBodyFile("search.json")));

        mVenueListActivity = Robolectric.buildActivity(VenueListActivity.class)
                .create().start().resume().get();
        mVenueListFragment = (VenueListFragment) mVenueListActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
    }
    @After
    public void tearDown() {
        TestDataManager.reset();
    }

    @Test
    public void activityListsVenuesReturnedFromSearch() {
        assertThat(mVenueListFragment, is(notNullValue()));

        RecyclerView venueRecyclerView = (RecyclerView) mVenueListFragment.getView()
                .findViewById(R.id.venueListRecyclerView);
        assertThat(venueRecyclerView, is(Matchers.notNullValue()));
        assertThat(venueRecyclerView.getAdapter().getItemCount(), is(2));

        venueRecyclerView.measure(0, 0);
        venueRecyclerView.layout(0, 0, 100, 1000);

        String bnrTitle = "BNR Intergalactic Headquarters";
        String rndTitle = "Ration and Dram";
        View firstVenueView = venueRecyclerView.getChildAt(0);
        TextView venueTitleTextView = (TextView) firstVenueView
                .findViewById(R.id.view_venue_list_VenueTitleTextView);
        assertThat(venueTitleTextView.getText(), is(bnrTitle));
        View secondVenueView = venueRecyclerView.getChildAt(1);
        TextView venueTitleTextView2 = (TextView) secondVenueView
                .findViewById(R.id.view_venue_list_VenueTitleTextView);
        assertThat(venueTitleTextView2.getText(), is(rndTitle));
    }


}