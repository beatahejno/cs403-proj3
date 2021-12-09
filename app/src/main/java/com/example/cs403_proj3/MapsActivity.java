package com.example.cs403_proj3;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cs403_proj3.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    double userLat, userLon;
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
        userLat = getIntent().getDoubleExtra("userLat", 0.0);
        userLon = getIntent().getDoubleExtra("userLon", 0.0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //put the plus and minus zoom icons on the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //move the camera to where the user is
        LatLng userCoords = new LatLng(userLat, userLon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, 10));

        //Add a marker for the user
        LatLng userCoordinates = new LatLng(userLat, userLon);
        mMap.addMarker(new MarkerOptions()
                .position(userCoordinates)
                .title("This is you!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .infoWindowAnchor(0.5f, 0.5f));

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
            try {
                Store temp = (Store) marker.getTag();
                Intent i = new Intent(getApplicationContext(), StoreActivity.class);
                i.putExtra("store", temp);
                i.putExtra("stock", masterList);
                startActivity(i);
            } catch (Exception e){
                //do nothing. this happens if you click on the "This is you!" marker
            }
        });

    }

    public void launchMain(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}