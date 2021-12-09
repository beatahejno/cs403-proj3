package com.example.cs403_proj3;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cs403_proj3.databinding.ActivityMapsBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ArrayList<Store> stores;
    ArrayList<StockedItem> masterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get the data from main
        stores = (ArrayList<Store>) getIntent().getSerializableExtra("stores");
        masterList = (ArrayList<StockedItem>) getIntent().getSerializableExtra("stock");

        //checking permissions for using gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("locdemo", "getLocation: permissions not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d("locdemo", "getLocation: permissions already granted");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng saginaw = new LatLng(43.41959658225769, -83.95095897448223);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saginaw, 10));

        // Add a marker for each store we have
        for (Store store : stores) {
            LatLng storeCoordinates = new LatLng(store.lat, store.lon);
            Marker storeMarker = mMap.addMarker(new MarkerOptions().position(storeCoordinates).title(store.name));
            storeMarker.setSnippet(store.address);
            storeMarker.setInfoWindowAnchor(0.5f, 0.5f);
            //associate this marker with that store object
            storeMarker.setTag(store);
        }

        //go to the selected store upon clicking the marker's title
        mMap.setOnInfoWindowClickListener(marker -> {
            //get the associated store object
            Store temp = (Store) marker.getTag();
            Intent i = new Intent(getApplicationContext(), StoreActivity.class);
            i.putExtra("store", temp);
            startActivity(i);
        });

    }

    public void launchMain(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}