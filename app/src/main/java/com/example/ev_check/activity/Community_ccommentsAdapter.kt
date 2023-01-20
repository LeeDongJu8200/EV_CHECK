package com.example.ev_check.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Community_ccommentsAdapter(val context: Context?) : RecyclerView.Adapter<Community_ccommentsAdapter.Community_ccommentsViewHolder>() {
    // 데이터 담을 리스트
    var community_ccommentsList = ArrayList<Community_ccomments>()

    // 커스텀 리스너..
    // Adpater안에 OnItemClickListener를 Interface로 만들어주고, ViewHolder에서 연결..
    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)
    }
    // 객체 저장 변수
    private var itemClicklistener : OnItemClickListener? = null
    // 객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.itemClicklistener = onItemClickListener
    }
    // 화면설정 : 아이템 레이아웃을 객체화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Community_ccommentsAdapter.Community_ccommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.community_comments_item, parent, false)
        return Community_ccommentsViewHolder(view)
    }
    // 데이터 설정
    override fun onBindViewHolder(holder: Community_ccommentsAdapter.Community_ccommentsViewHolder, position: Int) {
        // 아이템 담기
        holder.bind(community_ccommentsList[position])
    }
    // 리스트 사이즈 반환
    override fun getItemCount(): Int = community_ccommentsList.size

    inner class Community_ccommentsViewHolder (community_ccommentsView: View): RecyclerView.ViewHolder(community_ccommentsView) {
        private val commuccmslayout: LinearLayout = community_ccommentsView.findViewById(R.id.commu_ccomments_layout)
        private val commuccmsitem: ImageView = community_ccommentsView.findViewById(R.id.commu_ccomments_item)
        private val commuccmsname: TextView = community_ccommentsView.findViewById(R.id.commu_ccomments_name)
        private val commuccmsdate: TextView = community_ccommentsView.findViewById(R.id.commu_ccomments_date)
        private val commuccmscontent: TextView = community_ccommentsView.findViewById(R.id.commu_ccomments_content)
        fun bind(community_ccomments: Community_ccomments) {
//            commuccmsitem.text = community_ccomments.item
            commuccmsname.text = community_ccomments.name
            commuccmsdate.text = SimpleDateFormat("yyyy/MM/dd").format(Date(community_ccomments.date))
            commuccmscontent.text = community_ccomments.content
//            // Adapter 내부 Class인 ViewHolder bind 함수 안에 적어주기!
//            commuccmslayout.setOnClickListener {
//                val pos = adapterPosition
//                if (pos != RecyclerView.NO_POSITION ) {
//                    itemClicklistener?.onItemClick(commuccmslayout, pos)
//                }
//            }
        }
    }
}