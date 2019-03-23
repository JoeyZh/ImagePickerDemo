package com.joeyzh.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joeyzh.imagepicker.utils.ImageLoaderUtil;

import java.util.List;


public class AddImageAdapter extends BaseAdapter {


    private List<String> data;
    private Context context;
    //定义接口属性
    private OnItemClickLisener callback;

    public void setCallback(OnItemClickLisener callback) {
        this.callback = callback;
    }

    public static final String EMPTY_PATH = "path://add_image";

    public AddImageAdapter(Context context, List<String> dataList) {
        this.data = dataList;
        this.context = context;
    }

    @Override
    public String getItem(int i) {
        return data.get(i);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.pictools_item_add_image, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String path = getItem(position);
        setImageView(path, holder);
        return convertView;
    }

    private void setImageView(String path, ViewHolder holder) {
        if (EMPTY_PATH.equals(path)) {
            setImageView(holder.img, R.drawable.ic_addpic);
            holder.delImgView.setVisibility(View.INVISIBLE);
            return;
        }

        holder.delImgView.setVisibility(View.VISIBLE);
        final String finalPath = path;
        holder.delImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick(finalPath);
            }
        });
        if (ImageLoaderUtil.isBase64Img(finalPath)) {
            ImageLoaderUtil.loadBase64(context, finalPath, holder.img);
            return;
        }
        Uri uri = Uri.parse(path);
        if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
            path = "file://" + path;
        }
        ImageLoaderUtil.setImage(holder.img, path);

    }

    public void setText(TextView textView, int res) {
        if (res <= 0) {
            textView.setText("");
            return;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(res);
    }


    public void setText(TextView textView, CharSequence text) {
        if (TextUtils.isEmpty(text) || "null".equalsIgnoreCase(text.toString())) {
            textView.setText("");
            return;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }

    public void setImageView(ImageView imageView, int res) {
        if (res <= 0) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(res);
    }

    public void setImageView(final ImageView imageView, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        ImageLoaderUtil.setImage(imageView, url);

    }

    private class ViewHolder {
        ImageView img;
        private ImageView delImgView;

        public ViewHolder(View convertView) {
            img = convertView.findViewById(R.id.img_photo);
            delImgView = convertView.findViewById(R.id.img_del);
        }
    }

    public interface OnItemClickLisener {
        void onClick(String path);
    }
}