package com.example.cs403_proj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //this is where we'd show all of the stock of that store.
        //the maps activity will launch this screen
        Store thisStore = (Store) getIntent().getSerializableExtra("store");


    }

    public void launchMap(View v){
        finish();
    }
}