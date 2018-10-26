package com.joeyzh.imagepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joey.ui.view.ExpandGridView;
import com.joeyzh.imagepicker.utils.PickerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created by Joey on 2017/9/4.
 */

public class ImagePickerFragment extends Fragment {

    private ImageButton btnShowImage;
    private View layoutImage;
    private ExpandGridView gvImages;
    private AddImageAdapter addAdapter;
    protected static PickerConfig config;
    private int MAX_NUM = 9;
    private TextView tvImageCount;
    private TextView tvDeleteNotice;
    private boolean editable = true;
    private TextView tvPickerTitle;
    private View view;

    private ArrayList<String> imgsList = new ArrayList<>();

    protected AlertDialog dlgDelete;
    private String deletePath;
    private boolean changed;
    private String title;

    public ImagePickerFragment() {
        config = new PickerConfig();
        setArguments(new Bundle());
    }


    public static ImagePickerFragment newInstance(String[] imgsPath) {
        ImagePickerFragment submitFragment = new ImagePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("images", imgsPath);
        submitFragment.setArguments(bundle);
        return submitFragment;
    }

    public static ImagePickerFragment newInstance(String title, String[] imgsPath) {
        ImagePickerFragment submitFragment = new ImagePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putStringArray("images", imgsPath);
        submitFragment.setArguments(bundle);
        return submitFragment;
    }

    public static ImagePickerFragment newInstance(String title) {
        ImagePickerFragment submitFragment = new ImagePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        submitFragment.setArguments(bundle);
        return submitFragment;
    }

    public ImagePickerFragment initConfig(PickerConfig config) {
        if (config != null) {
            this.config = config;
        }
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pictools_layout_image, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MAX_NUM = config.getPickerMaxNum();
        btnShowImage = view.findViewById(R.id.btn_show_image);
        tvPickerTitle = view.findViewById(R.id.tv_picker_title);
        layoutImage = view.findViewById(R.id.layout_images);
        btnShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutImage.getVisibility() == View.VISIBLE) {
                    hideImages();
                } else {
                    showImages();
                }
            }
        });
        gvImages = view.findViewById(R.id.gv_submit_images);
        gvImages.setNumColumns(config.getPickerNumColumns());
        tvImageCount = view.findViewById(R.id.tv_submit_image_count);
        tvDeleteNotice = view.findViewById(R.id.tv_delete_notice);
        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imgsList.get(position).equals(AddImageAdapter.EMPTY_PATH)) {
                    if (!isEditable()) {
                        return;
                    }
                    Intent intent = new Intent(getActivity(), SelectPictureActivity.class);
                    int max = MAX_NUM - imgsList.size() + 1;
                    intent.putExtra(SelectPictureActivity.INTENT_MAX_NUM, max);
                    startActivityForResult(intent, 100);
                    return;
                }
                // 跳转到详情
                gotoImageDetail(position);

            }
        });
        gvImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditable()) {
                    return true;
                }
                if (imgsList.get(position).equals(AddImageAdapter.EMPTY_PATH)) {
                    return false;
                } else {
                    showDeleteDlg(imgsList.get(position));
                    return true;
                }
            }
        });

        addAdapter = new AddImageAdapter(getActivity(), imgsList);
        imgsList.clear();
        imgsList.add(AddImageAdapter.EMPTY_PATH);
        String[] imgs = getArguments().getStringArray("images");
        if (imgs != null) {
            if (imgs.length >= MAX_NUM) {
                imgsList.clear();
            }
            imgsList.addAll(Arrays.asList(imgs));
        }
        gvImages.setAdapter(addAdapter);
        showImages();
        setEditable(editable);
        String tmpTitle = getArguments().getString("title");
        setTitle(tmpTitle);
    }

    public void gotoImageDetail(int index) {
        ArrayList<String> imgs = new ArrayList<>();
        if (imgsList.isEmpty())
            return;
        if (imgsList.size() == 1 && imgsList.get(0).equals(AddImageAdapter.EMPTY_PATH)) {
            return;
        }
        // 添加按钮在列表的第一个
        if (imgsList.get(imgsList.size() - 1).equals(AddImageAdapter.EMPTY_PATH)) {
            imgs.addAll(imgsList.subList(0, imgsList.size() - 1));
        } else if (imgsList.get(0).equals(AddImageAdapter.EMPTY_PATH)) {
            // 添加按钮在列表的最后一个
            imgs.addAll(imgsList.subList(1, imgsList.size()));
            index = index - 1;
        } else {
            // 没有添加按钮
            imgs = imgsList;
        }
        Intent intent = new Intent(getActivity(), PhotosDisplayActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("images", imgs);
        getActivity().startActivity(intent);
    }

    public void showImages() {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate_180);
        rotate.setFillAfter(true);
        btnShowImage.startAnimation(rotate);
        layoutImage.setVisibility(View.VISIBLE);
//        LogUtils.e("isEnabled : " + isEnabled());
    }

    public void hideImages() {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate_ccw_180);
        rotate.setFillAfter(true);
        btnShowImage.startAnimation(rotate);
        layoutImage.setVisibility(View.GONE);
