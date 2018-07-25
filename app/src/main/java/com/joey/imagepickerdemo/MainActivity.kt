package com.joey.imagepickerdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joeyzh.imagepicker.ImageSubmitFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fr_content_image, ImageSubmitFragment()).commit()
    }
}
