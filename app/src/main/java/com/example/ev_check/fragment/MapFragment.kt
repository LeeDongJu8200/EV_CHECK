package com.example.ev_check.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ev_check.*
import com.example.ev_check.R
import com.example.ev_check.databinding.FragmentMapBinding
import com.google.android.gms.location.*
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

var mLat:Double = 0.1 // 마커위도
var mLng:Double = 0.1 // 마커경도

// 뒤로가기 연속 클릭 대기 시간
var mBackWait:Long = 0

class MapFragment : Fragment(), MainActivity.onBackPressedListener {

    /* ----------- 전역변수 선언 영역 ----------- */

    lateinit var binding: FragmentMapBinding // 뷰 바인딩
    lateinit var mapView: MapView // 카카오 지도 뷰
    val eventListener = MarkerEventListener(this)

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback

    // ** 초기 카메라 이동용 인공사 위치 코드 - 멘토링 필요!!!
    // 임시로 인공사 위경도값을 직접 받아서 쏘고 있으나 앱 실행과 동시에 위경도 값을 받아오는 방법이 필요함
    // 현재 아래쪽에 주석처리해 놓은 것들은 실시간 처리는 되는데 앱 실행 1~2초 후 작동.
    var curlat: Double = 35.1467267
    var curlng: Double = 126.922157

    // 마커 표시 정보 그룹핑 변수
    lateinit var hashStatNm : List<String>
    lateinit var hashAddr : List<String>
    lateinit var hashLat : List<String>
    lateinit var hashLng : List<String>
    // 거리
    var hashDistance = mutableListOf<String>()
    lateinit var distance : String

//    lateinit var hashChgerType: Collection<List<String>>
//    lateinit var hashStat: Collection<List<String>>
//    lateinit var hashUseTime : Collection<List<String>>
//    lateinit var hashBusiId : Collection<List<String>>
//    lateinit var hashBusiNm : Collection<List<String>>
//    lateinit var hashBusiCall : Collection<List<String>>
//    lateinit var hashParkingFree : Collection<List<String>>
//    lateinit var hashNote : Collection<List<String>>



    // 서치뷰 관련 전역변수
    lateinit var searchViewAdapter: SearchViewAdapter
    lateinit var evstations: ArrayList<EvStation>


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
        binding.mapViewContainer.removeAllViews()
        mapView = MapView(requireContext())

        // 맵뷰 이벤트 리스너
        mapView.setPOIItemEventListener(eventListener)


        // btnMyLocation 토글용 boolean 변수
        var isCheck = true
        binding.btnMyLocation.bringToFront()


        // 서치뷰, 리사이클러뷰
        binding.svSearch.bringToFront() // 서치뷰를 지도 위로
        binding.svView.bringToFront() // 리사이클러뷰를 지도 위로
        binding.svView.isInvisible = true // 리사이클러뷰가 평소에는 안보이게

        // 서치뷰 텍스트 리스너(검색기능)
        binding.svSearch.setOnQueryTextListener(searchViewTextListener)


        /* ----------- 지도 영역 ----------- */

        // 카카오지도 표시
        binding.mapViewContainer.addView(mapView)
        
        // 실시간 위치 업데이트
        startLocationUpdates()

        var t: Double = 0.0
        var g: Double = 0.0

        try {
            t = MyApplication.prefs.getString("curlat", "").toDouble()
            g = MyApplication.prefs.getString("curlng", "").toDouble()
            Log.d("저장", ""+t)
            Log.d("저장", ""+g)
        } catch (e: Exception){

        }
        
        // 앱 시작과 동시에 초기위치로 카메라 이동 + 줌인
        if (t == 0.0){
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(curlat,curlng),1,false)
        } else {
            curlat = t
            curlng = g
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(curlat,curlng),1,false)
        }

        // 앱 시작시 위치 추적
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving


        /* ----------- Rest API 영역 ----------- */

        // API key값 (인증키)
        val key = "2680%2Bqf3cT4Io2pS%2BAP7WbASYKfnMEzIWIqJZpszpKjbA%2FSNZ9HbeR8Z40kl24TH4qAGbl4XnWt1xPqeM3Qylw%3D%3D"
        // 페이지번호
        val pageNo = "&pageNo=1"
        // 한 페이지 결과 수 (최소 10, 최대 9999)
        val numOfRows ="&numOfRows=30"
        // 지역구분코드(행정구역코드 앞 2자리, 서울 11, 광주 29)
        // ** 멘토링 필요! 현재 위치 값을 기반으로 지역을 파악하는 방법이 필요함
        // 위경도 범위로 일일이 하드코딩해서 범위에 따라 지역코드를 분할하는 방법을 고려중인데 더 좋은 방법이 있는지 알고싶음
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

        // 거리 체크용 임시코드, 수정필요
        for (i in 0..hashStatNm.size-1){
            getDistance(hashLat[i].toDouble(), hashLng[i].toDouble())
            hashDistance.add(distance)
        }


