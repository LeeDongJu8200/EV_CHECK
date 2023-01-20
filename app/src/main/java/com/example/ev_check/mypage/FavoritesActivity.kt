package com.example.ev_check.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ev_check.R

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val img_back4:ImageView = findViewById(R.id.img_back4)

        img_back4.setOnClickListener {
            finish()
        }


    }
}