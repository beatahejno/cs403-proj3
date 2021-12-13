package com.example.cs403_proj3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StorePage extends Fragment {
    SharedPreferences preferences;
    RequestQueue queue;
    RecyclerView lstStores;
    ArrayList<Store> display;
    ArrayList<Store> list;
    StoreAdaptor adaptor;
    EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_store_page,container,false);
        lstStores = view.findViewById(R.id.lstStores);
        search = view.findViewById(R.id.txtStoreSearch);
        list = new ArrayList<>();
        adaptor = new StoreAdaptor(list);
        lstStores.setAdapter(adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        lstStores.setLayoutManager(layoutManager);

        //preferences = getContext().getSharedPreferences("LOGIN_APP", Context.MODE_PRIVATE);
        //String token = preferences.getString("auth-token",null);
        String url = "https://fast-ocean-54669.herokuapp.com/stores/";
        queue = Volley.newRequestQueue(getContext());
        fetchData(url,queue);

        display = new ArrayList<>();


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }); itemTouchHelper.attachToRecyclerView(lstStores);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                display = new ArrayList<>();
                for(Store store: list) {
                    if(store.name.contains(charSequence)) display.add(store);
                } adaptor.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void fetchData(String url, RequestQueue queue) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url,null, response->{

            try {
                for(int i=0;i<response.length();i++){
                    JSONObject obj = response.getJSONObject(i);

                    String name = obj.getString("store_name");
                    String address = obj.getString("address");
                    double lat = obj.getDouble("lat");
                    double lon = obj.getDouble("lon");
                    Store p = new Store(name,address,lat,lon);

                    list.add(p);
                }
                display.addAll(list);
                adaptor.notifyItemRangeInserted(0,display.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error->{
            Toast.makeText(getContext(),"Could not get data...",Toast.LENGTH_LONG).show();
        });

        queue.add(request);
    }

    class StoreAdaptor extends RecyclerView.Adapter<StoreAdaptor.StoreHolder> {
        ArrayList<Store> stores;

        public StoreAdaptor(ArrayList<Store> stores) {
            this.stores = stores;
        }

        @NonNull
        @Override
        public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store,parent,false);
            return new StoreHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
            Store s = stores.get(position);
            holder.name.setText(s.name);
            holder.address.setText(s.address);
            holder.location.setText("Lat: " + s.lat + " Long: " + s.lon);
            holder.select.setOnClickListener(view -> {
                Intent i = new Intent(getContext(),MapsActivity.class);
                i.putExtra("userLat",s.lat);
                i.putExtra("userLon",s.lon);
                i.putExtra("stores", stores);
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