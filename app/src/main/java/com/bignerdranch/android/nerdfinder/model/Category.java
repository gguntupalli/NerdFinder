package com.bignerdranch.android.nerdfinder.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id") private String mId;
    @SerializedName("name") private String mName;
    @SerializedName("icon") private Icon mIcon;
    @SerializedName("primary") private boolean mPrimary;
}
