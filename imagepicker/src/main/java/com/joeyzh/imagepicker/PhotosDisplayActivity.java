package com.joeyzh.imagepicker;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joey.ui.general.BaseActivity;
import com.joey.ui.util.ImageShapeUtil;

import java.util.ArrayList;


public class PhotosDisplayActivity extends BaseActivity {

    private final String TAG = getClass().toString();

    private ViewPager viewPager;
    private ArrayList<String> mapList;
    private int displayIndex;
    private int group;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            displayIndex = arg0;
            setTitle(String.format("%d/%d", (displayIndex + 1),
                    mapList.size()));
            Log.i(TAG, mapList.get(arg0).toString());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictools_activity_photo_display);
        initViews();
    }

    protected void initViews() {
        mapList = (ArrayList<String>) getIntent().getStringArrayListExtra("images");
        displayIndex = getIntent().getIntExtra("index", 0);
        setTitle(String.format("%d/%d", (displayIndex + 1), mapList.size()));
        viewPager = (ViewPager) findViewById(R.id.album_photos_viewpager);
        BrowseAdapter browserAdapter = new BrowseAdapter(mapList);
        viewPager.setAdapter(browserAdapter);
        viewPager.setCurrentItem(displayIndex);
        viewPager.setOnPageChangeListener(pageChangeListener);

    }

    @Override
    public void onBindView() {

    }


    class BrowseAdapter extends PagerAdapter {
        private ArrayList<String> mapList;
        private LayoutInflater inflater;

        public BrowseAdapter(ArrayList<String> fileArray) {
            mapList = fileArray;
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(
                    R.layout.pictools_item_photo_display, container, false);
            final TouchImageView currentimage = (TouchImageView) imageLayout
                    .findViewById(R.id.album_photo_display_img);
            if (position >= mapList.size())
                currentimage.setImageDrawable(new ColorDrawable());
            else {
                if (mapList.get(position).startsWith("http")) {
                    ImageShapeUtil.setImage(currentimage, mapList.get(position));
                } else {
//                    currentimage.setImageBitmap(BitmapFactory.decodeFile(mapList
//                            .get(position).toString()));
                    ImageShapeUtil.setImage(currentimage, "file://" + mapList.get(position));

                }
            }
            currentimage.setMaxZoom(4f);
            ((ViewPager) container).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}