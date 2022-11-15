package com.example.ev_check.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ev_check.R
import com.google.android.gms.location.*
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.delay
import org.w3c.dom.Text

class MapFragment : Fragment() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback

    // ----------------------------------------------------------
    
    lateinit var btnCurrentLocation: View
    var lat: Double = 0.0
    var lon: Double = 0.0

    // ----------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_map, container, false)

        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)

        val tmapContainer = view.findViewById<FrameLayout>(R.id.tmapViewContainer)
        val tmapview = TMapView(requireContext())
        tmapview.setSKTMapApiKey("l7xx5d7fe0cb13c64ba3847085ce8a7ffbf9")
        tmapContainer.addView(tmapview)

        startLocationUpdates()



        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        // ----------------------------------------------------------

        // 버튼 이벤트를 통해 현재 위치 찾기 + 마커찍기
        btnCurrentLocation.setOnClickListener {

            tmapview.setCenterPoint(lat,lon)
            tmapview.setZoomLevel(17)


            // ----------------------------------------------------------
            // 마커 테스트용 - 현재 위치까지 카메라 이동은 되나 마커가 찍히지 않음...
            // *** 에뮬레이터 GPS 동작 X - 케이블 이용해 폰에 연결해서 동작시키면 제대로 작동함
            // 레퍼런스 자료가 자바 뿐이라 그런것같긴한데...
            // 참고자료 - 크롬 북마크 - 최종프로젝트
            
            // 비트맵 이미지
            val bitmap =
                BitmapFactory.decodeResource(
                    requireActivity().resources,
                    R.drawable.markerblue
                )

            // 마커 선언, 표시
            val markerItem1 = TMapMarkerItem()
            markerItem1.id = "marker1"
            markerItem1.icon = bitmap
            markerItem1.setPosition(0.5f, 1.0f)
            markerItem1.setTMapPoint(lat,lon)
            markerItem1.name = "현재위치"
            tmapview.addTMapMarkerItem(markerItem1)
        }

        // ----------------------------------------------------------

        return view
    }

    private fun startLocationUpdates() {

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
            interval = 60 * 1000
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