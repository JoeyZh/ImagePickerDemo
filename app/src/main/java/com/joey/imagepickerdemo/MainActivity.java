package com.joey.imagepickerdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.joeyzh.imagepicker.ImagePickerFragment;
import com.joeyzh.imagepicker.utils.PickerConfig;

/**
 * Created by Joey on 2019/3/21.
 *
 * @author by Joey
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fr_content_image,
                new ImagePickerFragment().
                        initConfig(new PickerConfig()
                                .setPickerMaxNum(42)// 最大选择的图片数
                                .setPickerNumColumns(4)))//每行有几张图片
                .commit();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("memoryClass " + activityManager.getMemoryClass() + "M");
        strbuf.append("\nlarge memory " + activityManager.getLargeMemoryClass() + "M");
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(strbuf.toString());
    }
}
