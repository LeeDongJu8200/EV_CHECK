package com.example.ev_check.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ev_check.R
import com.google.android.gms.location.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {

    /* ----------- 전역변수 선언 영역 ----------- */

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback

    lateinit var btnDirectionTracking: View
    lateinit var mapView:MapView
    lateinit var mapViewContainer:RelativeLayout
    
    var lat: Double = 35.1467267 // 인공사 위치
    var lon: Double = 126.922157


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* ----------- 테스트 영역 (사용 후 삭제) ----------- */

        

        /* ----------- 변수 선언 및 초기화 영역 ----------- */
        
        // 프래그먼트의 View 객체 inflate
        val view =  inflater.inflate(R.layout.fragment_map, container, false) 

        // 각 객체 초기화
        btnDirectionTracking = view.findViewById(R.id.btnDirectionTracking)
        mapView = MapView(requireContext())
        mapViewContainer = view.findViewById(R.id.mapView)

        // btnDirectionTracking 토글용 boolean 변수
        var isCheck = true

        /* ----------- 지도 영역 ----------- */
        
        // 카카오지도 표시
        mapViewContainer.addView(mapView)
        
        // 실시간 위치 업데이트
        startLocationUpdates()
        
        // 앱 시작과 동시에 초기위치로 카메라 이동 + 줌인
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat,lon),1,false)

        // 방향추적 기능 없이 마커만 표시
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving


        /* ----------- 이벤트 영역 ----------- */
        
        // btnDirectionTracking 버튼 - 방향 추적기능 On/Off
        btnDirectionTracking.setOnClickListener {

            if (isCheck){ // 방향 추적 기능이 꺼져 있을 때 (기본값)
                // 방향 추적기능 켜기
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
                // *** 오류
//                mapView.setZoomLevel(1, true); // 줌레벨 변경 이벤트
//                btnDirectionTracking.setBackgroundColor(Color.parseColor("blue")) // 버튼 이미지 변경 파트

            }else{
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving
//                mapView.setZoomLevel(1, true);
//                btnDirectionTracking.setBackgroundColor(Color.parseColor("gray"))
            }

            // 토글 전환
            isCheck = !isCheck



            // 기능 테스트용 임시 코드 - 마커 세팅
            val marker = MapPOIItem()
            marker.itemName = "Default Marker"
            marker.tag = 0
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(35.1538797, 126.9047647)
            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.

            marker.selectedMarkerType =
                MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.


            mapView.addPOIItem(marker)


        }

        /* ----------- 기타 영역 ----------- */

        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        
        return view
    }

    /* ----------- 함수, 메서드, 기타기능 영역 ----------- */

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
                    Log.e("TAG", "location get fail")
                } else {
                    Log.d("TAG", "${mLastLocation.latitude} , ${mLastLocation.longitude}")
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
                        lat = location.latitude
                        lon = location.longitude
                        Log.d("현재좌표", "#$i ${location.latitude} , ${location.longitude}")
                    }
                }
            }
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    override fun onPause() {
        super.onPause()
        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }


}