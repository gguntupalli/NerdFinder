package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by gguntupalli on 23/01/17.
 */

public class VenueListDeserializer implements JsonDeserializer<VenueSearchResponse> {
    @Override
    public VenueSearchResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
        JsonElement responseElement = json.getAsJsonObject().get("response");
        return new Gson().fromJson(responseElement, VenueSearchResponse.class);
    }
}
