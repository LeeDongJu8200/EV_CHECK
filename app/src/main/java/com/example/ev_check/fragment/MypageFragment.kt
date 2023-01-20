package com.example.ev_check.fragment

import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.example.ev_check.mypage.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MypageFragment : Fragment(), MainActivity.onBackPressedListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
        val tv_gauge = view.findViewById<TextView>(R.id.tv_gauge)
        val tv_carname = view.findViewById<TextView>(R.id.tv_carname)
        val tv_favorites_station = view.findViewById<TextView>(R.id.tv_favorites_station)
        val tv_setting = view.findViewById<TextView>(R.id.tv_setting)
        val img = view.findViewById<ImageView>(R.id.img)
        val backwards = view.findViewById<ImageView>(R.id.img_backwards)

        var tvLoginBtn = view.findViewById<TextView>(R.id.tv_login)

        //뒤로가기 버튼 -> 메인페이지로 이동
        backwards.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        //로그인 했을때, 계정명, 이미지 가져오기
        if (auth.currentUser != null) {
            tvLoginBtn.setText(
                auth.currentUser?.displayName + "  님")
//                        auth.currentUser?.email + "\n" + auth.currentUser?.photoUrl

            Picasso.get().load(auth.currentUser?.photoUrl).into(img)
            // 클릭하면 정보변경page로 이동.
            tvLoginBtn.setOnClickListener {
                val intent = Intent(context, PrivacyActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }else {
            //로그인 안했을때, 클릭하면 로그인page로 이동.
            tvLoginBtn.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
//                startActivityForResult(intent, 0)
                startActivity(intent)
                activity?.finish()
            }
        }
        tv_gauge.setOnClickListener {
            val intent = Intent(context, StatusActivity::class.java)
            startActivityForResult(intent, 0)
        }
        tv_carname.setOnClickListener {
            val intent = Intent(context, VehicleActivity::class.java)
            startActivityForResult(intent, 0)
        }
        tv_favorites_station.setOnClickListener {
            val intent = Intent(context, FavoritesActivity::class.java)
            startActivityForResult(intent, 0)
        }
        tv_setting.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivityForResult(intent, 0)
        }
        return view
    }
    // 뒤로가기 버튼 - 추후 개선?
    override fun onBackPressed() {
        // 뒤로가기 2번 눌렀을때의 동작
        if (System.currentTimeMillis() - mBackWait >= 1000){
            // 1초 기다린 후 1초 안에 뒤로가기 2번 연속 클릭시 종료
            mBackWait = System.currentTimeMillis()
            Toast.makeText(context, "뒤로가기 버튼을 연속으로 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 종료
//            MyApplication.prefs.setString("curlat", curlat.toString())
//            MyApplication.prefs.setString("curlng", curlng.toString())
            activity?.finish()
        }
    }
}