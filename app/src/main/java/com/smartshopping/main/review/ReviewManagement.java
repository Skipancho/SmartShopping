package com.smartshopping.main.review;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smartshopping.main.MainActivity;
import com.smartshopping.main.review.MyReview;
import com.smartshopping.R;
import com.smartshopping.adapter.MyReviewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ReviewManagement extends Fragment {
    private List<MyReview> reviewList;
    private ListView review_lv;
    private MyReviewAdapter adapter;
    private TextView no_review_text;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        review_lv = getView().findViewById(R.id.my_review_list);
        no_review_text = getView().findViewById(R.id.no_review_text);
        reviewList = new ArrayList<>();
        adapter = new MyReviewAdapter(getActivity(),reviewList);
        review_lv.setAdapter(adapter);
        new SearchReview().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_management, container, false);
    }
    class SearchReview extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ReviewList.php?mode=userID&find="+ URLEncoder.encode(MainActivity.user.getUserID(),"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp=bufferedReader.readLine()) != null){
                    stringBuilder.append(temp+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpsURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }
        @Override
        public void onPostExecute(String result){
            try{
                String pCode, pName, rText, rDate;
                int score,rId;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    rId = object.getInt("rID");
                    pCode = object.getString("pCode");
                    score = object.getInt("score");
                    pName = object.getString("pName");
                    rText = object.getString("rText");
                    rDate = object.getString("rDate");
                    Date rd = mFormat.parse(rDate);
                    long gap = date.getTime() - rd.getTime();
                    gap = gap/(24*60*60*1000);
                    MyReview review = new MyReview(rId,pCode,score,rText,rDate,pName,(int)gap);
                    reviewList.add(review);
                    adapter.notifyDataSetChanged();
                    count++;
                }
                if(count == 0){
                    no_review_text.setVisibility(View.VISIBLE);
                }else{
                    no_review_text.setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}