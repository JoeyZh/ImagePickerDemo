package com.joeyzh.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joey.ui.adapter.BaseAdapter;
import com.joey.ui.util.ImageShapeUtil;

import java.util.List;


public class AddImageAdapter extends BaseAdapter {


    private List<String> data;
    private Context context;

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
        } else
            holder = (ViewHolder) convertView.getTag();
        String path = getItem(position);
        if (EMPTY_PATH.equals(path)) {
            setImageView(holder.img, R.drawable.ic_addpic);
        } else {
            Uri uri = Uri.parse(path);
            if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
                path = "file://" + path;
            }
            ImageShapeUtil.setImage(holder.img,path);

        }
//        holder.img.setTag(path);
        return convertView;
    }

    private class ViewHolder {
        ImageView img;

        public ViewHolder(View convertView) {
            img =  convertView.findViewById(R.id.img_photo);
        }
    }

}