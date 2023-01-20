package com.example.ev_check.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.fragment.CarAdminFragment
import com.example.ev_check.R

class ContentAdmin : AppCompatActivity() {
//    lateinit var adminList : Admin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_admin)
        // 초기화
        val back : ImageView = findViewById(R.id.img_back)
        val admincwrite = findViewById<TextView>(R.id.admin_cwrite)
        val admincdelete = findViewById<TextView>(R.id.admin_cdelete)
        val admincitem = findViewById<TextView>(R.id.admin_citem)
        val admincdate = findViewById<TextView>(R.id.admin_cdate)
        val adminctitle = findViewById<TextView>(R.id.admin_ctitle)
        val admincprice = findViewById<TextView>(R.id.admin_cprice)
        val admincphotoList = findViewById<RecyclerView>(R.id.admin_cphotoList)
        //        adminList = intent.getSerializableExtra("data") as Admin
        //뒤로가기버튼
        back.setOnClickListener {
            finish()
        }
        // 수정 클릭시
        admincwrite.setOnClickListener {
            Toast.makeText(this, "수정 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
            val adcdate: String = admincdate.text.toString()
            val adcprice: String = admincprice.text.toString()
            val adcitem: String = admincitem.text.toString()
            val adctitle: String = adminctitle.text.toString()
//            val adcphotoList: ? = admincphotoList.
            // 데이터 담기
            bundle.putString("adcdate", adcdate)
            bundle.putString("adcprice", adcprice)
            bundle.putString("adcitem", adcitem)
            bundle.putString("adctitle", adctitle)
//            bundle.putString("adcphotoList", adpchotoList)
            // 인텐트 선언
            val intent = Intent(this, WriteAdmin::class.java)
            startActivity(intent)
        }
        admincdelete.setOnClickListener {
            Toast.makeText(this, "수정 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
            val adcdate: String = admincdate.text.toString()
            val adcprice: String = admincprice.text.toString()
            val adcitem: String = admincitem.text.toString()
            val adctitle: String = adminctitle.text.toString()
//            val adcphotoList: ? = admincphotoList.
            // 데이터 담기
            bundle.putString("adcdate", adcdate)
            bundle.putString("adcprice", adcprice)
            bundle.putString("adcitem", adcitem)
            bundle.putString("adctitle", adctitle)
//            bundle.putString("adcphotoList", adpchotoList)
            // 프래그먼트 선언
            val carAdminFragment:  CarAdminFragment = CarAdminFragment()
            // 프래그먼트에 데이터 넘기기
            carAdminFragment.arguments = bundle
            // 프래그먼트 추가, 변경, 삭제 기능
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            // 프래그먼트 화면 보여주기
            transaction.replace(R.id.fragment_adimLayout, carAdminFragment)
            finish()
        }
    }
}