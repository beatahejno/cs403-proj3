package com.example.cs403_proj3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ItemPage extends Fragment {
    RecyclerView lstItems;
    ArrayList<Item> list;
    ItemAdaptor adaptor;
    EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_item_page,container,false);
        lstItems = view.findViewById(R.id.lstItems);
        search = view.findViewById(R.id.txtItemSearch);
        adaptor = new ItemAdaptor(list);
        lstItems.setAdapter(adaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
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


        return view;
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