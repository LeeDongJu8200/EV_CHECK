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
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ev_check.R
import com.google.android.gms.location.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장
    internal lateinit var mLocationCallback: LocationCallback

    // ----------------------------------------------------------
    
    lateinit var btnCurrentLocation: View
    var lat: Double = 35.1467267 // 인공사 위치
    var lon: Double = 126.922157


    // ----------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_map, container, false)

        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)


        val mapView = MapView(requireContext())

        val mapViewContainer = view.findViewById<RelativeLayout>(R.id.mapView)
        mapViewContainer.addView(mapView)



        startLocationUpdates()



        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        // ----------------------------------------------------------

        // 버튼 이벤트를 통해 현재 위치 찾기 + 마커찍기
        btnCurrentLocation.setOnClickListener {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat,lon), 1, false);

            val marker = MapPOIItem()

            marker.itemName = "Default Marker"
            marker.tag = 0

            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.

            marker.selectedMarkerType =
                MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.


            mapView.addPOIItem(marker)

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