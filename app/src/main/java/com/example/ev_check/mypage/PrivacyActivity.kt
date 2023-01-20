package com.example.ev_check.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class PrivacyActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        val img = findViewById<ImageView>(R.id.img2)
        val back : ImageView = findViewById(R.id.img_back1)
        val img_camera = findViewById<ImageView>(R.id.img_camera)
        val btn_change1 = findViewById<TextView>(R.id.tv_change1)
        val btn_change2 = findViewById<TextView>(R.id.tv_change2)
        val edt_nickname = findViewById<EditText>(R.id.edt_nickname)
        val edt_account = findViewById<EditText>(R.id.edt_account)
        val edt_call = findViewById<EditText>(R.id.edt_call)

        auth = FirebaseAuth.getInstance()

        //뒤로가기버튼
        back.setOnClickListener {
            finish()
        }
        //사용자 사진, 이름, 사용자계정(email)가져오기
        if(auth.currentUser != null){
            Picasso.get().load(auth.currentUser?.photoUrl).into(img)
            edt_nickname.setText(auth.currentUser?.displayName)
            edt_account.setText( auth.currentUser?.email)
            edt_call.setText(auth.currentUser?.phoneNumber)
        }
        //사용자 사진변경하기(img_camera)-> 폰앨범 연결
        img_camera.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,1004)
        }
//btn_change1 이름 변경하여 MypageFragment에 적용하기

//btn_change2 전화번호 고정시키기

        //로그아웃버튼
        findViewById<TextView>(R.id.tv_signOut).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
//startActivityForResult를 통해 받아온 결과값처리메소드

}