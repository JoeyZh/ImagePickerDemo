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
        encoded = encoded.split("base64,")[1];
        byte[] decode = Base64.decode(encoded, Base64.DEFAULT);
        try {
            Glide.with(context).load(decode).asBitmap()
                    .placeholder(R.drawable.ic_load_image_fail)
                    .error(R.drawable.ic_load_image_fail)
                    .dontAnimate()
                    .into(target);
        } catch (Exception e) {

        }
    }

    /**
     * 显示正常照片
     */
    public static void setImage(final ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(url) //加载url
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()//取消动画
                .placeholder(R.drawable.ic_load_image_fail) //占位图设置
                .error(R.drawable.ic_load_image_fail)//显示异常图
                .into(imageView);
    }

}
