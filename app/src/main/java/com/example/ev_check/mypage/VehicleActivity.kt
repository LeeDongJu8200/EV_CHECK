package com.example.ev_check.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.ev_check.R

class VehicleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle)

        val img_back5 = findViewById<ImageView>(R.id.img_back5)
        val tv_vehicle = findViewById<TextView>(R.id.tv_vehicle)

        //뒤로가기버튼
        img_back5.setOnClickListener {
            finish()
        }

        tv_vehicle.setOnClickListener {
            val intent = Intent(this, InformationCar_Activity::class.java)
            startActivityForResult(intent, 0)
        }
    }
}