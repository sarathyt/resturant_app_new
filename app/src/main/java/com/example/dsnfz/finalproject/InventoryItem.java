package com.example.dsnfz.finalproject;

import java.util.Iterator;

/**
 * Created by dsnfz on 2017/11/22.
 */

public class InventoryItem {
    private String name;
    private int quantity;

    public InventoryItem(){
        this(null,0);
    }
    public InventoryItem(String name,int quantity){
        this.name=name;
        this.quantity=quantity;
    }

    /**
     * getters:
     * @return
     */
    public String getName(){return name;}
    public int getQuantity(){return quantity;}

    /**
     * setters
     * @param name
     */
    public void setName(String name){this.name=name;}
    public void setQuantity(int quantity){this.quantity=quantity;}


}
