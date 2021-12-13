package com.example.cs403_proj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemSelection extends AppCompatActivity {
    Item item;
    RequestQueue queue;
    ListView lstItems;
    ArrayList<Store> list;
    ItemStoreAdaptor adaptor;
    TextView name;
    TextView desc;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);
        item = (Item) getIntent().getSerializableExtra("selected");
        itemName = item.name;
        name = findViewById(R.id.txtItem);
        name.setText(itemName);
        desc = findViewById(R.id.txtItemDesc);
        desc.setText(item.description);
        lstItems = findViewById(R.id.lstItemStores);
        list = new ArrayList<>();
        adaptor = new ItemStoreAdaptor(list);
        String url = "https://fast-ocean-54669.herokuapp.com/item_stock/?format=api";
        queue = Volley.newRequestQueue(this);
        fetchData(url,queue);
    }

    private void fetchData(String url, RequestQueue queue) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null, response->{

            try {
                JSONArray results = response.getJSONArray("");
                for(int i=0;i<results.length();i++){
                    JSONObject obj = results.getJSONObject(i);

                    if(item.name.equals(obj.getJSONObject("item").getString("name"))) {
                        String storeName = obj.getJSONObject("store").getString("store_name");
                        String storeAddress = obj.getJSONObject("store").getString("address");
                        double storeLat = obj.getJSONObject("store").getDouble("lat");
                        double storeLon = obj.getJSONObject("store").getDouble("lon");
                        Store store = new Store(storeName,storeAddress,storeLat,storeLon);
                        list.add(store);
                        adaptor.notifyItemInserted(list.indexOf(store));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error->{
            Toast.makeText(this,"Could not get data...",Toast.LENGTH_LONG).show();
        });

        queue.add(request);
    }

    class ItemStoreAdaptor extends RecyclerView.Adapter<ItemStoreAdaptor.StoreHolder> {
        ArrayList<Store> stores;

        public ItemStoreAdaptor(ArrayList<Store> stores) {
            this.stores = stores;
        }

        @NonNull
        @Override
        public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            return new StoreHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
            Store s = stores.get(position);
            holder.name.setText(s.name);
            holder.address.setText(s.address);
            holder.location.setText("Lat: " + s.lat + " Long: " + s.lon);
            holder.select.setOnClickListener(view -> {
                Intent i = new Intent(getBaseContext(),MapsActivity.class);
                i.putExtra("userLat",s.lat);
                i.putExtra("userLon",s.lon);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return stores.size();
        }

        class StoreHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView address;
            TextView location;
            Button select;

            public StoreHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtStoreName);
                address = itemView.findViewById(R.id.txtStoreAddress);
                location = itemView.findViewById(R.id.txtStoreLoc);
                select = itemView.findViewById(R.id.btnLocateStore);
            }
        }
    }
}