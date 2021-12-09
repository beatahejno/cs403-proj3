package com.example.cs403_proj3;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StockedItem implements Serializable {
    Item item;
    Store store;
    int quantity;
    LocalDateTime dateUpdated;

    public StockedItem(Item item, Store store, int quantity, LocalDateTime dateUpdated) {
        this.item = item;
        this.store = store;
        this.quantity = quantity;
        this.dateUpdated = dateUpdated;
    }
}
