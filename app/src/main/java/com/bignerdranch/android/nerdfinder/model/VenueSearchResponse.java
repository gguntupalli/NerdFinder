package com.bignerdranch.android.nerdfinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gguntupalli on 23/01/17.
 */

public class VenueSearchResponse {
    @SerializedName("venues") private List<Venue> mVenueList;

    public List<Venue> getVenueList(){
        return mVenueList;
    }
}
