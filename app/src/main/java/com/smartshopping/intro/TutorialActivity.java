package com.smartshopping.intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartshopping.R;

public class TutorialActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter pagerAdapter;
    private LinearLayout dots_ll;
    private TextView[] dots;
    private int[] tutorials;
    private Button skip_btn, next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dots_ll = (LinearLayout) findViewById(R.id.layout_dots);
        skip_btn = (Button) findViewById(R.id.skip_btn);
        next_btn = (Button) findViewById(R.id.next_btn);

        tutorials = new int[]{
                R.layout.tutorial_1,
                R.layout.tutorial_2,
                R.layout.tutorial_3,
                R.layout.tutorial_4,
                R.layout.tutorial_5
        };
        addBottomDots(0);

        // 알림 표시줄을 투명하게 만들기
        changeStatusBarColor();

        pagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        // 건너띄기 버튼 클릭시 메인화면으로 이동
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 조건문을 통해 버튼 하나로 두개의 상황을 실행
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < tutorials.length) {
                    // 마지막 페이지가 아니라면 다음 페이지로 이동
                    viewPager.setCurrentItem(current);
                }
                else {
                    // 마지막 페이지라면 메인페이지로 이동
                    finish();
                }
            }
        });

    }

    private void addBottomDots(int currentPage){
        dots = new TextView[tutorials.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dots_ll.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            //dots[i].setTextColor(colorsInactive[currentPage]);
            dots[i].setTextColor(colorsInactive[0]);
            dots_ll.addView(dots[i]);
        }

        if (dots.length > 0){
            //dots[currentPage].setTextColor(colorsActive[currentPage]);
            dots[currentPage].setTextColor(colorsActive[0]);
        }
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == tutorials.length - 1) {
                // last page. make button text to GOT IT
                next_btn.setText(getString(R.string.start));
                skip_btn.setVisibility(View.GONE);
            } else {
                // still pages are left
                next_btn.setText(getString(R.string.next));
                skip_btn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(tutorials[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return tutorials.length;
        }


        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}