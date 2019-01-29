package com.joey.imagepickerdemo

import android.app.ActivityManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joeyzh.imagepicker.ImagePickerFragment
import com.joeyzh.imagepicker.utils.PickerConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fr_content_image,
                ImagePickerFragment().
                        initConfig(PickerConfig()
                                .setPickerMaxNum(42)// 最大选择的图片数
                                .setPickerNumColumns(4)))//每行有几张图片
                .commit()
        var activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var strbuf = StringBuffer()
        strbuf.append("memoryClass "+activityManager.memoryClass.toString()+"M")
        strbuf.append("\nlarge memory "+activityManager.largeMemoryClass.toString()+"M")
        tv_title.text = strbuf.toString()

    }
}
