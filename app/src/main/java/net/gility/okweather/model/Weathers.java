package net.gility.okweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Weathers {

    @SerializedName("HeWeather data service 3.0") @Expose
    public List<Weather> weatherList
            = new ArrayList<>();
}