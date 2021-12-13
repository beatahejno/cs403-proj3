package com.example.cs403_proj3;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {
    String name, description;
    double price;
    int id;

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Item(int id, String name, String description, double price) {
        this(name, description, price);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
