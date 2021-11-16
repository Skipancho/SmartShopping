package com.smartshopping.product;

public class Product {
    private String pCode;
    private String pName;
    private int price;
    private int amount;

    public Product(String pCode, String pName, int price, int amount) {
        this.pCode = pCode;
        this.pName = pName;
        this.price = price;
        this.amount = amount;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
