package com.example.ev_check

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ev_check.fragment.MapFragment
import net.daum.mf.map.api.MapPoint

class SearchViewAdapter(var evstation: ArrayList<EvStation>, var con: MapFragment) :
    RecyclerView.Adapter<SearchViewAdapter.ViewHolder>(), Filterable {

    /* ----------- 전역변수 선언 영역 ----------- */
    var TAG = "StationListAdapter" // logd 태그용
    
    var filteredEvstations = ArrayList<EvStation>() // 필터링
    var itemFilter = ItemFilter() // 아이템 필터

    /* ----------- 이너 클래스 뷰홀더 선언 ----------- */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        // 리사이클러뷰에 나타날 정보, 추가 가능
        var imgPin: ImageView
        var tvStatNmForListView: TextView
        var tvAddrForListView: TextView
        var tvDistance: TextView

        init {
            imgPin = itemView.findViewById(R.id.imgPin)
            tvStatNmForListView = itemView.findViewById(R.id.tvStatNmForListView)
            tvAddrForListView = itemView.findViewById(R.id.tvAddrForListView)
            tvDistance = itemView.findViewById(R.id.tvDistance)

            // 리스트 아이템 클릭 이벤트 리스너
            itemView.setOnClickListener {
                var position = adapterPosition
                var evStation = filteredEvstations[position]
                Log.d(TAG, evStation.statNm)
                Log.d(TAG, evStation.addr)
                Log.d(TAG, evStation.lat)
                Log.d(TAG, evStation.lng)
                Log.d(TAG, evStation.distance)

                // 리스트 클릭시 해당 위치로 시점 이동
                con.mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(evStation.lat.toDouble(), evStation.lng.toDouble()),1,true)

                // 서치뷰 텍스트 초기화, 리사이클러뷰 가리기
                con.binding.svSearch.setQuery("", false)
                con.binding.svView.isInvisible = true

            }
        }
    }

    /* ----------- init 및 override 선언 ----------- */

    init {
        filteredEvstations.addAll(evstation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val con = parent.context
        val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_searchview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evStation: EvStation = filteredEvstations[position]

        // 이미지 작업의 경우 glide를 사용해 server의 image를 불러올 수 있음
        holder.tvStatNmForListView.text = evStation.statNm
        holder.tvAddrForListView.text = evStation.addr
        holder.tvDistance.text = evStation.distance
    }

    override fun getItemCount(): Int {
        return filteredEvstations.size
    }

    /* ----------- 필터 관련 기능 영역 ----------- */
    override fun getFilter(): Filter {
        return itemFilter
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filterString = charSequence.toString()
            val results = FilterResults()
            Log.d(TAG, "charSequence : $charSequence")

            // 검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<EvStation> = ArrayList<EvStation>()

            // 공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = evstation
                results.count = evstation.size

                return results

            // 공백제외 2글자 이하인 경우 -> 충전소명만으로
            } else if (filterString.trim { it <= ' ' }.length <= 2) {
                for (evstation in evstation) {
                    if (evstation.statNm.contains(filterString)) {
                        filteredList.add(evstation)
                    }
                }

            // 그 외의 경우(공백제외 2글자 초과) -> 충전소명/주소로 검색
            } else {
                for (evstation in evstation) {
                    if (evstation.statNm.contains(filterString) || evstation.addr.contains(filterString)) {
                        filteredList.add(evstation)
                    }
                }
            }

            results.values = filteredList
            results.count = filteredList.size

            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredEvstations.clear()
            filteredEvstations.addAll(filterResults.values as ArrayList<EvStation>)
            notifyDataSetChanged()
        }
    }


}