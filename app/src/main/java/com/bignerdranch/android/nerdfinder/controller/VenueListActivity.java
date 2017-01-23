package com.bignerdranch.android.nerdfinder.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bignerdranch.android.nerdfinder.R;

public class VenueListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, VenueListFragment.newInstance())
                    .commit();
        }
    }
}
