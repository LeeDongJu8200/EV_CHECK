package com.example.ev_check.fragment

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.ev_check.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory


/* ----------- Rest API 통신용 객체 선언 ----------- */

var text = "" // 테스트용 text파일

var statNm = mutableListOf<String>() // 충전소명
var addr = mutableListOf<String>() // 소재지 도로명 주소
var lat = mutableListOf<String>() // 위도
var lng = mutableListOf<String>() // 경도

var chgerType = mutableListOf<String>() // 충전기타입
var stat = mutableListOf<String>() // 충전기상태
var useTime = mutableListOf<String>() // 이용가능시간
var busiId = mutableListOf<String>() // 기관ID
var busiNm = mutableListOf<String>() // 운영기관명
var busiCall = mutableListOf<String>() // 관리업체 전화번호
var parkingFree = mutableListOf<String>() // 주차료무료 여부
var note = mutableListOf<String>() // 충전소 안내

class MapFragment : Fragment() {

    /* ----------- 전역변수 선언 영역 ----------- */

    lateinit var binding: FragmentMapBinding // 뷰 바인딩
    lateinit var mapView: MapView // 카카오 지도 뷰
    val eventListener = MarkerEventListener(this)

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback

    // 초기 카메라 이동용 인공사 위치 코드
    var firstlat: Double = 35.1467267
    var firstlon: Double = 126.922157

    // 마커 표시 정보 그룹핑 변수

    lateinit var hashStatNm : List<String>
    lateinit var hashAddr : List<String>
    lateinit var hashLat : List<String>
    lateinit var hashLng : List<String>

    lateinit var hashChgerType: Collection<List<String>>
    lateinit var hashStat: Collection<List<String>>
    lateinit var hashUseTime : Collection<List<String>>
    lateinit var hashBusiId : Collection<List<String>>
    lateinit var hashBusiNm : Collection<List<String>>
    lateinit var hashBusiCall : Collection<List<String>>
    lateinit var hashParkingFree : Collection<List<String>>
    lateinit var hashNote : Collection<List<String>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* ----------- 변수 선언 및 초기화 영역 ----------- */
        
        // 프래그먼트의 View 객체 inflate
        // 뷰 바인딩 사용
        binding = FragmentMapBinding.inflate(layoutInflater)
        val view = binding.root

        // 슬라이딩 레이아웃 바인딩 및 이벤트 리스너 추가
        val slidePanel = binding.mainFrameLayout
        slidePanel.addPanelSlideListener(PanelEventListener())

        // 맵뷰 초기화
        mapView = MapView(requireContext())
        mapView.setPOIItemEventListener(eventListener)

        // btnDirectionTracking 토글용 boolean 변수
        var isCheck = true


        /* ----------- 지도 영역 ----------- */
        
        // 카카오지도 표시
        binding.mapViewContainer.addView(mapView)
        
        // 실시간 위치 업데이트
//        startLocationUpdates()
        
//        // 앱 시작과 동시에 초기위치로 카메라 이동 + 줌인
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(firstlat,firstlon),1,false)

        // 앱 시작시 위치 추적
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading


        /* ----------- Rest API 영역 ----------- */

        // API key값 (인증키)
        val key = "2680%2Bqf3cT4Io2pS%2BAP7WbASYKfnMEzIWIqJZpszpKjbA%2FSNZ9HbeR8Z40kl24TH4qAGbl4XnWt1xPqeM3Qylw%3D%3D"
        // 페이지번호
        val pageNo = "&pageNo=1"
        // 한 페이지 결과 수 (최소 10, 최대 9999)
        val numOfRows ="&numOfRows=10"
        // 지역구분코드(행정구역코드 앞 2자리, 서울 11, 광주 29)
        val zcode = "&zcode=29"
        // 전체 URL
        val url = "http://apis.data.go.kr/B552584/EvCharger/getChargerInfo?serviceKey="+key+pageNo+numOfRows+zcode
        Log.d("restAPI", url)

        // 쓰레드 생성
        val thread = Thread(NetworkThread(url))
        thread.start() // 쓰레드 시작
        thread.join() // 멀티 작업 안되게 하려면 start 후 join 입력


        /* ----------- 데이터 관리 영역 ----------- */

        // 중복 제어용 리스트 생성
        hashStatNm = statNm.distinct()
        hashAddr = addr.distinct()
        hashLat = lat.distinct()
        hashLng = lng.distinct()

        hashChgerType = chgerType.groupBy { it.split("@")[0] }.values
        hashStat = stat.groupBy { it.split("@")[0] }.values
        hashUseTime = useTime.groupBy { it.split("@")[0] }.values
        hashBusiId = busiId.groupBy { it.split("@")[0] }.values
        hashBusiNm = busiNm.groupBy { it.split("@")[0] }.values
        hashBusiCall = busiCall.groupBy { it.split("@")[0] }.values
        hashParkingFree = parkingFree.groupBy { it.split("@")[0] }.values
        hashNote = note.groupBy { it.split("@")[0] }.values


        // 마커 표시
        for (i in 0..hashStatNm.size-1){
            // Rest API에서 가져온 데이터로 마커 표시
            val marker = MapPOIItem()
            marker.apply {
                itemName = hashStatNm[i]
                marker.tag = i
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(hashLat[i].toDouble(), hashLng[i].toDouble())
                marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                marker.selectedMarkerType =
                    MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            }
            mapView.addPOIItem(marker) // 맵뷰에 마커 생성

        }


        /* ----------- 이벤트 영역 ----------- */

        // btnDirectionTracking : 방향추적 버튼
