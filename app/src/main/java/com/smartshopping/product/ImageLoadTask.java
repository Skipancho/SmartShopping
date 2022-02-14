package com.smartshopping.product;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.smartshopping.main.MainActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String urlStr;
    private ImageView imageView;

    public ImageLoadTask(String urlStr, ImageView imageView){
        this.urlStr = urlStr;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        if (MainActivity.imageCache.containsKey(urlStr)){
            return MainActivity.imageCache.get(urlStr);
        }
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();

            InputStream inputStream = con.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

            MainActivity.imageCache.put(urlStr, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }

}