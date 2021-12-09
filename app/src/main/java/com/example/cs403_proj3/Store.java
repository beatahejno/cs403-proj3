package com.example.cs403_proj3;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Store implements Serializable {
    String name, address;
    double lat, lon;

    public Store(String name, String address, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }
}
