package com.example.ev_check.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ev_check.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val img_back : ImageView = findViewById(R.id.img_back)

        img_back.setOnClickListener {
            finish()
        }
    }
}