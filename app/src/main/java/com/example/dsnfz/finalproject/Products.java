package com.example.dsnfz.finalproject;

import android.graphics.drawable.Drawable;

/**
 * Created by myronsong on 11/26/17.
 */

public class Products {


   private int id;
    private String shopName;//店名
    private double price;//单价
    private String goods;//货物名称
    private Drawable picture;//货物图片
    private String type;//货物类型
    private String createtime;
//
//    public Products(int id, String goods, int price, Drawable picture) {
//        this.id = id;
//        this.goods = goods;
//        this.picture=picture;
//        this.price=price;
//    }
//    public String getGoods(){
//        return goods;
//    }
//    public int getId(){
//        return id;
//    }
//    public Drawable getPicture(){
//        return picture;
//    }
//    private int getPrice(){
//        return price;
//    }
//
//}
    /**
     * 商品数目
     */
   private int number;

    private String money;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public String getShopName() {
//        return shopName;
//    }
//
//    public void setShopName(String shopName) {
//        this.shopName = shopName;
//    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

}


