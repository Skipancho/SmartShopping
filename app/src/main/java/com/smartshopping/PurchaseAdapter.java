package com.smartshopping;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class PurchaseAdapter extends BaseAdapter {
    private Context context;
    private List<PurchaseProduct> productList;
    private AlertDialog dialog;

    public PurchaseAdapter(Context context, List<PurchaseProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.purchase_item, null);

        TextView dateText = (TextView) v.findViewById(R.id.bDateText);
        TextView nameText = (TextView) v.findViewById(R.id.pNameText);
        TextView priceText = (TextView) v.findViewById(R.id.p_PriceText);
        TextView amountText = (TextView) v.findViewById(R.id.p_amountText);

        dateText.setText(productList.get(i).getbDate());
        nameText.setText(productList.get(i).getpName());
        priceText.setText(productList.get(i).getPrice()+"원");
        amountText.setText(productList.get(i).getAmount()+"개");

        int isReviewed = productList.get(i).getIsReviewed();
        if(isReviewed == 1){
            v.setBackgroundColor(context.getResources().getColor(R.color.colorGray));
        }

        v.setTag(productList.get(i).getpCode());
        return v;
    }
}