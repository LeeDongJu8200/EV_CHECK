package com.example.ev_check.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ev_check.R

class StatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        val img_back2: ImageView = findViewById(R.id.img_back2)

        img_back2.setOnClickListener {
            finish()
        }
    }
}