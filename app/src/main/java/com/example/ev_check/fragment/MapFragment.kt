package com.example.ev_check.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ev_check.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {

    /* ----------- 전역변수 선언 영역 ----------- */

    lateinit var binding: FragmentMapBinding // 뷰 바인딩
    lateinit var mapView: MapView // 카카오 지도 뷰
    val eventListener = MarkerEventListener(this)

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback


    var lat: Double = 35.1467267 // 인공사 위치(임시)
    var lon: Double = 126.922157


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
        
        // 앱 시작과 동시에 초기위치로 카메라 이동 + 줌인
         mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat,lon),1,false)

        // 방향추적 기능 없이 마커만 표시
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving

        /* ----------- 이벤트 영역 ----------- */

        // btnDirectionTracking : 방향추적 버튼
        binding.btnDirectionTracking.setOnClickListener {

            if (isCheck){ // 방향 추적 기능이 꺼져 있을 때 (기본값)
                // 방향 추적기능 켜기
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
                mapView.setZoomLevel(1, true) // 줌레벨 변경 이벤트
                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_clicked) // **버튼 이미지 변경 이벤트, 멘토링 필요

            }else{
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving
                mapView.setZoomLevel(1, true)
                // binding.btnDirectionTracking.setBackgroundResource(R.drawable.ic_gps_normal)

            }

            // 토글 전환
            isCheck = !isCheck

        }

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
        val marker = MapPOIItem()
        marker.apply {
            itemName = "Default Marker"
            marker.tag = 0
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(35.1538797, 126.9047647)
            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
            marker.selectedMarkerType =
                MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        }
        mapView.addPOIItem(marker)


        /* ----------- View 리턴 ----------- */

        return view
    }


    /* ----------- 함수, 메서드, 리스너, 기타기능 영역 ----------- */


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

            // 마커 클릭시
            // 닫힌 상태일 경우 열기
            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }
            // 열린 상태일 경우 닫기
            else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭 시 (Deprecated)
            // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            // 말풍선 클릭 시
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
