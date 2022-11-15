package com.example.ev_check

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.FrameMetricsAggregator
import com.example.ev_check.fragment.MapFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // -------------------------------------------------------------

        // 변수 선언
        val bnv = findViewById<BottomNavigationView>(R.id.bnv)
        val fl = findViewById<FrameLayout>(R.id.fl)

        // -------------------------------------------------------------

        // 처음 실행시 bnv에 MapFragment가 보이도록 설정
        supportFragmentManager.beginTransaction().replace(
            R.id.fl, // fragment를 넣을 곳
            MapFragment()
        ).commit() // 커밋 필수!!!

        // -------------------------------------------------------------

        // bnv를 클릭했을 때
        bnv.setOnItemSelectedListener { item ->
            // item --> 내가 선택한 메뉴 정보
            when (item.itemId){
                // item이 어떤 id값을 가지고 있는지 판단!
                R.id.tab1 -> {
                    // tab1 선택시
                    Toast.makeText(this, "첫번째 부분화면", Toast.LENGTH_SHORT).show()
                    // FrameLayout에 HomeFragment 부여
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fl, // fragment를 넣을 곳
                        MapFragment() // 넣을 fragment
                    ).commit() // 커밋 필수!!!
                }

            }
            // false : event 처리가 끝나지 않았다고 판단
            // true : event 종료를 감지해서 다른 버튼 클릭 가능
            true
        }
        // -------------------------------------------------------------


    }


}