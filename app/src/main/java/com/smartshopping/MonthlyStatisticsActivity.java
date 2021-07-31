package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


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

import javax.net.ssl.HttpsURLConnection;

public class MonthlyStatisticsActivity extends AppCompatActivity {

    private User user = MainActivity.user;
    private String year, this_year;
    private int this_month;
    private Spinner year_spin;
    private BarChart barChart;
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<BarEntry> entries = new ArrayList<>();
    private int[] spend_data = {0,0,0,0,0,0,0,0,0,0,0,0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_statistics);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat yFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat mFormat = new SimpleDateFormat("MM");
        this_year = yFormat.format(date);
        this_month = Integer.parseInt(mFormat.format(date));
        year = this_year;
        year_spin = findViewById(R.id.year_spin);
        ArrayAdapter year_adapt = ArrayAdapter.createFromResource(this,R.array.year,android.R.layout.simple_spinner_dropdown_item);
        year_spin.setAdapter(year_adapt);

        barChart = findViewById(R.id.bar_chart);

        for(int i = 0; i<12;i++){
            labels.add((i+1)+"월");
        }

        Button chart_search_btn = findViewById(R.id.chart_search_btn);
        chart_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.clear();
                entries.clear();
                for(int i = 0; i<12; i++){
                    spend_data[i] = 0;
                }
                year = year_spin.getSelectedItem().toString();
                new BackgroundTask().execute();
            }
        });

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_PurchaseCal_month.php?userID="+ URLEncoder.encode(user.getUserID(),"UTF-8")
                        +"&year="+URLEncoder.encode(year,"UTF-8");
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
                int price, month;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    month = object.getInt("month");
                    price = object.getInt("price");
                    entries.add(new BarEntry((float) price,month-1));
                    spend_data[month-1] = price;
                    count++;
                }
                DrawChart();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void DrawChart(){
        BarDataSet barDataSet = new BarDataSet(entries,"월별 지출 내역");
        BarData barData = new BarData(labels,barDataSet);
        barData.setValueTextSize(20);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barChart.setData(barData);
        barChart.setDescription("");
        barChart.animateXY(1000,2000);
        barChart.invalidate();
        TextView stat_text = findViewById(R.id.stat_text);
        int sum = 0;
        int avg = 0;
        if(year.equals(this_year)){
            for(int i = 0; i<this_month;i++){
                sum += spend_data[i];
            }
            avg = sum/this_month;
            double gap = ((int)((spend_data[this_month-1]/(double)avg)*1000))/10.0;

            stat_text.setText("총 지출액 : "+sum + "원");
            stat_text.append("\n월 평균 지출액 : "+avg+"원");
            stat_text.append("\n이번달의 지출액은 평균의 "+gap+"%입니다.");
        }else{
            for(int i = 0; i<12;i++){
                sum += spend_data[i];
            }
            avg = sum/this_month;
            stat_text.setText("총 지출액 : "+sum + "원");
            stat_text.append("\n월 평균 지출액 : "+avg+"원");
        }




    }
}