package com.example.cs403_proj3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    volatile ArrayList<Store> stores;
    volatile ArrayList<StockedItem> masterList;
    volatile HashMap<Integer, Item> items;
    final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    double userLat, userLon;
    SharedPreferences sharedPref;
    volatile static JSONObject response;
    TextView txtWelcome ;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref=getSharedPreferences("LOGIN_APP", Context.MODE_PRIVATE);

        txtWelcome = findViewById(R.id.txtWelcome);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //find if logged in, if not send to logout
        if (!sharedPref.getBoolean("login",false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            txtWelcome.setText("Welcome " + sharedPref.getString("username", ""));
        }
        //add some random stores - just until API works.
        //TODO Anthony G feel free to delete this once you're done with the API

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_APP", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth-token", "");
        Log.d("token", token);

        stores = new ArrayList<>();
        HashMap<Integer, Store> storeMap = new HashMap<>();
        items = new HashMap<>();
        masterList = new ArrayList<>();

        JsonArrayRequest stockedItemsRequest = new JsonArrayRequest(Request.Method.GET, "https://fast-ocean-54669.herokuapp.com/item_stock/",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject stockedItem = response.getJSONObject(i);
                        Item item = items.get(stockedItem.getJSONObject("item").getInt("id"));
                        Store store = storeMap.get(stockedItem.getJSONObject("store").getInt("id"));

                        StockedItem si = new StockedItem(item, store, stockedItem.getInt("stock"),LocalDateTime.parse(stockedItem.getString("last_update"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        masterList.add(si);
                    } catch (JSONException e) {
                        Log.d("ItemRequest", e.getLocalizedMessage());
                    }
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };

        JsonArrayRequest itemsRequest = new JsonArrayRequest(Request.Method.GET, "https://fast-ocean-54669.herokuapp.com/items/",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        items.put(item.getInt("id"), new Item(item.getInt("id"),item.getString("item_name"), item.getString("item_description"), item.getDouble("item_price")));
                    } catch (JSONException e) {
                        Log.d("ItemRequest", e.getLocalizedMessage());
                    }
                }
                RequestManager.getInstance(getApplicationContext()).addToRequestQueue(stockedItemsRequest);
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };

        JsonArrayRequest storesRequest = new JsonArrayRequest(Request.Method.GET, "https://fast-ocean-54669.herokuapp.com/stores/",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject store = response.getJSONObject(i);
                        Store s = new Store(store.getInt("id"),store.getString("store_name"), store.getString("address"), store.getDouble("lat"), store.getDouble("lon"));
                        stores.add(s);
                        storeMap.put(s.id, s);
                    } catch (JSONException e) {
                        Log.d("StoresRequest", e.getLocalizedMessage());
                    }
                }
                RequestManager.getInstance(getApplicationContext()).addToRequestQueue(itemsRequest);
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        RequestManager.getInstance(this).addToRequestQueue(storesRequest);

        //add some random stock
        Item ps5 = new Item("Sony PlayStation 5", "Gaming console", 500.00);
        Item xboxx = new Item("Xbox Series X", "Gaming console", 500.0);
        Item stapler = new Item("Stapler idk", "??", 5.00);
        //and random stock to stores

        //checking permissions for using gps and get the location
        //this can't be in the maps activity bc it doesn't execute in time
        checkPermissions();
        getUserLocation();
    }

    public void launchMaps(View v) {
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("stores", stores);
        i.putExtra("stock", masterList);
        i.putExtra("userLat", userLat);
        i.putExtra("userLon", userLon);
        i.putExtra("items", items);
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
                            Log.d("beata-debug", "Loc: " + userLat + " " + userLon);
                        } else {
                            Log.d("beata-debug", "Could not get location");
                            //set to SVSU by default
                            userLat = 43.513583032256754;
                            userLon = -83.96106395172602;
                        }
                    }
                });
    }

    public void logout(View view) {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("auth-token", "");
        prefEditor.putBoolean("login", false);
        prefEditor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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