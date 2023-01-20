package com.example.ev_check.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.R
import java.text.SimpleDateFormat
import java.util.*

class CommunityAdapter(private val context: Context?): RecyclerView.Adapter<CommunityAdapter.CommuViewHolder>() {
    // 데이터 담을 리스트
    var commuList = ArrayList<Community>()
    // 커스텀 리스너
    // Adpater안에 OnItemClickListener를 Interface로 만들어주고, ViewHolder에서 연결..
    interface OnItemClickListener{
        fun onItemClick(v: View, pos : Int)
    }
    // 객체 저장 변수
    private var itemClicklistener : OnItemClickListener? = null
    // 객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.itemClicklistener = onItemClickListener
    }
    // 화면설정 : 아이템 레이아웃을 객체화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.community_item, parent, false)
        return CommuViewHolder(view)
    }
    // 데이터 설정
    override fun onBindViewHolder(holder: CommuViewHolder, position: Int) {
        // 아이템 담기
        holder.bind(commuList[position])
    }
    // 리스트 사이즈 반환
    override fun getItemCount(): Int = commuList.size

    inner class CommuViewHolder(commuView: View):  RecyclerView.ViewHolder(commuView) {
        private val commulayout: LinearLayout = commuView.findViewById(R.id.commu_layout)
        private val commuitem: TextView = commuView.findViewById(R.id.commu_item)
        private val commutitle: TextView = commuView.findViewById(R.id.commu_title)
        private val communame: TextView = commuView.findViewById(R.id.commu_name)
        private val commudate: TextView = commuView.findViewById(R.id.commu_date)
        private val commucount: TextView = commuView.findViewById(R.id.commu_count)
        private val commucomments: TextView = commuView.findViewById(R.id.commu_comments)
        private val commuokay: TextView = commuView.findViewById(R.id.commu_okay)
        fun bind(community: Community) {
//            Glide.with()
            commuitem.text = community.item
            commutitle.text = community.title
            communame.text = community.name
            commudate.text = SimpleDateFormat("yyyy/MM/dd").format(Date(community.date))
            commucount.text = community.count.toString()
            commucomments.text = community.comments.toString()
            commuokay.text = community.okay.toString()
            // Adapter 내부 Class인 ViewHolder bind 함수 안에 적어주기!
            commulayout.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION ) {
                   itemClicklistener?.onItemClick(commulayout, pos)
                }
            }
        }
    }
}