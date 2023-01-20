package com.example.ev_check.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.fragment.CarAdminFragment
import com.example.ev_check.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class WriteAdmin : AppCompatActivity() {
    //** 이미지 첨부위한..
    lateinit var adminGalleryAdapter: AdminGalleryAdapter
    var imageList: ArrayList<Uri> = ArrayList()   //**
    //** 사진 촬영위한..
    lateinit var bitmap: Bitmap
//    lateinit var adminwphotoList: RecyclerView    //**
    // 달력에 날짜 표시위한..
    lateinit var adminwdate: TextView
    // FireBase.. 전역변수로 사용할 수 있도록 선언
    lateinit var auth: FirebaseAuth
    lateinit var adminwphoto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_admin)
        // 초기화
        val back : ImageView = findViewById(R.id.img_back)
        val adminwwrite = findViewById<TextView>(R.id.admin_wwrite)
        val adminwdate = findViewById<TextView>(R.id.admin_wdate)
        val adminwdateselect = findViewById<ImageView>(R.id.admin_wdateselect)
        val adminwprice = findViewById<EditText>(R.id.admin_wprice)
        val adminwitem = findViewById<CheckBox>(R.id.admin_witem)
        val adminwphoto = findViewById<TextView>(R.id.admin_wphoto)
        val adminwcamera = findViewById<LinearLayout>(R.id.admin_wcamera)
        val adminwtitle = findViewById<EditText>(R.id.admin_wtitle)
        val adminwphotoList = findViewById<RecyclerView>(R.id.admin_wphotoList)
        // FireBase 초기화
        auth = Firebase.auth
        // DataBase 기능 가져오기
        val admindb = Firebase.database
        // db 저장
        val myRef = admindb.getReference("Admin")
        //뒤로가기버튼
        back.setOnClickListener {
            finish()
        }
        // 등록 버튼 클릭시
        adminwwrite.setOnClickListener {
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
            val adwdate: String = adminwdate.text.toString()
            val adwprice: String = adminwprice.text.toString()
            val adwitem: String = adminwitem.text.toString()
            val adwtitle: String = adminwtitle.text.toString()
//            val adwphotoList = auth.currentUser?.uid.toString()
            // 데이터 담기
            bundle.putString("adwdate", adwdate)
            bundle.putString("adprice", adwprice)
            bundle.putString("adwitem", adwitem)
            bundle.putString("adtitle", adwtitle)
//            bundle.putString("adphotoList", adwphotoList)
            // 미리 uid값 받아오기 : 올라가는 게시글 == 이미지의 이름 일치
            val uid = auth.currentUser?.uid.toString()
            val key = myRef.push().key.toString()
            myRef.child(key).setValue(Admin(adwitem, adwdate, adwtitle, adwprice, uid))
            // 이미지 업로드
            imgUpload(key)
            // 프래그먼트 선언
            val carAdminFragment: CarAdminFragment = CarAdminFragment()
            // 프래그먼트에 데이터 넘기기
            carAdminFragment.arguments = bundle
            // 프래그먼트 추가, 변경, 삭제 기능
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            // 프래그먼트 화면 보여주기
            transaction.replace(R.id.fragment_adimLayout, carAdminFragment)
            finish()
            Toast.makeText(this, "작성 등록 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
        // 날짜 클릭시
        // 오늘 날짜 변수에 담기
        val calendar: Calendar = Calendar.getInstance()
        var iYear = calendar.get(Calendar.YEAR)
        var iMonth = calendar.get(Calendar.MONTH)
        var iDay = calendar.get(Calendar.DAY_OF_MONTH)
        adminwdate.text = "${iYear}년 ${iMonth+1}월 ${iDay}일"
        adminwdateselect.setOnClickListener {
            Toast.makeText(this, "날짜 선택!", Toast.LENGTH_SHORT).show()
            showDatePicker()
        }
        // 정비 체크박스 클릭시
        adminwitem.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this, isChecked.toString(), Toast.LENGTH_SHORT).show()
            if (isChecked) {
                adminwitem.text = "정비"
            } else {
                adminwitem.text = "정비"
            }
        }
        //** 갤러리 사진 첨부 apapter 초기화
        adminGalleryAdapter = AdminGalleryAdapter(imageList, this)
        // 갤러리 사진 첨부 recyclerView 설정
        adminwphotoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        adminwphotoList.adapter = adminGalleryAdapter   // **
        // 사진 첨부 버튼 클릭시
        adminwphoto.setOnClickListener {
            Toast.makeText(this, "사진 첨부 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            // 멀티 선택 기능
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResult.launch(intent)
        }
        // 사진 촬영 버튼 클릭시
        adminwcamera.setOnClickListener {
            // 사진 촬영 : 기본 카메라앱 실행
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activitycameraResult.launch(intent)
        }
    }
    // 달력.. 날짜 보여주기
    private fun showDatePicker() {
        // 오늘 날짜 변수에 담기
        val calendar: Calendar = Calendar.getInstance()
        val iYear = calendar.get(Calendar.YEAR)
        val iMonth = calendar.get(Calendar.MONTH)
        val iDay = calendar.get(Calendar.DAY_OF_MONTH)
        // 달력 호출
        val datePicker: DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            // 1월은 0부터 시작해서 +1해주기
            val tMonth: Int = month +1
            // 년, 월, 일
//            val date: String = "$year/$tMonth/$dayOfMonth"
            val date: String = "${year}년 ${tMonth}월 ${dayOfMonth}일"
            // 화면에 선택한 날짜 보여주기
            val adminwdate = findViewById<TextView>(R.id.admin_wdate)
            adminwdate.text = date
        }, iYear, iMonth, iDay)
        // 달력호출
        datePicker.show()
    }
    // 사진첨부 결과 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 결과 코드 OK, 결과값 null 아니면
        if(it.resultCode == RESULT_OK) {
            // 멀티 선택 이미지는 clipData로..
            if(it.data!!.clipData != null) {    // 멀티 이미지
                // 선택한 이미지 갯수
                val count = it.data!!.clipData!!.itemCount
                for (index in 0 until count) {
                    if (count < 4) {
                        // 선택한 이미지 순서대로 담기
                        val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                        // 이미지 추가
                        imageList.add(imageUri)
                    } else {
                        Toast.makeText(this, "3장까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
//            else {     // 싱글 이미지
//                val imageUri = it.data!!.data
//                imageList.add(imageUri!!)
//            }
            adminGalleryAdapter.notifyDataSetChanged()
        }
//        // 사진 촬영 결과 가져오기
//        else if(it.resultCode == RESULT_OK && it.data != null) {
//            // 결과 값 담기
//            val extras = it.data!!.extras
//            // bitmap으로 타입 변경
//            bitmap = extras?.get("data") as Bitmap
//            // 화면에 보여주기
//            imageList.setImageBitmap(bitmap)
//
//            adminGalleryAdapter.notifyDataSetChanged()
//        }
    }
    // 사진촬영 결과 가져오기
    private val activitycameraResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 사진 촬영 결과 가져오기
        if(it.resultCode == RESULT_OK && it.data != null) {
            // 결과 값 담기
            val extras = it.data!!.extras
            // bitmap으로 타입 변경
            bitmap = extras?.get("data") as Bitmap
            // 화면에 보여주기
            imageList.setImageBitmap(bitmap)

            adminGalleryAdapter.notifyDataSetChanged()
        }
    }
    // img(에뮬레이터 메모리 img)를 Firebase에 올려주는 메서드
    fun imgUpload(key: String) {
        // FireStorage사용은 imgUpload메서드 안에서만 사용하니 지역변수로 선언
        val storage = Firebase.storage
        // 저장함 : Create a storage reference from our app
        val storageRef = storage.reference
        // 저장함 경로 : Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("$key.png")
        // Get the data from an ImageView as bytes
        adminwphoto = findViewById<TextView>(R.id.admin_wphoto)
        adminwphoto.isDrawingCacheEnabled = true
        adminwphoto.buildDrawingCache()
        val bitmap = imageList[0]
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putFile(bitmap)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d("my", "imgUpload: fail")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Log.d("my", "imgUpload: ok")
        }
    }
}
private fun <E> ArrayList<E>.setImageBitmap(bitmap: Bitmap) {

}

