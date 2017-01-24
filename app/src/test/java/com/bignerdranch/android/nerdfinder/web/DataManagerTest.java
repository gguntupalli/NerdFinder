package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.exception.UnauthorizedException;
import com.bignerdranch.android.nerdfinder.listener.VenueCheckInListener;
import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.bignerdranch.android.nerdfinder.web.DataManager;
import com.bignerdranch.android.nerdfinder.web.TestDataManager;
import com.bignerdranch.android.nerdfinder.web.VenueInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gguntupalli on 24/01/17.
 */

@RunWith(JUnit4.class)
public class DataManagerTest {
    @Captor
    private ArgumentCaptor<Callback<VenueSearchResponse>> mSearchCaptor;
    private DataManager mDataManager;
    @Mock
    private static Retrofit mRetrofit;
    @Mock
    private static Retrofit mAuthenticatedRetrofit;
    @Mock
    private TokenStore mTokenStore;
    @Mock
    private static VenueInterface mVenueInterface;
    @Mock
    private static VenueSearchListener mVenueSearchListener;
    @Mock
    private static VenueCheckInListener mVenueCheckinListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mDataManager = TestDataManager.get(mTokenStore, mRetrofit, mAuthenticatedRetrofit);

        when(mRetrofit.create(VenueInterface.class))
                .thenReturn(mVenueInterface);
        when(mAuthenticatedRetrofit.create(VenueInterface.class))
                .thenReturn(mVenueInterface);

        mDataManager.addVenueSearchListener(mVenueSearchListener);
        mDataManager.addVenueCheckInListener(mVenueCheckinListener);
    }

    @After
    public void tearDown() {
        //clear DataManager state in between tests
        reset(mRetrofit, mAuthenticatedRetrofit, mVenueInterface, mVenueSearchListener);
        mDataManager.removeVenueSearchListener(mVenueSearchListener);
        mDataManager.removeVenueCheckInListener(mVenueCheckinListener);
        TestDataManager.reset();
    }

    @Test
    public void searchListenerTriggeredOnSuccessfulSearch() {
        Call<VenueSearchResponse> responseCall = mock(Call.class);
        when(mVenueInterface.venueSearch(anyString())).thenReturn(responseCall);

        mDataManager.fetchVenueSearch();

        verify(responseCall).enqueue(mSearchCaptor.capture());

        VenueSearchResponse venueSearchResponse = mock(VenueSearchResponse.class);
        Response<VenueSearchResponse> response = Response.success(venueSearchResponse);

        mSearchCaptor.getValue().onResponse(responseCall, response);

        verify(mVenueSearchListener).onVenueSearchFinished();
    }

    @Test
    public void venueSearchListSavedOnSuccessfulSearch() {
        Call<VenueSearchResponse> responseCall = mock(Call.class);
        when(mVenueInterface.venueSearch(anyString())).thenReturn(responseCall);

        mDataManager.fetchVenueSearch();

        verify(responseCall).enqueue(mSearchCaptor.capture());

        String firstVenueName = "Cool first venue";
        Venue firstVenue = mock(Venue.class);
        when(firstVenue.getName()).thenReturn(firstVenueName);

        String secondVenueName = "awesome second venue";
        Venue secondVenue = mock(Venue.class);
        when(secondVenue.getName()).thenReturn(secondVenueName);

        List<Venue> venueList = new ArrayList<>();
        venueList.add(firstVenue);
        venueList.add(secondVenue);

        VenueSearchResponse venueSearchResponse = mock(VenueSearchResponse.class);
        when(venueSearchResponse.getVenueList()).thenReturn(venueList);
        Response<VenueSearchResponse> response = Response.success(venueSearchResponse);

        mSearchCaptor.getValue().onResponse(responseCall, response);
        List<Venue> dataManagerVenueList = mDataManager.getVenueList();

        assertThat(dataManagerVenueList, equalTo(venueList));
    }

    @Test
    public void checkInListenerTriggeredOnSuccessfulSearch() {
        Observable<Object> successObservable = Observable.just(new Object());
        when(mVenueInterface.venueCheckin(anyString())).thenReturn(successObservable);

        String fakeVenueId = "fakeVenueId";
        mDataManager.checkInToVenue(fakeVenueId);

        verify(mVenueCheckinListener).onVenueCheckInFinished();
    }

    @Test
    public void checkInListenerNotifiesTokenExpiredOnUnauthorizedException(){
        Observable<Object> unauthorizedObservable = Observable.error(new UnauthorizedException());
        when(mVenueInterface.venueCheckin(anyString())).thenReturn(unauthorizedObservable);

        mDataManager.checkInToVenue("fakeVenueId");

        verify(mVenueCheckinListener).onTokenExpired();
    }

    @Test
    public void checkInListenerDoesNotNotifyTokenExpiredOnPlainException() {
        Observable<Object> runtimeObservable = Observable.error(new RuntimeException());
        when(mVenueInterface.venueCheckin(anyString())).thenReturn(runtimeObservable);

        mDataManager.checkInToVenue("fakeVenueId");
        verify(mVenueCheckinListener, never()).onTokenExpired();
    }
}

