package com.example.ev_check

/* ----------- 리스트뷰 전용 클래스 ----------- */
class EvStation(
    var no: Int, // 번호
    var statNm: String, // 충전소명
    var addr: String, // 충전소 주소
    var lat: String, // 충전소 위도
    var lng: String // 충전소 경도
) {}