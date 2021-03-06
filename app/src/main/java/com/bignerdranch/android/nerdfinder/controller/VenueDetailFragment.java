package com.bignerdranch.android.nerdfinder.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;

public class VenueDetailFragment extends Fragment {
    private static final String ARG_VENUE_ID = "VenueDetailFragment.VenueId";

    private String mVenueId;
    private Venue mVenue;
    private TextView mVenueNameTextView;
    private TextView mVenueAddressTextView;
    private Button mCheckInButton;
    private TokenStore mTokenStore;

    public static VenueDetailFragment newInstance(String venueId) {
        VenueDetailFragment fragment = new VenueDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_VENUE_ID, venueId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTokenStore = TokenStore.get(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
        mVenueNameTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_name_text_view);
        mVenueAddressTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_address_text_view);
        mCheckInButton = (Button) view.findViewById(R.id.fragment_venue_detail_check_in_button);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mVenueId = getArguments().getString(ARG_VENUE_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private View.OnClickListener mCheckInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
}
