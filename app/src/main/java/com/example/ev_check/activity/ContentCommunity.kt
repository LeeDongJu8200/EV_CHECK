package com.example.ev_check.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.activity.Community_ccomments
import com.example.ev_check.activity.Community_ccommentsAdapter
import com.example.ev_check.fragment.CarAdminFragment
import com.example.ev_check.R

class ContentCommunity : AppCompatActivity() {
    var commucokaycheckBoolean = true
    val community_ccommentsList = ArrayList<Community_ccomments>()
    private var community_ccommentsAdapter: Community_ccommentsAdapter? = null
//    private lateinit var commuccommentstext: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_community)
        // 초기화
        val back : ImageView = findViewById(R.id.img_back)
        val commucwrite = findViewById<TextView>(R.id.commu_cwrite)
        val commucdelete = findViewById<TextView>(R.id.commu_cdelete)
        val commucscroll = findViewById<NestedScrollView>(R.id.commu_cscroll)
        val commucitem = findViewById<TextView>(R.id.commu_citem)
        val commuctitle = findViewById<TextView>(R.id.commu_ctitle)
        val commucphoto = findViewById<ImageView>(R.id.commu_cphoto)
        val commucname = findViewById<TextView>(R.id.commu_cname)
        val commucdate = findViewById<TextView>(R.id.commu_cdate)
        val commuccount = findViewById<TextView>(R.id.commu_ccount)
        val commucokaycheck = findViewById<ImageView>(R.id.commu_cokaycheck)
        val commucokay = findViewById<TextView>(R.id.commu_cokay)
        val commuccomments = findViewById<TextView>(R.id.commu_ccomments)
        val commuccontent = findViewById<TextView>(R.id.commu_ccontent)
        val commucphotoList = findViewById<RecyclerView>(R.id.commu_cphotoList)
        val commucplace = findViewById<LinearLayout>(R.id.commu_cplace)
        val commucplace1 = findViewById<TextView>(R.id.commu_cplace1)
        val commucplace2 = findViewById<TextView>(R.id.commu_cplace2)
        val commuccommentstext = findViewById<EditText>(R.id.commu_ccommentstext)
        val commuccommentswrite = findViewById<ImageView>(R.id.commu_ccommentswrite)
        val commuccommentsrecyclerview = findViewById<RecyclerView>(R.id.commu_ccommentsrecyclerview)

        // recyclerView 설정
        commuccommentsrecyclerview.layoutManager = LinearLayoutManager(this)
        commuccommentsrecyclerview.setHasFixedSize(true)
        // Adapter 설정
        val context = null
        community_ccommentsAdapter = Community_ccommentsAdapter(context)
        // Adapter 적용
        commuccommentsrecyclerview.adapter = community_ccommentsAdapter
        // 샘플데이터 생성
        getData()
        // 적용
//        adminrecyclerview.notifyDataSetChanged()