//        hashChgerType = chgerType.groupBy { it.split("@")[0] }.values
//        hashStat = stat.groupBy { it.split("@")[0] }.values
//        hashUseTime = useTime.groupBy { it.split("@")[0] }.values
//        hashBusiId = busiId.groupBy { it.split("@")[0] }.values
//        hashBusiNm = busiNm.groupBy { it.split("@")[0] }.values
//        hashBusiCall = busiCall.groupBy { it.split("@")[0] }.values
//        hashParkingFree = parkingFree.groupBy { it.split("@")[0] }.values
//        hashNote = note.groupBy { it.split("@")[0] }.values
//        hashNote = note.groupBy { it.split("@")[0] }.values


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

        // btnMyLocation : 내 위치로 이동
        binding.btnMyLocation.setOnClickListener {

//            if (isCheck){ // 기본값 : 맵 고정이 꺼져 있을 때
//                // 방향 추적기능 켜기
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
//                mapView.setZoomLevel(1, true) // 줌레벨 변경 이벤트
//                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_clicked) // **버튼 이미지 변경 이벤트, 멘토링 필요
//
//            }else{
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
//                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
//                mapView.setZoomLevel(1, true)
//                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_normal)
//
//            }
//
//            // 토글 전환
//            isCheck = !isCheck

            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(curlat,curlng),1,true)

        }

        // 슬라이드 버튼 이벤트
        binding.btnReport.setOnClickListener {
            Toast.makeText(context, "고장신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        var check = false

        binding.btnBookMark.setOnClickListener {
            if (check == false){
                binding.btnBookMark.setImageResource(R.drawable.slideicon_bookmark_o)
                check = !check
                Toast.makeText(context, "즐겨찾기 등록!", Toast.LENGTH_SHORT).show()
            } else {
                binding.btnBookMark.setImageResource(R.drawable.slideicon_bookmark_x)
                check = !check
                Toast.makeText(context, "즐겨찾기 해제", Toast.LENGTH_SHORT).show()
            }
        }

        // 임시버튼할당!!!!
        binding.imgSlow.setOnClickListener {
            // 카카오 네비 URL scheme
//            val url = "kakaomap://route?sp="+conlat+","+"conlng+&ep="+mLat+","+mLng+"&by=CAR" // 왜 현재위치까지 찍히는지 몰?루
            val url = "kakaomap://route?sp="+curlat+","+curlng+"&ep="+mLat+","+mLng+"&by=CAR"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

//            // 인텐트 카테고리 브라우저를 사용해 설치된 앱 검색
//            intent.addCategory(Intent.CATEGORY_BROWSABLE)
//            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//
//            // 설치되어 있지 않다면
//            if (list.isEmpty()) {
//                // 마켓으로 이동
//                this.startActivity(
//                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"))
//                )
//            }
            // 카카오맵이 없을 때 동작용
            try { // 카카오맵 실행
                startActivity(intent)
            } catch (e: Exception) {  // 만약 실행이 안된다면 (앱이 없다면)
                // 카카오맵 설치 페이지로 이동
                this.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"))
                )
            }


        }


        /* ----------- 리사이클러뷰, 어댑터 영역 ----------- */

        evstations = tempEv()
        setAdapter()


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

    fun getDistance(lat: Double, lon: Double): String? {
        val locationA = Location("current")
        locationA.latitude = curlat
        locationA.longitude = curlng
        val locationB = Location("target")
        locationB.latitude = lat
        locationB.longitude = lon
        val dis = locationA.distanceTo(locationB).toDouble()
        if (dis < 1000) {
            distance = "" + dis.toInt() + " m"
        } else {
            distance = "" + dis.toInt() / 10 * 10 / 1000.0 + " km"
        }
        Log.d("거리", distance)
        return distance
    }


    /* ----------- Rest API ----------- */

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


    /* ----------- 슬라이딩 패널 ----------- */

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


    /* ----------- 마커 클릭 ----------- */

    // 마커 클릭 이벤트 리스너
    class MarkerEventListener(val context: MapFragment): MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            val slidePanel = context.binding.mainFrameLayout
            val state = slidePanel.panelState

            // 마커 내부에서만 돌아가는 데이터 변수 선언
            val mChgerType = chgerType.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 충전기 타입, 갯수
            val mStat = stat.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 충전기 상태
            val mUseTime = useTime.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 이용 가능 시간
            val mBusiCall = busiCall.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 관리업체 전화번호
            val mParkingFree = parkingFree.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 무료주차 가능 여부
            val mNote = note.groupBy { it.split("@")[0] }.values.toTypedArray()[poiItem?.tag!!.toInt()].toMutableList() // 특이사항

            // 지도 중앙 이동용 위경도
            mLat = context.hashLat[poiItem.tag].toDouble()
            mLng = context.hashLng[poiItem.tag].toDouble()
            mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mLat, mLng),true)

            // 충전기 정보
            var dcCombo = false
            var dcDemo = false
            var ac3 = false
            var slow = false

            // 충전기 사용 가능 여부
            var chargeable = false
            var useableChgr = 0

            // 그룹화를 위해 묶었던 'statNm@' 제거
            for (i in 0..mChgerType.size-1){
                mChgerType[i] = mChgerType[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")
                Log.d("tst", mChgerType[i])
                mStat[i] = mStat[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")
                mUseTime[i] = mUseTime[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")
                mBusiCall[i] = mBusiCall[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")
                mParkingFree[i] = mParkingFree[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")
                mNote[i] = mNote[i].replace(context.hashStatNm.toTypedArray()[poiItem?.tag!!.toInt()]+"@","")

                // 충전기 정보에 따라 이미지뷰 변경
                when(mChgerType[i]){
                    "01" -> dcDemo = true
                    "02" -> slow = true
                    "03" -> {
                        dcDemo = true
                        slow = true}
                    "04" -> dcCombo = true
                    "05" -> {
                        dcDemo = true
                        dcCombo = true
                    }
                    "06" -> {
                        dcDemo = true
                        ac3 = true
                        dcCombo = true
                    }
                    "07" -> ac3 = true
                }


                // 충전기 사용가능 여부 확인
                if (mStat[i] == "2"){
                    chargeable = true
                    useableChgr++
                }
            }
            Log.d("tst2", dcDemo.toString())
            Log.d("tst2", dcCombo.toString())
            Log.d("tst2", ac3.toString())

            // 마커 클릭시
//            text = "" // 테스트용 임시 text
//            text += "${poiItem?.tag.toString()}번 충전소 \n"
//            text += "1. 충전소명 : ${poiItem?.itemName.toString()} \n"
//            text += "2. 충전소 주소 : ${context.hashAddr.toTypedArray()[poiItem?.tag!!.toInt()]} \n"
//            text += "3. 충전기 갯수 : ${mChgerType.size} \n"
//            text += "4. 충전기 타입 : ${mChgerType} \n"
//            text += "5. 충전기 상태 : ${mStat} \n"
//            text += "6. 이용가능 시간 : ${mUseTime[0]} \n"
//            text += "7. 관리업체 전화번호 : ${mBusiCall[0]} \n"
//            text += "8. 무료주차 여부 : ${mParkingFree[0]} \n"
//            text += "9. 특이사항 : ${mNote[0]} \n"



            // 닫힌 상태일 경우 열기
            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED

                // context.binding.tvAPI.text = text

                context.binding.tvStatNm.text = poiItem?.itemName.toString() // 충전소 이름
                context.binding.tvAddr.text = context.hashAddr.toTypedArray()[poiItem?.tag!!.toInt()] // 충전소 주소

                if (mUseTime[0] == "24시간 이용가능"){ // 이용가능 시간
                    context.binding.img24h.setImageResource(R.drawable.slideicon_24_o)
                } else {
                    context.binding.img24h.setImageResource(R.drawable.slideicon_24_x)
                }

                if (mParkingFree[0] == "Y"){ // 무료주차 여부
                    context.binding.imgParkingFree.setImageResource(R.drawable.slideicon_parkingfree_o)
                } else {
                    context.binding.imgParkingFree.setImageResource(R.drawable.slideicon_parkingfree_x)
                }

                // 사용가능 충전기 타입
                if (dcCombo){
                    context.binding.imgDcCombo.setImageResource(R.drawable.slideicon_dccombo_o)
                } else {
                    context.binding.imgDcCombo.setImageResource(R.drawable.slideicon_dccombo_x)
                }
                if (dcDemo){
                    context.binding.imgDcDemo.setImageResource(R.drawable.slideicon_dcdemo_o)
                } else {
                    context.binding.imgDcDemo.setImageResource(R.drawable.slideicon_dcdemo_x)
                }
                if (ac3){
                    context.binding.imgAc3.setImageResource(R.drawable.slideicon_ac3_o)
                } else {
                    context.binding.imgAc3.setImageResource(R.drawable.slideicon_ac3_x)
                }
                if (slow){
                    context.binding.imgSlow.setImageResource(R.drawable.slideicon_slow_o)
                } else {
                    context.binding.imgSlow.setImageResource(R.drawable.slideicon_slow_x)
                }

                // 충전가능 여부 및 사용가능 충전기 댓수
                if (chargeable){
                    context.binding.imgChargeable.setImageResource(R.drawable.slideicon_charge_o)
                } else {
                    context.binding.imgChargeable.setImageResource(R.drawable.slideicon_charge_x)
                }
                context.binding.tvUseableChgr.text = "${useableChgr} 대 사용가능"


            }
            // 슬라이드 패널 열린 상태일 경우 닫기
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

//            context.binding.tvAPI.text = text
//            Log.d("APItest", statNm.toString())
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }

    //



    // 실시간 위치 갱신 함수
    private fun startLocationUpdates() {
        // 권한 체크
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mFusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { mLastLocation->
                if(mLastLocation == null) {
//                    Log.e("TAG", "location get fail")
                } else {
//                    Log.d("TAG", "${mLastLocation.latitude} , ${mLastLocation.longitude}")
                }
            }
            .addOnFailureListener {
                Log.e("TAG", "location error is ${it.message}")
                it.printStackTrace()
            }

        mLocationRequest = LocationRequest.create()
        mLocationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 // 갱신주기
        }

        mLocationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let {
                    for((i, location) in it.locations.withIndex()) {
                        curlat = location.latitude
                        curlng = location.longitude
                        Log.d("현재좌표", "#$i ${location.latitude} , ${location.longitude}")
                    }
                }
            }
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }


    /* ----------- 서치뷰 ----------- */

    // 서치뷰 텍스트 리스너
    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                binding.svView.isInvisible = false
                searchViewAdapter.filter.filter(s)
                Log.d("서치뷰", "SearchVies Text is changed : $s")
                return false
            }
        }

    // 리사이클러뷰에 리사이클러뷰 어댑터 부착
    fun setAdapter(){
        binding.svView.layoutManager = LinearLayoutManager(context)
        searchViewAdapter = SearchViewAdapter(evstations, this)
        binding.svView.adapter = searchViewAdapter
    }

    // tempEV에 데이터 가져오기
    fun tempEv(): ArrayList<EvStation> {
        var tempEv = ArrayList<EvStation>()

        for (i in 0..hashStatNm.size-1){
            tempEv.add(EvStation(i, hashStatNm[i], hashAddr[i], hashLat[i], hashLng[i], hashDistance[i]))
        }


        return tempEv
    }

    /* ----------- 기타 기능 ----------- */

    // 뒤로가기 버튼 - 추후 개선?
    override fun onBackPressed() {
        val slidePanel = binding.mainFrameLayout
        val state = slidePanel.panelState
        Log.d("백버튼", "뒤로가기")

        // 서치뷰 텍스트 초기화, 리사이클러뷰 가리기
        binding.svSearch.setQuery("", false)
        binding.svView.isInvisible = true
        // 슬라이드 패널이 열려 있을 경우 닫기
        if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

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


   // onPause : 다른 Activity 활성화시 호출

    override fun onPause() {
        super.onPause()
//        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
//        binding.mapViewContainer.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.prefs.setString("curlat", curlat.toString())
        MyApplication.prefs.setString("curlng", curlng.toString())
    }



}
