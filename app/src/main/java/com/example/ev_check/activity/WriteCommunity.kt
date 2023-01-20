package com.example.ev_check.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.fragment.CommunityFragment
import com.example.ev_check.R

class WriteCommunity : AppCompatActivity() {
    //** 이미지 첨부위한..
    lateinit var communityGalleryAdapter: CommunityGalleryAdapter
    var imageList: ArrayList<Uri> = ArrayList()   //**

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_community)
        // 초기화
        val back : ImageView = findViewById(R.id.img_back)
        val commuwwrite = findViewById<TextView>(R.id.commu_wwrite)
        val commuwtitle = findViewById<EditText>(R.id.commu_wtitle)
        val commuwitem = findViewById<CheckBox>(R.id.commu_witem)
        val commuwcontent = findViewById<EditText>(R.id.commu_wcontent)
        val commuwphoto = findViewById<TextView>(R.id.commu_wphoto)
        val commuwphotoList = findViewById<RecyclerView>(R.id.commu_wphotoList)
        val commuwplace = findViewById<TextView>(R.id.commu_wplace)
        //뒤로가기버튼
        back.setOnClickListener {
            finish()
        }
        // 등록 버튼 클릭시
        commuwwrite.setOnClickListener {
            Toast.makeText(this, "작성 등록 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
//            val adminwdate: String =
            val comwtitle: String = commuwtitle.text.toString()
            val comwitem: String = commuwitem.text.toString()
            val comwcontent: String = commuwcontent.text.toString()
//            val comwphotoList: String = commuwphotoList.text.toString()
            val comwplace: String = commuwplace.text.toString()
            // 데이터 담기
//            bundle.putString("adminwdate", adminwdate)
            bundle.putString("comwtitle", comwtitle)
            bundle.putString("comwitem", comwitem)
            bundle.putString("comwcontent", comwcontent)
//            bundle.putString("comwphotoList", comwphotoList)
            bundle.putString("comwplace", comwplace)
            // 프래그먼트 선언
            val communityFragment: CommunityFragment = CommunityFragment()
            // 프래그먼트에 데이터 넘기기
            communityFragment.arguments = bundle
            // 프래그먼트 추가, 변경, 삭제 기능
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            // 프래그먼트 화면 보여주기
            transaction.replace(R.id.fragment_commuLayout, communityFragment)
            finish()
        }
        // 체크박스 클릭시
        commuwitem.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this, isChecked.toString(), Toast.LENGTH_SHORT).show()
            if (isChecked) {
                commuwitem.text = "고장"
            } else {
                commuwitem.text = "고장"
            }
        }
        //** 갤러리 사진 첨부 apapter 초기화
        communityGalleryAdapter = CommunityGalleryAdapter(imageList, this)
        // 갤러리 사진 첨부 recyclerView 설정
        commuwphotoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        commuwphotoList.adapter = communityGalleryAdapter   // **
        // 사진 첨부 버튼 클릭시
        commuwphoto.setOnClickListener {
            Toast.makeText(this, "사진 첨부 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            // 멀티 선택 기능
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResult.launch(intent)
        }
    }
    // 사진첨부 결과 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 결과 코드 OK, 결과값 null 아니면
        if(it.resultCode == RESULT_OK) {
            // 멀티 선택은 clipData
            if(it.data!!.clipData != null) {    // 멀티 이미지
                // 선택한 이미지 갯수
                val count = it.data!!.clipData!!.itemCount
                for (index in 0 until count) {
                    if (count < 6) {
                        // 이미지 담기
                        val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                        // 이미지 추가
                        imageList.add(imageUri)
                    } else {
                        Toast.makeText(this, "5장까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            communityGalleryAdapter.notifyDataSetChanged()
        }
    }
}