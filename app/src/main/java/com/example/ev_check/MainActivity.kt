package com.example.ev_check

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.FrameMetricsAggregator
import com.example.ev_check.fragment.CarAdminFragment
import com.example.ev_check.fragment.CommunityFragment
import com.example.ev_check.fragment.MapFragment
import com.example.ev_check.fragment.MypageFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    /* ----------- 전역변수 선언 영역 ----------- */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ----------- 변수 선언 및 초기화 영역 ----------- */

        val bnv = findViewById<BottomNavigationView>(R.id.bnv)
        val fl = findViewById<FrameLayout>(R.id.fl)

        /* ----------- 이벤트 영역 ----------- */

        // 처음 실행시 bnv에 MapFragment가 보이도록 설정
        supportFragmentManager.beginTransaction().replace(R.id.fl, MapFragment(), "map").commit() // 커밋 필수!!!

        // bnv(네비게이션 뷰) 클릭 이벤트
        bnv.setOnItemSelectedListener { item ->
            // item --> 내가 선택한 메뉴 정보
            when (item.itemId){ // 선택된 tab에 따른 동작
                // item이 어떤 id값을 가지고 있는지 판단!
                R.id.tab1 -> {
                    // tab1 선택시
                    Toast.makeText(this, "Map 화면", Toast.LENGTH_SHORT).show()
                    if (supportFragmentManager.findFragmentByTag("map") != null){
                        // MapFragment가 존재할 경우, 보여준다.
                        supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("map")!!).commit()
                    } else {
                        // MapFragment가 존재하지 않을 경우, 더해준다.
                        supportFragmentManager.beginTransaction().add(R.id.fl, MapFragment(), "map").commit()
                    }
                    // 다른 세 프래그먼트는 가려준다.
                    if (supportFragmentManager.findFragmentByTag("community") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("community")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("caradmin") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("caradmin")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("mypage") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("mypage")!!).commit()
                    }
                }

                R.id.tab2 -> {
                    // tab2 선택시
                    Toast.makeText(this, "Community 화면", Toast.LENGTH_SHORT).show()
                    if (supportFragmentManager.findFragmentByTag("community") != null){
                        // CommunityFragment가 존재할 경우, 보여준다.
                        supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("community")!!).commit()
                    } else {
                        // CommunityFragment가 존재하지 않을 경우, 더해준다.
                        supportFragmentManager.beginTransaction().add(R.id.fl, CommunityFragment(), "community").commit()
                    }
                    // 다른 세 프래그먼트는 가려준다.
                    if (supportFragmentManager.findFragmentByTag("map") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("map")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("caradmin") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("caradmin")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("mypage") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("mypage")!!).commit()
                    }
                }

                R.id.tab3 -> {
                    // tab3 선택시
                    Toast.makeText(this, "CarAdmin 화면", Toast.LENGTH_SHORT).show()
                    if (supportFragmentManager.findFragmentByTag("caradmin") != null){
                        // CarAdminFragment가 존재할 경우, 보여준다.
                        supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("caradmin")!!).commit()
                    } else {
                        // CarAdminFragment가 존재하지 않을 경우, 더해준다.
                        supportFragmentManager.beginTransaction().add(R.id.fl, CarAdminFragment(), "caradmin").commit()
                    }
                    // 다른 세 프래그먼트는 가려준다.
                    if (supportFragmentManager.findFragmentByTag("map") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("map")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("community") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("community")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("mypage") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("mypage")!!).commit()
                    }
                }

                R.id.tab4 -> {
                    // tab4 선택시
                    Toast.makeText(this, "Mypage 화면", Toast.LENGTH_SHORT).show()
                    if (supportFragmentManager.findFragmentByTag("mypage") != null){
                        // MypageFragment가 존재할 경우, 보여준다.
                        supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("mypage")!!).commit()
                    } else {
                        // MypageFragment가 존재하지 않을 경우, 더해준다.
                        supportFragmentManager.beginTransaction().add(R.id.fl, MypageFragment(), "mypage").commit()
                    }
                    // 다른 세 프래그먼트는 가려준다.
                    if (supportFragmentManager.findFragmentByTag("map") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("map")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("community") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("community")!!).commit()
                    }
                    if (supportFragmentManager.findFragmentByTag("caradmin") != null){
                        supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("caradmin")!!).commit()
                    }
                }
            }
            // false : event 처리가 끝나지 않았다고 판단
            // true : event 종료를 감지해서 다른 버튼 클릭 가능
            true
        }
        /* ----------- 기타 영역 ----------- */

        /* ----------- 테스트 영역 (사용 후 삭제) ----------- */

    }

    /* ----------- 함수, 메서드, 기타기능 영역 ----------- */

    // 텍스트 입력 박스 이외 다른 영역 클릭시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    // 뒤로가기 버튼용 함수
    // Listener역할을 할 Interface 생성
    interface onBackPressedListener {
        fun onBackPressed()
    }

    override fun onBackPressed(){
        // 해당 엑티비티에서 띄운 프래그먼트에서 뒤로가기를 누르게 되면 프래그먼트에서 구현한 onBackPressed 함수가 실행
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is onBackPressedListener) {
                (fragment as onBackPressedListener).onBackPressed()
                return
            }
        }
    }
}