//        binding.btnDirectionTracking.setOnClickListener {
//
//            if (isCheck){ // 방향 추적 기능이 꺼져 있을 때 (기본값)
//                // 방향 추적기능 켜기
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
//                mapView.setZoomLevel(1, true) // 줌레벨 변경 이벤트
//                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_clicked) // **버튼 이미지 변경 이벤트, 멘토링 필요
//
//            }else{
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving
//                mapView.setZoomLevel(1, true)
//                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_normal)
//
//            }
//
//            // 토글 전환
//            isCheck = !isCheck
//
//        }

        /* ----------- 기타 영역 ----------- */

        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        /* ----------- 테스트 영역 (사용 후 삭제) ----------- */

//        // **화면이 켜졌을 때 다시 지도 동작하는 코드 구현, 멘토링 필요
//        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
//        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
//        val receiver = object: BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val action = intent!!.action
//                Log.d("Test", "receive : $action")
//
//                when (action) {
//                    Intent.ACTION_SCREEN_ON -> {
//                        // do something
//                    }
//                    Intent.ACTION_SCREEN_OFF -> {
//                        // do something
//                    }
//                }
//            }
//        }

//        registerReceiver(receiver, intentFilter);

        // 기능 테스트용 임시 코드 - 마커 세팅
//        val marker = MapPOIItem()
//        marker.apply {
//            itemName = "Default Marker"
//            marker.tag = 0
//            marker.mapPoint = MapPoint.mapPointWithGeoCoord(35.1467267, 126.922157)
//            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
//            marker.selectedMarkerType =
//                MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//        }
//        mapView.addPOIItem(marker)

