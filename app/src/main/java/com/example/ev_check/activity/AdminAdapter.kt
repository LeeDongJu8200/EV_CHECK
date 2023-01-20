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
import kotlin.collections.ArrayList

class AdminAdapter(private val context: Context?): RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {
    // 데이터 담을 리스트
    var adminList = ArrayList<Admin>()
    // 커스텀 리스너
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item, parent, false)
        return AdminViewHolder(view)
    }
    // 데이터 설정
    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        // 아이템 담기
        holder.bind(adminList[position])
    }
    // 리스트 사이즈 반환
    override fun getItemCount(): Int = adminList.size

    inner class AdminViewHolder(adminView: View): RecyclerView.ViewHolder(adminView) {
        private val adminlayout: LinearLayout = adminView.findViewById(R.id.admin_layout)
        private val adminitem: TextView = adminView.findViewById(R.id.admin_item)
        private val admindate: TextView = adminView.findViewById(R.id.admin_date)
        private val admintitle: TextView = adminView.findViewById(R.id.admin_title)
        private val adminprice: TextView = adminView.findViewById(R.id.admin_price)
        fun bind(admin: Admin) {
//            Glide.with()
            adminitem.text = admin.item
            admindate.text = SimpleDateFormat("yyyy/MM/dd").format(Date(admin.date))
            admintitle.text = admin.title
            adminprice.text = admin.price

            // Adapter 내부 Class인 ViewHolder bind 함수 안에 적어주기!
            adminlayout.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION ) {
                    itemClicklistener?.onItemClick(adminlayout, pos)
                }
            }

        }
    }
}
