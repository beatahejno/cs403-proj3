package com.example.cs403_proj3;


import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ItemPage extends Fragment {
    SharedPreferences preferences;
    RequestQueue queue;
    RecyclerView lstItems;
    ArrayList<Item> list;
    ArrayList<Item> display;
    ItemAdaptor adaptor;
    EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_item_page,container,false);
        lstItems = view.findViewById(R.id.lstItems);
        search = view.findViewById(R.id.txtItemSearch);
        adaptor = new ItemAdaptor(display);
        lstItems.setAdapter(adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        lstItems.setLayoutManager(layoutManager);
        queue = Volley.newRequestQueue(view.getContext());

        //TODO input items to list
        //preferences = getContext().getSharedPreferences("LOGIN_APP", Context.MODE_PRIVATE);
        //String token = preferences.getString("auth-token",null);
        String url = "https://fast-ocean-54669.herokuapp.com/items/?format=api";
        fetchData(url,queue);

        display = new ArrayList<>();
        display.addAll(list);
        adaptor.notifyItemRangeInserted(0,display.size());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }); itemTouchHelper.attachToRecyclerView(lstItems);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                display = new ArrayList<>();
                for(Item item: list) {
                    if(item.name.contains(charSequence)) display.add(item);
                    else if(item.description.contains(charSequence)) display.add(item);
                } adaptor.notifyDataSetChanged();
            }
        });


        return view;
    }

    private void fetchData(String url, RequestQueue queue) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,response->{

            try {
                JSONArray results = response.getJSONArray("");
                for(int i=0;i<results.length();i++){
                    JSONObject obj = results.getJSONObject(i);

                    String name = obj.getString("item_name");
                    String desc = obj.getString("item_description");
                    double price = obj.getDouble("item_price");
                    Item p = new Item(name,desc,price);
                    list.add(p);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error->{
            Toast.makeText(getContext(),"Could not get data...",Toast.LENGTH_LONG).show();
        });

        queue.add(request);
    }

    class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ItemHolder> {
        ArrayList<Item> items;

        public ItemAdaptor(ArrayList<Item> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            return new ItemAdaptor.ItemHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            Item i = items.get(position);
            holder.name.setText(i.name);
            holder.desc.setText(i.description);
            holder.price.setText("$" + i.price);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView desc;
            TextView price;
            TextView amount;
            Button select;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtItemName);
                desc = itemView.findViewById(R.id.txtItemDesc);
                price = itemView.findViewById(R.id.txtItemPrice);
                amount = itemView.findViewById(R.id.txtItemAmount);
                select = itemView.findViewById(R.id.btnItemSelect);
                select.setOnClickListener(view -> {
                    //TODO Expanded Item layout to search for stores
                });
            }
        }
    }
}