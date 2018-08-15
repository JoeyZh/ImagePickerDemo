package com.joeyzh.imagepicker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joey.base.util.Permission;
import com.joey.base.util.PermissionManager;
import com.joey.ui.general.BaseActivity;
import com.joey.ui.util.ImageShapeUtil;
import com.joeyzh.imagepicker.utils.ImagePickerManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class SelectPictureActivity extends BaseActivity {

    /**
     * 最多选择图片的个数
     */
    private static int MAX_NUM = 5;
    private static final int TAKE_PICTURE = 520;

    public static final String INTENT_MAX_NUM = "intent_max_num";
    public static final String INTENT_SELECTED_PICTURE = "intent_selected_picture";
    private static final String FILE_DIR = ImagePickerManager.getFileDir();

    private Context context;
    private GridView gridview;
    private PictureAdapter adapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashMap<String, Integer> tmpDir = new HashMap<String, Integer>();
    private ArrayList<ImageFloder> mDirPaths = new ArrayList<ImageFloder>();
    private ContentResolver mContentResolver;
    private Button btn_select, btn_ok;
    private ListView listview;
    private FolderAdapter folderAdapter;
    private ImageFloder imageAll, currentImageFolder;

    /**
     * 已选择的图片
     */
    private ArrayList<String> selectedPicture = new ArrayList<String>();
    private String cameraPath = null;
    private boolean selectedFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictools_activity_select_picture);
        toolbar.setVisibility(View.GONE);
        MAX_NUM = getIntent().getIntExtra(INTENT_MAX_NUM, 6);
        context = this;
        mContentResolver = getContentResolver();
        initView();
        if (!PermissionManager.checkPermisson(this, Permission.STORAGE, Permission.RequestCode.TYPE_STORAGE)) {
            return;
        }
        selectedFlag = getIntent().getBooleanExtra("selectedFlag", true);
        if (!selectedFlag)
            goCamare();
    }

    public void select(View v) {
        if (listview.getVisibility() == View.VISIBLE) {
            hideListAnimation();
        } else {
            listview.setVisibility(View.VISIBLE);
            gridview.setVisibility(View.GONE);
            showListAnimation();
            folderAdapter.notifyDataSetChanged();
        }
    }

    public void showListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1, 0f);
        ta.setDuration(200);
        listview.startAnimation(ta);
    }

    public void hideListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1, 1f);
        ta.setDuration(200);
        listview.startAnimation(ta);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listview.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 点击完成按钮
     *
     * @param v
     * @version 1.0
     * @author zyh
     */
    public void ok(View v) {
        Intent data = new Intent();
        data.putStringArrayListExtra(INTENT_SELECTED_PICTURE, selectedPicture);
//        data.putExtra(INTENT_SELECTED_PICTURE, selectedPicture);
        Log.e("selectImage:", selectedPicture.toString());
        setResult(RESULT_OK, data);
        this.finish();
    }

    private void initView() {
        imageAll = new ImageFloder();
        imageAll.setDir("/所有图片");
        currentImageFolder = imageAll;
        mDirPaths.add(imageAll);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_ok.setText("完成0/" + MAX_NUM);

        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new PictureAdapter();
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (PermissionManager.checkPermisson(SelectPictureActivity.this, Permission.CAMERA, Permission.RequestCode.TYPE_CAMERA))
                        goCamare();
                }
            }
        });

        listview = (ListView) findViewById(R.id.listview);
        folderAdapter = new FolderAdapter();
        listview.setAdapter((ListAdapter) folderAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentImageFolder = mDirPaths.get(position);
                Log.d("zyh", position + "-------" + currentImageFolder.getName() + "----"
                        + currentImageFolder.images.size());
                hideListAnimation();
                adapter.notifyDataSetChanged();
                btn_select.setText(currentImageFolder.getName());
            }
        });
        getThumbnail();
    }

    /**
     * 使用相机拍照
     *
     * @version 1.0
     * @author zyh
     */
    protected void goCamare() {
        if (selectedPicture.size() + 1 > MAX_NUM) {
            Toast.makeText(context, "最多选择" + MAX_NUM + "张", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PermissionManager.checkPermisson(this, Permission.CAMERA)) {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri = getOutputMediaFileUri();
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
//            MessageBox.Show(SelectPictureActivity.this, R.string.camera_forbidden_warning);
        }
    }

    /**
     * 用于拍照时获取输出的Uri
     *
     * @return
     * @version 1.0
     * @author zyh
     */

    protected Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FILE_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        cameraPath = mediaFile.getAbsolutePath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context.getApplicationContext(), getApplicationContext().getPackageName() + ".fileProvider", mediaFile);
        }
        return Uri.fromFile(mediaFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && cameraPath != null) {
            selectedPicture.add(cameraPath);
            Intent data2 = new Intent();
            data2.putExtra(INTENT_SELECTED_PICTURE, selectedPicture);
            setResult(RESULT_OK, data2);
            this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode <= 0) {
            return;
        }
        // 请求相机权限
        if (requestCode == Permission.RequestCode.TYPE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goCamare();
                return;
            }
            AlertDialog warnDialog =
                    new AlertDialog.Builder(SelectPictureActivity.this)
                            .setTitle("温馨提示")
                            .setMessage("应用需要手机的拍照视频功能，请您打开手机的视频拍照权限！")
                            .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PermissionManager.checkPermisson(SelectPictureActivity.this, permissions, Permission.RequestCode.TYPE_CAMERA);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();
            warnDialog.setCancelable(false);
            warnDialog.setCanceledOnTouchOutside(false);
            warnDialog.show();
            return;
        }
        if (requestCode == Permission.RequestCode.TYPE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mContentResolver = getContentResolver();
                initView();
                return;
            }
            AlertDialog warnDialog =
                    new AlertDialog.Builder(SelectPictureActivity.this)
                            .setTitle("温馨提示")
                            .setMessage("应用需要使用手机的存储权限，否则无法读取手机的相册！")
                            .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PermissionManager.checkPermisson(SelectPictureActivity.this, permissions, Permission.RequestCode.TYPE_STORAGE);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();
            warnDialog.setCancelable(false);
            warnDialog.setCanceledOnTouchOutside(false);
            warnDialog.show();
            return;
        }
    }

    public void back(View v) {
        onBackPressed();
    }

    @Override
    public void onBindView() {

    }

    class PictureAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return currentImageFolder.images.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.pictools_grid_item_picture, null);
                holder = new ViewHolder();
                holder.iv = convertView.findViewById(R.id.iv);
                holder.checkBox = convertView.findViewById(R.id.check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.iv.setImageResource(R.drawable.ic_camera_normal);
                holder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                position = position - 1;
                holder.checkBox.setVisibility(View.VISIBLE);
                final ImageItem item = currentImageFolder.images.get(position);
                ImageShapeUtil.setImage(holder.iv, "file://" + item.path);
                boolean isSelected = selectedPicture.contains(item.path);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!v.isSelected() && selectedPicture.size() + 1 > MAX_NUM) {
                            Toast.makeText(context, "最多选择" + MAX_NUM + "张", Toast.LENGTH_SHORT).show();
                            v.setSelected(false);
                            return;
                        }
                        if (selectedPicture.contains(item.path)) {
                            selectedPicture.remove(item.path);
                        } else {
                            selectedPicture.add(item.path);
                        }
                        btn_ok.setEnabled(selectedPicture.size() > 0);
                        btn_ok.setText("完成" + selectedPicture.size() + "/" + MAX_NUM);
                        v.setSelected(selectedPicture.contains(item.path));
                    }
                });
                holder.checkBox.setSelected(isSelected);
            }
            return convertView;
        }


    }

    class ViewHolder {
        ImageView iv;
        Button checkBox;
    }

    class FolderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDirPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FolderViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.pictools_list_dir_item, null);
                holder = new FolderViewHolder();
                holder.id_dir_item_image = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                holder.id_dir_item_name = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                holder.id_dir_item_count = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                holder.choose = (ImageView) convertView.findViewById(R.id.choose);
                convertView.setTag(holder);
            } else {
                holder = (FolderViewHolder) convertView.getTag();
            }
            ImageFloder item = mDirPaths.get(position);
            ImageShapeUtil.setImage(holder.id_dir_item_image, "file://" + item.getFirstImagePath());
            holder.id_dir_item_count.setText(item.images.size() + "张");
            holder.id_dir_item_name.setText(item.getName());
            holder.choose.setVisibility(currentImageFolder == item ? View.VISIBLE : View.GONE);
            return convertView;
        }
    }

    class FolderViewHolder {
        ImageView id_dir_item_image;
        ImageView choose;
        TextView id_dir_item_name;
        TextView id_dir_item_count;
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail() {
        try {
            Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, "", null,
                    MediaStore.MediaColumns.DATE_ADDED + " DESC");
            // Log.e("TAG", mCursor.getCount() + "");
            if (mCursor.moveToFirst()) {
                int _date = 0;
                try {
                    _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                } catch (Exception e) {

                }
                do {
                    try {
                        // 获取图片的路径
                        String path = mCursor.getString(_date);
                        // Log.e("TAG", path);
                        imageAll.images.add(new ImageItem(path));
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null) {
                            continue;
                        }
                        ImageFloder imageFloder = null;
                        String dirPath = parentFile.getAbsolutePath();
                        if (!tmpDir.containsKey(dirPath)) {
                            // 初始化imageFloder
                            imageFloder = new ImageFloder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                            mDirPaths.add(imageFloder);
                            // Log.d("zyh", dirPath + "," + path);
                            tmpDir.put(dirPath, mDirPaths.indexOf(imageFloder));
                        } else {
                            imageFloder = mDirPaths.get(tmpDir.get(dirPath));
                        }
                        imageFloder.images.add(new ImageItem(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (mCursor.moveToNext());
            }
            mCursor.close();
            // for (int i = 0; i < mDirPaths.size(); i++) {
            //     ImageFloder f = mDirPaths.get(i);
            //     Log.d("zyh", i + "-----" + f.getName() + "---" + f.images.size());
            // }
        } catch (Exception e) {

        }
        tmpDir = null;
    }

    class ImageFloder {
        /**
         * 图片的文件夹路径
         */
        private String dir;

        /**
         * 第一张图片的路径
         */
        private String firstImagePath;
        /**
         * 文件夹的名称
         */
        private String name;

        public List<ImageItem> images = new ArrayList<ImageItem>();

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
            int lastIndexOf = this.dir.lastIndexOf("/");
            this.name = this.dir.substring(lastIndexOf);
        }

        public String getFirstImagePath() {
            return firstImagePath;
        }

        public void setFirstImagePath(String firstImagePath) {
            this.firstImagePath = firstImagePath;
        }

        public String getName() {
            return name;
        }

    }

    class ImageItem {
        String path;

        public ImageItem(String p) {
            this.path = p;
        }
    }

}