        //뒤로가기버튼
        back.setOnClickListener {
            finish()
        }
        // 수정 클릭시
        commucwrite.setOnClickListener {
            Toast.makeText(this, "수정 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
            val comcitem: String = commucitem.text.toString()
            val comctitle: String = commuctitle.text.toString()
            val comccontent: String = commuccontent.text.toString()
//            val commucphotoList: String = commucphotoList.text.toString()
//            val comcplace: String = commucplace.text.toString()
            // 데이터 담기
            bundle.putString("comcitem", comcitem)
            bundle.putString("comctitle", comctitle)
            bundle.putString("comccontent", comccontent)
//            bundle.putString("commucphotoList", commucphotoList)
//            bundle.putString("comcplace", comcplace)
            val intent = Intent(this, WriteCommunity::class.java)
            // 인텐트 선언
            startActivity(intent)
        }
        // 삭제 버튼 클릭시
        commucdelete.setOnClickListener {
            Toast.makeText(this, "수정 버튼 클릭!", Toast.LENGTH_SHORT).show()
            // 데이터를 담을 객체 생성
            val bundle: Bundle = Bundle()
            // 입력 메시지
            val comcitem: String = commucitem.text.toString()
            val comctitle: String = commuctitle.text.toString()
            val comccontent: String = commuccontent.text.toString()
//            val commucphotoList: String = commucphotoList.text.toString()
//            val comcplace: String = commucplace.text.toString()
            // 데이터 담기
            bundle.putString("comcitem", comcitem)
            bundle.putString("comctitle", comctitle)
            bundle.putString("comccontent", comccontent)
//            bundle.putString("commucphotoList", commucphotoList)
//            bundle.putString("comcplace", comcplace)
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
        }
        // 관심 버튼 클릭시
        commucokaycheck.setOnClickListener {
            Toast.makeText(this, "관심 버튼 클릭!", Toast.LENGTH_SHORT).show()
            if (commucokaycheckBoolean) {
                commucokaycheck.setImageResource(R.drawable.group69)
                commucokaycheckBoolean = false
            } else {
                commucokaycheck.setImageResource(R.drawable.group66)
                commucokaycheckBoolean = true
            }
        }
        // 댓글 버튼 클릭시
        commuccommentswrite.setOnClickListener {
            Toast.makeText(this, "댓글 추가.", Toast.LENGTH_SHORT).show()
//            // 입력 값 변수에 담기
//            val commentstext: String = commuccommentstext.text.toString()
//            if(commentstext.isNotEmpty()) {
//                // 데이터 담기
//                community_ccommentsList.add(commentstext)
//                community_ccommentsAdapter?.notifyDataSetChanged()
//            } else {
//                Toast.makeText(this, "댓글을 작성해주세요.", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun getData() {
        community_ccommentsList.apply {
            add(Community_ccomments(R.drawable.squirrel,"류데렐라","2022/10/19", "와~부러워유~~~\n저도 전기차 바꾸고 싶어요ㅜㅜ", false))
            add(Community_ccomments(R.drawable.cat,"귀요미귀요미","2022/10/19", "전기차 너무 좋으네요\n차 바꾼지 1년인데 또 바꾸고 싶네요", false))
            add(Community_ccomments(R.drawable.puppy,"원유니","2022/10/19", "맞아요 너무 좋아요ㅋ\n막 타고 다녀도 기름 걱정 안해도 되고요~", false))
            add(Community_ccomments(R.drawable.rabbit,"ililililil","2022/10/19", "와 혹하네요~~", false))
            community_ccommentsAdapter?.community_ccommentsList = community_ccommentsList
            community_ccommentsAdapter?.notifyDataSetChanged()
        }
    }
    //    fun sendRequest1() {
//        // Volley Lib 새로운 요청객체 생성
//        queue = Volley.newRequestQueue(getApplicationContext())
//        // 서버에 요청할 주소
//        val url: String = server_url + "/reviewSearch"
//        // 요청 문자열 저장
//        stringRequest = object : StringRequest(Request.Method.POST, url, object : Listener<String?>() {
//            // 응답데이터를 받아오는 곳
//            fun onResponse(response: String) {
//                val review_data = response.split("`").toTypedArray()
//                recyclerView = findViewById<View>(R.id.recyclerview05) as RecyclerView
//                recyclerView.setLayoutManager(
//                    LinearLayoutManager(
//                        getApplicationContext(),
//                        RecyclerView.VERTICAL,
//                        false
//                    )
//                )
//                adapter = ReviewAdapter()
//                for (i in review_data.indices) {
//                    adapter.addItem(review_data[i])
//                }
//                recyclerView.setAdapter(adapter)
//            }
//        }, object : ErrorListener() {
//            // 서버와의 연동 에러시 출력
//            fun onErrorResponse(error: VolleyError) {
//                error.printStackTrace()
//            }
//        }) {
//            //response를 UTF8로 변경해주는 소스코드
//            protected fun parseNetworkResponse(response: NetworkResponse): Response<String?>? {
//                return try {
//                    val utf8String = String(response.data, "UTF-8")
//                    Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response))
//                } catch (e: UnsupportedEncodingException) {
//                    Response.error(ParseError(e))
//                } catch (e: Exception) {
//                    Response.error(ParseError(e))
//                }
//            }
//            // 보낼 데이터를 저장하는 곳
//            @get:Throws(AuthFailureError::class)
//            protected val params: Map<String, String>?
//                protected get() {
//                    val params: MutableMap<String, String> = HashMap()
//                    params["prog_seq"] = prog_data.get(0)
//                    Log.d("로그2보기", "dddd")
//                    return params
//                }
//        }
//        val TAG = "black"
//        stringRequest.setTag(TAG)
//        queue.add(stringRequest)
//    }
}

private fun <E> ArrayList<E>.add(element: String) {

}