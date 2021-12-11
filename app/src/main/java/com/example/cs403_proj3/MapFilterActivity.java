package com.example.cs403_proj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.widget.Filter;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MapFilterActivity extends AppCompatActivity {

    SearchView filterSearch;
    RecyclerView rclFilteredItems;
    RecyclerView rclSearchResults;
    ArrayList<Item> filteredItems;
    ArrayList<Item> unfilteredItems;
    ArrayList<Item> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        filterSearch = findViewById(R.id.filterSearch);
        filterSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        Intent intent = getIntent();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                doSearch(intent.getStringExtra(SearchManager.QUERY));
            }
        }

        rclFilteredItems = findViewById(R.id.rclFilteredItems);
        rclSearchResults = findViewById(R.id.rclSearchResults);

        filteredItems = new ArrayList<>();
        unfilteredItems = new ArrayList<>();
        searchResults = unfilteredItems;
        filteredItems.add(new Item("TestItem", "", 1));
        unfilteredItems.add(new Item("a", "", 1));
        unfilteredItems.add(new Item("GPU !0940", "", 1));

        FilterAdapter adapter = new FilterAdapter(filteredItems, unfilteredItems, searchResults, false);
        rclFilteredItems.setAdapter(adapter);
        rclFilteredItems.setLayoutManager(new LinearLayoutManager(this));

        FilterAdapter searchAdapter = new FilterAdapter(filteredItems, unfilteredItems, searchResults, true);
        rclSearchResults.setAdapter(searchAdapter);
        rclSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private void doSearch(String query) {
        searchResults.clear();
        for (Item item : unfilteredItems) {
            if (query.trim().equals(""))
                searchResults.add(item);
            else if (item.name.contains(query))
                searchResults.add(item);
        }
    }
}

class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    ArrayList<Item> filteredItems;
    ArrayList<Item> unfilteredItems;
    ArrayList<Item> results;
    Boolean isSearchResults;

    public FilterAdapter(ArrayList<Item> filteredItems, ArrayList<Item> unfilteredItems, ArrayList<Item> results, boolean isSearchResults) {
        this.filteredItems = filteredItems;
        this.unfilteredItems = unfilteredItems;
        this.results = results;
        this.isSearchResults = isSearchResults;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilterViewHolder holder;
        if (isSearchResults) {
            holder = new FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false));
        } else {
            holder = new FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Item item;

        if (isSearchResults) {
            item = results.get(position);

            holder.btnAddFilter.setOnClickListener(v -> {
                Item result = results.get(position);
                filteredItems.add(result);
                unfilteredItems.remove(result);
                results.remove(result);
                this.notifyDataSetChanged();
            });
        } else {
            item = filteredItems.get(position);

            holder.btnRemoveFilter.setOnClickListener(view -> {
                unfilteredItems.add(filteredItems.get(position));
                filteredItems.remove(position);
                this.notifyDataSetChanged();
            });
        }

        if (item != null) {
            if (item.name != null)
                holder.txtItemName.setText(item.name);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemName;
        Button btnRemoveFilter;
        Button btnAddFilter;

        public FilterViewHolder(View view) {
            super(view);
            this.txtItemName = view.findViewById(R.id.txtItemName);
            if (isSearchResults) {
                this.btnAddFilter = view.findViewById(R.id.btnAddFilter);
            } else {
                this.btnRemoveFilter = view.findViewById(R.id.btnRemoveFilter);

            }
        }
    }
}

