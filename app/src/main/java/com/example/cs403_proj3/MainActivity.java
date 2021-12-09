package com.example.cs403_proj3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Store> stores;
    ArrayList<StockedItem> masterList;
    final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    double userLat, userLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add some random stores - just until API works.
        //TODO Anthony G feel free to delete this once you're done with the API
        stores = new ArrayList<>();
        Store meijer = new Store( "Meijer", findAddress(43.48273485750115, -83.9825981015855),43.48273485750115, -83.9825981015855);
        Store bestBuy = new Store("Best Buy",findAddress(43.472831468931524, -83.97255798905496), 43.472831468931524, -83.97255798905496);
        Store meijer2 = new Store("Meijer", findAddress(43.58328401083199, -83.83878854560731),43.58328401083199, -83.83878854560731);
        Store staples = new Store("Staples",findAddress(43.623929085147914, -83.91255452520612), 43.623929085147914, -83.91255452520612);
        stores.add(meijer);
        stores.add(bestBuy);
        stores.add(meijer2);
        stores.add(staples);
        //add some random stock
        Item ps5 = new Item("Sony PlayStation 5", "Gaming console", 500.00);
        Item xboxx = new Item("Xbox Series X", "Gaming console", 500.0);
        Item stapler = new Item("Stapler idk", "??", 5.00);
        //and random stock to stores
        masterList = new ArrayList<>();
        masterList.add(new StockedItem(ps5, bestBuy, 10, LocalDateTime.now()));
        masterList.add(new StockedItem(xboxx, meijer, 1, LocalDateTime.now()));
        masterList.add(new StockedItem(stapler, staples, 105, LocalDateTime.now()));
        masterList.add(new StockedItem(xboxx, bestBuy, 13, LocalDateTime.now()));


        //checking permissions for using gps and get the location
        //this can't be in the maps activity bc it doesn't execute in time
        checkPermissions();
        getUserLocation();
    }

    public void launchMaps(View v){
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("stores", stores);
        i.putExtra("stock", masterList);
        i.putExtra("userLat", userLat);
        i.putExtra("userLon", userLon);
        startActivity(i);
    }

    private String findAddress(double lat, double lon) {
        //create geocoder. this doesn't work inside Store.java. there's probably a reason for that
        //but i do not know it
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
            return "Address Error";
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        //no sure if we need this data. keeping it just in case
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return address;
    }


    private void getUserLocation() {
        //GPS Stuff
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Gets last location from phone
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //Set lat and lon values to the location lat and long
                            userLat = location.getLatitude();
                            userLon = location.getLongitude();
                            Log.d("beata-debug", "Loc: "+userLat + " " + userLon);
                        }
                        else{
                            Log.d("beata-debug", "Could not get location");
                            //set to SVSU by default
                            userLat = 43.513583032256754;
                            userLon = -83.96106395172602;
                        }
                    }
                });
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("locdemo", "getLocation: permissions not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d("locdemo", "getLocation: permissions already granted");
        }
    }

}