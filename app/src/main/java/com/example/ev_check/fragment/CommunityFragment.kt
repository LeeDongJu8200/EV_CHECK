package com.example.ev_check.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.example.ev_check.activity.Community
import com.example.ev_check.activity.CommunityAdapter
import com.example.ev_check.activity.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CommunityFragment : Fragment(), MainActivity.onBackPressedListener {
    // TODO: Rename and change types of parameters
    val commuList = ArrayList<Community>()
    private var communityAdapter: CommunityAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        // Inflate한 loyout을 지속적으로 사용할 수 있도록 저장...
        // 현재 이곳은 Activity가 아니기에!! fragment_home에 접근하려면 xml코드를 눈에 보이는 view로 바꿔주어야함.
        // inflater.intlate를 통해서 가능...
        // 바로 retrue를 하게 되면 사용이 불가능 하기에 view라는 변수에 저장해놓고 사용해야함.
        var view = inflater.inflate(R.layout.fragment_community, container, false)
        // 초기화
        val fragmentcommuLayout = view.findViewById<ConstraintLayout>(R.id.fragment_commuLayout)
        val back : ImageView = view.findViewById(R.id.img_back)
        val commusearch = view.findViewById<ImageView>(R.id.commu_search)
        val commusearchlist = view.findViewById<ImageView>(R.id.commu_searchlist)
        val commurecycleview = view.findViewById<RecyclerView>(R.id.commu_RecyclerView)
        val commuwrite = view.findViewById<FloatingActionButton>(R.id.commu_write)
        //뒤로가기버튼
        back.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        // 검색 버튼 클릭시
        commusearch.setOnClickListener {
            Toast.makeText(this.context, "검색 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
        // 검색리스트 버튼 클릭시
        commusearchlist.setOnClickListener {
            Toast.makeText(this.context, "검색리스트 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
        // 글 작성 버튼 클릭시
        commuwrite.setOnClickListener {
            Toast.makeText(this.context, "작성 버튼 클릭!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, WriteCommunity::class.java)
            startActivity(intent)
        }
        // recyclerView 설정
        commurecycleview.layoutManager = LinearLayoutManager(this.context)
        commurecycleview.setHasFixedSize(true)
        // Adapter 설정
        communityAdapter = CommunityAdapter(context)
        // Adapter 적용
        commurecycleview.adapter = communityAdapter
        // 샘플데이터 생성
        getData()
        // 적용
//        adminrecyclerview.notifyDataSetChanged()
        // 아이템 클릭시..
        val intent = Intent(this.context, ContentCommunity::class.java)
        communityAdapter?.setOnItemClickListener(object : CommunityAdapter.OnItemClickListener{
            override fun onItemClick(v: View, pos : Int) {
//                Intent(this@CarAdminFragment, ContentAdmin::class.java).apply {
                intent.putExtra("data", commuList[pos].item)
                intent.putExtra("data", commuList[pos].title)
                intent.putExtra("data", commuList[pos].name)
                intent.putExtra("data", commuList[pos].date)
                intent.putExtra("data", commuList[pos].count)
                intent.putExtra("data", commuList[pos].comments)
                intent.putExtra("data", commuList[pos].okay)
//                intent.putExtra("data", commuList[pos].selected)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })
        // Inflate the layout for this fragment
        return view
    }
    // RecyclerView 안에 쌓일 데이터
    private fun getData() {
        commuList.apply {
            add(Community("일상공유", "타이틀1", "스카이", "2022/9/8", 4, 2, 1, false))
            add(Community("고장신고", "고장 신고1", "맘마", "2022/9/12", 8, 4, 3, false))
            add(Community("일상공유", "게시판 제목1", "카순이", "2022/9/19", 14, 2, 2, false))
            add(Community("일상공유", "게시판 머라고 쓰지?", "삼족오", "2022/9/28", 23, 1, 6, false))
            add(Community("일상공유", "와이파이 잘 되어 좋아요.", "터틀쉽", "2022/10/3", 12, 5, 12, false))
            add(Community("일상공유", "무료 충전 가능한 곳은?", "블랙", "2022/10/10", 67, 36, 54, false))
            add(Community("일상공유", "충전 저렴한 곳은 어디인가요?", "뉴코딩", "2022/10/16", 57, 22, 56, false))
            add(Community("일상공유", "타이틀2", "거북선", "2022/10/23", 5, 1, 1, false))
            add(Community("고장신고", "충전이 잘 안 되요", "잇다", "2022/10/31", 25, 12, 2, false))
            add(Community("일상공유", "게시판 제목2", "사랑", "2022/11/9", 45, 1, 1, false))
            add(Community("일상공유", "나는 충전 자동차에 대해 몰라요.", "또치", "2022/11/18", 4, 1, 0, false))
            add(Community("일상공유", "완충하면 이만큼 갈 수 있어요.", "천사맘", "2022/11/27", 73, 35, 3, false))

            communityAdapter?.commuList = commuList
            communityAdapter?.notifyDataSetChanged()
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