package com.smartshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<SearchProduct> productList;

    public GridAdapter(Context context, List<SearchProduct> productList) {
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
        View v = View.inflate(context,R.layout.search_item_grid,null);
        TextView nameText = v.findViewById(R.id.name);
        TextView priceText = v.findViewById(R.id.price);
        ImageView imageView = v.findViewById(R.id.image);

        nameText.setText(productList.get(i).getpName());
        priceText.setText(productList.get(i).getPrice()+"원");
        final Bitmap[] bitmap = new Bitmap[getCount()];
        final String pCode = productList.get(i).getpCode();
        String url = "https://ctg1770.cafe24.com/SC/image/"+pCode+".jpg";
        new ImageLoadTask(url,imageView).execute();

        /*Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL("https://ctg1770.cafe24.com/SC/image/"+pCode+".jpg");
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();

                    InputStream inputStream = con.getInputStream();
                    bitmap[i] = BitmapFactory.decodeStream(inputStream);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            imageView.setImageBitmap(bitmap[i]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        v.setTag(productList.get(i).getpCode());
        /*TranslateAnimation translateAnimation = new TranslateAnimation(300, 0, 0, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(500);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        v.setAnimation(animation);*/

        return v;
    }

}
