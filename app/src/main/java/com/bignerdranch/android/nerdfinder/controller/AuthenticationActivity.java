package com.bignerdranch.android.nerdfinder.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AuthenticationActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthenticationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
