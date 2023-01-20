package com.example.ev_check.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.example.ev_check.activity.Admin
import com.example.ev_check.activity.AdminAdapter
import com.example.ev_check.activity.ContentAdmin
import com.example.ev_check.activity.WriteAdmin
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class CarAdminFragment : Fragment(), MainActivity.onBackPressedListener {
    val adminList = ArrayList<Admin>()
    private var adminAdapter: AdminAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate한 loyout을 지속적으로 사용할 수 있도록 저장...
        // 현재 이곳은 Activity가 아니기에!! fragment_home에 접근하려면 xml코드를 눈에 보이는 view로 바꿔주어야함.
        // inflater.intlate를 통해서 가능...
        // 바로 retrue를 하게 되면 사용이 불가능 하기에 view라는 변수에 저장해놓고 사용해야함.
        var view = inflater.inflate(R.layout.fragment_car_admin, container, false)
        // 초기화
        val fragmentadimLayout = view.findViewById<ConstraintLayout>(R.id.fragment_adimLayout)
        val back : ImageView = view.findViewById(R.id.img_back)
        val adminsearch = view.findViewById<ImageView>(R.id.admin_search)
        val adminsearchlist = view.findViewById<ImageView>(R.id.admin_searchlist)
        val adminrecyclerview = view.findViewById<RecyclerView>(R.id.admin_RecyclerView)
        val adminwrite = view.findViewById<FloatingActionButton>(R.id.admin_write)
        //뒤로가기버튼
        back.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        // 검색 버튼 클릭시..
        adminsearch.setOnClickListener {
            Toast.makeText(this.context, "검색 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
        // 검색리스트 버튼 클릭시..
        adminsearchlist.setOnClickListener {
            Toast.makeText(this.context, "검색리스트 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
        // 글 작성 버튼 클릭시..
        adminwrite.setOnClickListener {
            Toast.makeText(this.context, "작성 버튼 클릭!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, WriteAdmin::class.java)
            startActivity(intent)
        }
        // recyclerView 설정
        adminrecyclerview.layoutManager = LinearLayoutManager(this.context)
        adminrecyclerview.setHasFixedSize(true)
        // Adapter 설정
        adminAdapter = AdminAdapter(context)
        // Adapter 적용
        adminrecyclerview.adapter = adminAdapter
        // 샘플데이터 생성
        getData()
        // 적용
//        adminrecyclerview.notifyDataSetChanged()
        // 아이템 클릭시..
        val intent = Intent(this.context, ContentAdmin::class.java)
        adminAdapter?.setOnItemClickListener(object : AdminAdapter.OnItemClickListener{
            override fun onItemClick(v: View, pos : Int) {
//                Intent(this@CarAdminFragment, ContentAdmin::class.java).apply {
                intent.putExtra("data", adminList[pos].item)
                intent.putExtra("data", adminList[pos].date)
                intent.putExtra("data", adminList[pos].title)
                intent.putExtra("data", adminList[pos].price)
//                intent.putExtra("data", adminList[pos].selected)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })
        // Inflate the layout for this fragment
        return view
    }
    // RecyclerView 안에 쌓일 데이터
    private fun getData() {
        adminList.apply {
            add(Admin("충전", "2022/9/8", "충전", "9870", "", false))
            add(Admin("정비", "2022/9/12", "타이어 교체", "90000", "", false))
            add(Admin("충전", "2022/9/19", "충전", "15360", "", false))
            add(Admin("충전", "2022/9/28", "충전", "20140", "", false))
            add(Admin("충전", "2022/10/3", "충전", "13670", "", false))
            add(Admin("충전", "2022/10/10", "충전", "7320", "", false))
            add(Admin("충전", "2022/10/16", "충전", "9120", "", false))
            add(Admin("충전", "2022/10/23", "충전", "18790", "", false))
            add(Admin("정비", "2022/10/31", "자동차 정기점검", "30000", "", false))
            add(Admin("충전", "2022/11/9", "충전", "7890", "", false))
            add(Admin("충전", "2022/11/18", "충전", "14520", "", false))
            add(Admin("충전", "2022/11/27", "충전", "9730", "", false))

            adminAdapter?.adminList = adminList
            adminAdapter?.notifyDataSetChanged()
        }
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