//        // 테스트용 버튼클릭이벤트
//        binding.btnDirectionTracking.setOnClickListener {
//
//            val slidePanel = binding.mainFrameLayout
//            val state = slidePanel.panelState
//
//            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
//            }
//            // 열린 상태일 경우 닫기
//            else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
//            }
//
//            // 쓰레드에서 가져온 api 정보 텍스트에 뿌려주기
//            binding.tvAPI.text = text
//            Log.d("APItest", statNm.toString())
//
//        }



        
        /* ----------- View 리턴 ----------- */

        return view
    }


    /* ----------- 함수, 메서드, 리스너, 기타기능 영역 ----------- */

    // Rest API 통신용 Class
    class NetworkThread(var url: String): Runnable {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun run() {
            try {
                val xml : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url)
                xml.documentElement.normalize()

                // 찾고자 하는 데이터가 어느 노드 아래에 있는지 확인
                val list: NodeList = xml.getElementsByTagName("item")

                // list.length-1 만큼 얻고자 하는 태그의 정보를 가져옴
                for(i in 0..list.length-1){
                    val n: Node = list.item(i)
                    if(n.getNodeType() == Node.ELEMENT_NODE){
                        val elem = n as Element
                        val map = mutableMapOf<String,String>()

                        // 데이터 통신 반복문
                        for(j in 0..elem.attributes.length - 1) {
                            map.putIfAbsent(elem.attributes.item(j).nodeName, elem.attributes.item(j).nodeValue)
                        }


                        // 프래그먼트 중복 방지를 위한 if문
                        if (statNm.size < list.length){
                            // 충전 '소' 정보, 이후 distinct()로 중복 제거
                            statNm.add(elem.getElementsByTagName("statNm").item(0).textContent)
                            addr.add(elem.getElementsByTagName("addr").item(0).textContent)
                            lat.add(elem.getElementsByTagName("lat").item(0).textContent)
                            lng.add(elem.getElementsByTagName("lng").item(0).textContent)

                            // 충전 '기' 정보, 구분을 위해 앞에 statNm을 포함
                            chgerType.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("chgerType").item(0).textContent)
                            stat.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("stat").item(0).textContent)
                            useTime.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("useTime").item(0).textContent)
                            busiId.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("busiId").item(0).textContent)
                            busiNm.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("busiNm").item(0).textContent)
                            busiCall.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("busiCall").item(0).textContent)
                            parkingFree.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("parkingFree").item(0).textContent)
                            note.add(elem.getElementsByTagName("statNm").item(0).textContent +"@"+ elem.getElementsByTagName("note").item(0).textContent)
                        }


                    }
                }


            } catch (e: Exception) {
                Log.d("API", "오픈API"+e.toString())
            }
        }

    }


    // 슬라이딩 패널 이벤트 리스너
    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        // 패널이 슬라이드 중일 때
        override fun onPanelSlide(panel: View?, slideOffset: Float) {

        }

        // 패널의 상태가 변했을 때
        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {

            } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {

            }
        }
    }


    // 마커 클릭 이벤트 리스너
    class MarkerEventListener(val context: MapFragment): MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            val slidePanel = context.binding.mainFrameLayout
            val state = slidePanel.panelState

            var mChgerType = chgerType.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList()
            for (i in 0..mChgerType.size-1){
                mChgerType[i] = (mChgerType[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@",""))

            }

            Log.d("ttts", mChgerType.toString())

            // 마커 클릭시
            text = "" // 테스트용 임시 text
            text += "${poiItem?.tag.toString()}번 충전소 \n"
            text += "1. 충전소명 : ${poiItem?.itemName.toString()} \n"
            text += "2. 충전소 주소 : ${context.hashAddr.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
            text += "3. 충전기 갯수 : ${chgerType.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].size} \n"
            text += "4. 충전기 타입 : ${mChgerType} \n"
            text += "5. 충전기 상태 : ${context.hashStat.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
            text += "6. 이용가능 시간 : ${context.hashUseTime.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
            text += "7. 관리업체 전화번호 : ${context.hashBusiCall.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
            text += "8. 무료주차 여부 : ${context.hashParkingFree.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
            text += "9. 특이사항 : ${context.hashNote.toTypedArray()[poiItem?.tag!!.toInt()]} \n"



            // 닫힌 상태일 경우 열기
            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED

                context.binding.tvAPI.text = text
            }
            // 열린 상태일 경우 닫기
            else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

                text = ""
            }
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭 시 (Deprecated)
            // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            // 말풍선 클릭 시

            context.binding.tvAPI.text = text
            Log.d("APItest", statNm.toString())
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }


//    // 실시간 위치 갱신 함수
//    private fun startLocationUpdates() {
//        // 권한 체크
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//
//        mFusedLocationProviderClient!!.lastLocation
//            .addOnSuccessListener { mLastLocation->
//                if(mLastLocation == null) {
////                    Log.e("TAG", "location get fail")
//                } else {
////                    Log.d("TAG", "${mLastLocation.latitude} , ${mLastLocation.longitude}")
//                }
//            }
//            .addOnFailureListener {
//                Log.e("TAG", "location error is ${it.message}")
//                it.printStackTrace()
//            }
//
//        mLocationRequest = LocationRequest.create()
//        mLocationRequest.run {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = 1000 // 갱신주기
//        }
//
//        mLocationCallback = object: LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult?.let {
//                    for((i, location) in it.locations.withIndex()) {
//                        lat = location.latitude
//                        lon = location.longitude
////                        Log.d("현재좌표", "#$i ${location.latitude} , ${location.longitude}")
//                    }
//                }
//            }
//        }
//        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
//    }

    // onPause : 다른 Activity 활성화시 호출
   override fun onPause() {
        super.onPause()
        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

}
