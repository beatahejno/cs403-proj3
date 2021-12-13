package com.example.cs403_proj3;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Store implements Serializable {
    String name, address;
    double lat, lon;
    int id;
    boolean filtered;

    public Store(String name, String address, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        filtered = true;
    }
    public Store(int id, String name, String address, double lat, double lon){
        this(name, address, lat, lon);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return id == store.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
