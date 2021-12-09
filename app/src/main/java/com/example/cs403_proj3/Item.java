package com.example.cs403_proj3;

import java.io.Serializable;

public class Item implements Serializable {
    String name, description;
    double price;

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }


}
