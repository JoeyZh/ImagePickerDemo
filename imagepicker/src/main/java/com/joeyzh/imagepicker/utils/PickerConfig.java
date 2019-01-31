package com.joeyzh.imagepicker.utils;

/**
 * Created by Joey on 2018/10/25.
 */

public class PickerConfig {

    private String fileName = "Joeyzh";
    private int pickerMaxNum = 9;
    private int pickerNumColumns = 4;
    private int selectPickerNumColumns = 3;

    public int getSelectPickerNumColumns() {
        return selectPickerNumColumns;
    }

    public PickerConfig setSelectPickerNumColumns(int selectPickerNumColumns) {
        this.selectPickerNumColumns = selectPickerNumColumns;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public PickerConfig setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public int getPickerMaxNum() {
        return pickerMaxNum;
    }

    public PickerConfig setPickerMaxNum(int pickerMaxNum) {
        this.pickerMaxNum = pickerMaxNum;
        return this;
    }

    public int getPickerNumColumns() {
        return pickerNumColumns;
    }

    public PickerConfig setPickerNumColumns(int pickerColumn) {
        this.pickerNumColumns = pickerColumn;
        return this;
    }
}
