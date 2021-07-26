package com.smartshopping;

public class SearchProduct extends Product {
    private String info;

    public SearchProduct(String pCode, String pName, int price, int amount, String info) {
        super(pCode, pName, price, amount);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
