package com.joeyzh.imagepicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.joeyzh.imagepicker.R;

import java.util.regex.Pattern;

/**
 * Created by Joey on 2019/3/21.
 *
 * @author by Joey
 */

public class ImageLoaderUtil {

    public static boolean isBase64Img(String imgurl) {
        if (TextUtils.isEmpty(imgurl)) {
            return false;
        }
        if (imgurl.startsWith("data:") && imgurl.split(";base64").length > 1) {
            return true;
        }
        return false;
    }

    public static void loadBase64(final Context context, String encoded, final ImageView imageView) {
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
            }
        };
        encoded = encoded.split(";base64,")[1];
        try {
            byte[] decode = Base64.decode(encoded, Base64.DEFAULT);
            Glide.with(context).load(decode).asBitmap()
                    .placeholder(R.drawable.ic_load_image_fail)
                    .error(R.drawable.ic_load_image_fail)
                    .dontAnimate()
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
            setImage(imageView, encoded);
        }
    }

    /**
     * 显示正常照片
     */
    public static void setImage(final ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Glide.with(imageView.getContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_load_image_fail)
                    .error(R.drawable.ic_load_image_fail)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }
}
