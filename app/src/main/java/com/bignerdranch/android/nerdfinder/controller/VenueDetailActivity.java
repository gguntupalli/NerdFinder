package com.bignerdranch.android.nerdfinder.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.nerdfinder.R;

public class VenueDetailActivity extends AppCompatActivity {
    private static final String ARG_VENUE_ID = "VenueDetailActivity.VenueId";


    public static Intent newIntent(Context context, String venueId) {
        Intent intent = new Intent(context, VenueDetailActivity.class);
        intent.putExtra(ARG_VENUE_ID, venueId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String venueId = getIntent().getStringExtra(ARG_VENUE_ID);
        setContentView(R.layout.activity_venue_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, VenueDetailFragment.newInstance(venueId))
                    .commit();
        }
    }
}
