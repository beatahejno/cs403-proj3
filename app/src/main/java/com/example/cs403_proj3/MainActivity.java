package com.example.cs403_proj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Store> stores;
    ArrayList<StockedItem> masterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add some random stores - just until API works.
        //TODO Anthony feel free to delete this once you're done with your part
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

    }

    public void launchMaps(View v){
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("stores", stores);
        i.putExtra("stock", masterList);
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
}