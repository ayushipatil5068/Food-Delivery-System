package com.cyberweb.fdms.model;

import com.cyberweb.fdms.entity.MenuItem;

public class CartItem {

    private MenuItem item;
    private int quantity;

    public CartItem(MenuItem item) {
        this.item = item;
        this.quantity = 1;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }
}
