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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Joey on 2019/3/21.
 *
 * @author by Joey
 */

public class MainActivity extends AppCompatActivity {

    ImagePickerFragment pickerFragment;
    //    String path = "/DCIM/Camera/IMG_20190312_160924.jpg";
    String path = "/scan.png";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = Environment.getExternalStorageDirectory().getPath() + path;
        PickerConfig config = new PickerConfig()
                .setPickerMaxNum(42)
                .setPickerNumColumns(4);
        File file = new File(path);
        String base64Str = "";
        if (file.exists()) {
            base64Str = Base64Helper.formatBase64Str(path, true);
            Log.i("MainActivity", base64Str.length() + "");
        }
        String base64Str1 = readAssetsTxt(this, "base64_3");
        Log.i("MainActivity 1 ", base64Str1.length() + "");
        String base64Str2 = readAssetsTxt(this, "base64_2");
        Log.i("MainActivity 2", base64Str2.length() + "");
        pickerFragment = ImagePickerFragment.newInstance(new String[]{base64Str, base64Str1, base64Str2}).initConfig(config);
//        } else {
//            pickerFragment = new ImagePickerFragment().initConfig(config);
//        }
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

    public static String format(String original) {
        final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(original);
        String result = m.replaceAll("");
        return result;
    }


    public static String readAssetsTxt(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "{\"ResultData\":\"读取错误，请检查文件名！\",\"Success\":true,\"ReturnMsg\":\"读取错误，请检查文件名！\"}";
    }

}