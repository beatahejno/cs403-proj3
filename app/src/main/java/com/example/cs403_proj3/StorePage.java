package com.example.cs403_proj3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class StorePage extends Fragment {
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

        //TODO fill list

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

    class StoreAdaptor extends RecyclerView.Adapter<StoreAdaptor.StoreHolder> {
        ArrayList<Store> stores;

        public StoreAdaptor(ArrayList<Store> stores) {
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
                select.setOnClickListener(view -> {
                    //TODO Call Map to store location
                });
            }
        }
    }
}