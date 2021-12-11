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

public class ItemTemp extends AppCompatActivity {
    RecyclerView lstItems;
    ArrayList<Item> list;
    ItemAdaptor adaptor;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_temp);
        lstItems = findViewById(R.id.lstItems);
        search = findViewById(R.id.txtItemSearch);
        adaptor = new ItemAdaptor(list);
        lstItems.setAdapter(adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstItems.setLayoutManager(layoutManager);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }); itemTouchHelper.attachToRecyclerView(lstItems);

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
            return new ItemTemp.ItemAdaptor.ItemHolder(v);
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
                name = findViewById(R.id.txtItemName);
                desc = findViewById(R.id.txtItemDesc);
                price = findViewById(R.id.txtItemPrice);
                amount = findViewById(R.id.txtItemAmount);
                select = findViewById(R.id.btnItemSelect);
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO Expanded Item layout to search for stores
                    }
                });
            }
        }
    }
}