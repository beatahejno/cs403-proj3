package com.example.cs403_proj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class StoreTemp extends AppCompatActivity {
    RecyclerView lstStores;
    ArrayList<Store> list;
    StoreAdaptor adaptor;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);
        lstStores = findViewById(R.id.lstStores);
        search = findViewById(R.id.txtStoreSearch);
        list = new ArrayList<>();
        adaptor = new StoreAdaptor(list);
        lstStores.setAdapter(adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstStores.setLayoutManager(layoutManager);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }); itemTouchHelper.attachToRecyclerView(lstStores);

    }

    class StoreAdaptor extends RecyclerView.Adapter<StoreAdaptor.StoreHolder> {
        ArrayList<Store> stores;

        public StoreAdaptor(ArrayList<Store> stores) {
            this.stores = stores;
        }

        @NonNull
        @Override
        public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            return new StoreTemp.StoreAdaptor.StoreHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
            Store s = stores.get(position);

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
                name = findViewById(R.id.txtStoreName);
                address = findViewById(R.id.txtStoreAddress);
                location = findViewById(R.id.txtStoreLoc);
                select = findViewById(R.id.btnLocateStore);
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO Call Map to store location
                    }
                });
            }
        }
    }
}