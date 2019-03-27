package com.joey.imagepickerdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Joey on 2018/8/10.
 */

public class Base64Helper {


    public static String bitmapToBase64(String filePath, boolean compressed) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bm;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (compressed) {
            bm = CompressBitmapUtils.compressBitmap(filePath);
        } else {
            //解析完整图片
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(filePath, options);
        }
        Bitmap.CompressFormat format = CompressBitmapUtils.getCompressFormat(filePath);
        bm.compress(format, 100, baos);

        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }


    public static String formatBase64Str(String filePath) {
        return formatBase64Str(filePath, true);
    }

    public static String formatBase64Str(String filePath, boolean compressed) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //解析图片边缘
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        String type = options.outMimeType;
        StringBuffer buffer = new StringBuffer();
        buffer.append("data:");
        buffer.append(type);
        buffer.append(";base64,");
        buffer.append(bitmapToBase64(filePath, compressed));
        return buffer.toString();
    }

    public static Bitmap base64ToBitmap(String base64Str) {
        byte[] buffer = Base64.decode(base64Str, Base64.NO_WRAP);
        Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bm;
    }
}
