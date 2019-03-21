package com.joeyzh.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joey.ui.adapter.BaseAdapter;
import com.joey.ui.util.ImageShapeUtil;
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
        ImageShapeUtil.setImage(holder.img, path);


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