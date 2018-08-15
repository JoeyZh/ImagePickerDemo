package com.joeyzh.imagepicker.utils;

/**
 * Created by Joey on 2018/7/25.
 */

public class ImagePickerManager {
    public static int MAX_NUM = 9;
    private static String FILE_DIR = "Joeyzh";

    public static void setMaxNum(int maxNum) {
        MAX_NUM = maxNum;
    }

    public static void setFileDir(String fileDir) {
        FILE_DIR = fileDir;
    }

    public static int getPicMaxNum() {
        return MAX_NUM;
    }

    public static String getFileDir() {
        return FILE_DIR;
    }


}
