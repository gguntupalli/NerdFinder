package com.bignerdranch.android.nerdfinder.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bignerdranch.android.nerdfinder.controller.VenueDetailActivity;
import com.bignerdranch.android.nerdfinder.model.Venue;

public class VenueHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private VenueView mVenueView;
    private Venue mVenue;

    public VenueHolder(View itemView) {
        super(itemView);

        mVenueView = (VenueView) itemView;
        mVenueView.setOnClickListener(this);
    }

    public void bindVenue(Venue venue) {
        mVenue = venue;
        mVenueView.setVenueTitle(mVenue.getName());
        mVenueView.setVenueAddress(mVenue.getFormattedAddress());
    }

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = VenueDetailActivity.newIntent(context, mVenue.getId());
        context.startActivity(intent);
    }
}

