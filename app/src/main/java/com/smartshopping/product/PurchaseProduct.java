package com.smartshopping.product;

import com.smartshopping.product.Product;

public class PurchaseProduct extends Product {
    private int pKey;
    private String bDate;
    private int isReviewed;

    public PurchaseProduct(String pCode, String pName, int price, int amount, String bDate, int isReviewed, int pKey){
        super(pCode,pName,price,amount);
        this.bDate = bDate;
        this.isReviewed = isReviewed;
        this.pKey= pKey;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public int getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(int isReviewed) {
        this.isReviewed = isReviewed;
    }

    public int getpKey() {
        return pKey;
    }

    public void setpKey(int pKey) {
        this.pKey = pKey;
    }
}
