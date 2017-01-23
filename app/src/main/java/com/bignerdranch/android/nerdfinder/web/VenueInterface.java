package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gguntupalli on 23/01/17.
 */

public interface VenueInterface {
    @GET("venues/search")
    Call<VenueSearchResponse> venueSearch(@Query("ll") String latLngString);

    @FormUrlEncoded
    @POST("checkins/add")
    Call<Object> venueCheckin(@Field("venueId") String venueId);
}