//        LogUtils.e("isEnabled : " + isEnabled());
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        this.title = title;
        if (tvPickerTitle != null)
            tvPickerTitle.setText(title);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<String> getImgsList() {
        if (imgsList.isEmpty()) {
            return imgsList;
        }
        int start = 0;
        int end = imgsList.size();
        if (imgsList.get(0).equals(AddImageAdapter.EMPTY_PATH)) {
            start = 1;
        } else if (imgsList.get(size() - 1).equals(AddImageAdapter.EMPTY_PATH)) {
            end = imgsList.size() - 1;
        }
        return imgsList.subList(start, end);
    }

    public AddImageAdapter getAddAdapter() {
        return addAdapter;
    }

    public void addPath(String image) {
        imgsList.add(image);
        setImageCount();
    }

    public void addPathList(Collection<String> pathArray) {
        imgsList.addAll(pathArray);
        setImageCount();
    }

    public void addPathList(int index, Collection<String> pathArray) {
        imgsList.addAll(index, pathArray);
        setImageCount();
    }

    public void remove(String path) {
        imgsList.remove(path);
    }

    public void remove(int index) {
        imgsList.remove(index);
    }

    public void clear() {
        imgsList.clear();
        if (isEditable()) {
            imgsList.add(AddImageAdapter.EMPTY_PATH);
        }
    }

    public int size() {
        return imgsList.size();
    }

    private void showDeleteDlg(String path) {
        deletePath = path;
        dlgDelete = new AlertDialog.Builder(getActivity())
                .setTitle("温馨提示")
                .setMessage("您确定要删除图片吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setChanged(true);
                        if (imgsList.size() == MAX_NUM) {
                            if (!imgsList.get(MAX_NUM - 1).equals(AddImageAdapter.EMPTY_PATH))
                                imgsList.add(MAX_NUM, AddImageAdapter.EMPTY_PATH);
                        }
                        imgsList.remove(deletePath);

                        setImageCount();
                        addAdapter.notifyDataSetChanged();
                    }
                })
                .create();
        dlgDelete.show();
    }

    public void setImageCount() {
        int size = imgsList.size() - 1;
        if (size == MAX_NUM - 1) {
            size = (imgsList.get(0).equals(AddImageAdapter.EMPTY_PATH)) ? size : MAX_NUM;
        }
        String message = String.format("已选择图片%d/%d", size, MAX_NUM);
        if (size < 0) {
            message = "无";
        }
        if (tvImageCount != null)
            tvImageCount.setText(message);

    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if (tvDeleteNotice == null) {
            return;
        }
        if (editable) {
            tvDeleteNotice.setVisibility(View.VISIBLE);
            tvImageCount.setVisibility(View.VISIBLE);
            if (!imgsList.contains(AddImageAdapter.EMPTY_PATH)) {
                if (imgsList.size() < MAX_NUM)
                    imgsList.add(0, AddImageAdapter.EMPTY_PATH);
                setImageCount();
                addAdapter.notifyDataSetChanged();
            }
        } else {
            imgsList.remove(AddImageAdapter.EMPTY_PATH);
            if (imgsList.isEmpty()) {
                hideImages();
            }
            tvDeleteNotice.setVisibility(View.GONE);
            tvImageCount.setVisibility(View.INVISIBLE);
            addAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            ArrayList<String> temp = data
                    .getStringArrayListExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            addPathList(temp);

            if (size() > MAX_NUM) {
                remove(AddImageAdapter.EMPTY_PATH);
            }
            setImageCount();
            addAdapter.notifyDataSetChanged();
        }
    }

}
