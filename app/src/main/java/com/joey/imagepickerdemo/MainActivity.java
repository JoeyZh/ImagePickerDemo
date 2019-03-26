package com.joey.imagepickerdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.joeyzh.imagepicker.ImagePickerFragment;
import com.joeyzh.imagepicker.utils.ImagePickerManager;
import com.joeyzh.imagepicker.utils.PickerConfig;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Joey on 2019/3/21.
 *
 * @author by Joey
 */

public class MainActivity extends AppCompatActivity {

    ImagePickerFragment pickerFragment;
    String path = "/DCIM/Camera/IMG_20190312_160924.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = Environment.getExternalStorageDirectory().getPath() + path;
        PickerConfig config = new PickerConfig()
                .setPickerMaxNum(42)
                .setPickerNumColumns(4);
        File file = new File(path);
        if (file.exists()) {
            String base64Str = Base64Helper.formatBase64Str(path, true);
            Log.i("MainActivity", base64Str);
            pickerFragment = ImagePickerFragment.newInstance(new String[]{base64Str}).initConfig(config);
        } else {
            pickerFragment = new ImagePickerFragment().initConfig(config);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fr_content_image,
                pickerFragment)
                .commit();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("memoryClass " + activityManager.getMemoryClass() + "M");
        strbuf.append("\nlarge memory " + activityManager.getLargeMemoryClass() + "M");
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(strbuf.toString());
    }


}
