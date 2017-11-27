package com.sxt.banner;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sxt.banner.library.CycleViewPager;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        final List<Integer> urls = new ArrayList<>();
        urls.add(R.mipmap.a);
        urls.add(R.mipmap.b);
        urls.add(R.mipmap.c);
        urls.add(R.mipmap.d);
        CycleViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new BaseCyclePagerAdapter(this, urls) {
            @Override
            public Object instantiateItem(ViewGroup container, int position, int datePosition) {

                ImageView img = new ImageView(MainActivity.this);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setImageResource(urls.get(datePosition));
                container.addView(img);

                return img;
            }
        }).addOnPageSelecedListener(new CycleViewPager.OnPageSelectedListener() {
            @Override
            public void onPageSelected(ViewPager viewPager, BaseCyclePagerAdapter adapter, int position, int fixPosition, int datePosition) {
                Log.i("sxt", "position == " + position);
            }
        }).setScrollSelfState(true);
    }
}
