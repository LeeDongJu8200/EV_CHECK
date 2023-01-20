package com.example.ev_check.mypage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.example.ev_check.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class InformationCar_Activity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth   // firebase  선언
    lateinit var img_gallery: ImageView  //image메소드 만들기 위한 img_gallery전역변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_car)

        img_gallery= findViewById(R.id.img_gallery)
        val img_back3 = findViewById<ImageView>(R.id.img_back3)

        auth = Firebase.auth // firebase초기화

        //뒤로가기버튼
        img_back3.setOnClickListener {
            finish()
        }

        //img_gallery를 눌렀을때, 에뮬레이터 앨범에 저장하기(묵시적 인텐트)
        //미디어스토어에서 제공하는 기능으로 캡쳐실행
        img_gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1004)
        }
    }
    //startActivityForResult를 통해 받아온 결과값을 처리하는 메소드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //requestCode : 1004
        //resultCode: data를 보내주는 곳에서 보내는 신호
        //성공/실패 때 신호가 다름.
        //이미지를 잘 받아온다면, RESULT_OK 코드를 받아옴
        //data -> img

        //requestCode가 1004가 맞는지?
        //resultCode가 RESULT_OK 가 맞는지?
        if (requestCode == 1004 && resultCode == RESULT_OK)
        //img_gallery를 받아온 Img로 바꾸자
            img_gallery.setImageURI(data?.data)
        //firebase에 저장시키기

    }
    //    img(에뮬레이터 메모리에 있는)를 FireBase에 올려주는 메소드
    fun imgUpload(key:String){
        //firebase Storage사용은 imgUpload메서드 안에서만 사용함
        val storage = Firebase.storage

        val storageRef = storage.reference

        val mountainsRef = storageRef.child("$key.jpg")

        // Get the data from an ImageView as bytes
        img_gallery.isDrawingCacheEnabled = true
        img_gallery.buildDrawingCache()
        val bitmap = (img_gallery.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
}