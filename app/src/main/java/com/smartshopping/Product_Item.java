package com.smartshopping;

public class Product_Item extends Product {
    private String where = "non";
    public Product_Item(String pCode, String pName, int price, int amount, String where) {
        super(pCode, pName, price, amount);
        this.where = where;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
