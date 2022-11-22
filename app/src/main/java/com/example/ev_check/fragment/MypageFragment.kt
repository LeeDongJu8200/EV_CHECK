package com.example.ev_check.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.ev_check.R
import org.w3c.dom.Document

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory


var text2 = ""

class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_mypage, container, false)

        var tv = view.findViewById<TextView>(R.id.tvAPI)
        val button = view.findViewById<Button>(R.id.btnAPI)

        // key값
        val key = "2680%2Bqf3cT4Io2pS%2BAP7WbASYKfnMEzIWIqJZpszpKjbA%2FSNZ9HbeR8Z40kl24TH4qAGbl4XnWt1xPqeM3Qylw%3D%3D"

        // 현재 페이지번호
        val pageNo = "&pageNo=1"

        // 한 페이지 결과 수
        val numOfRows ="&numOfRows=10"

        // 생태기간 갱신
        val period = "&period=5"

        // 지역구분코드
        val zcode = "&zcode=29"

        val url = "http://apis.data.go.kr/B552584/EvCharger/getChargerInfo?serviceKey="+key+pageNo+numOfRows+period+zcode
        Log.d("TTT", url)

        // 버튼을 누르면 쓰레드 동작
        button.setOnClickListener {

            // 쓰레드 생성
            val thread = Thread(NetworkThread(url))
            thread.start() // 쓰레드 시작
            thread.join() // 멀티 작업 안되게 하려면 start 후 join 입력

            // 쓰레드에서 가져온 api 정보 텍스트에 뿌려주기
            tv.text = text2

        }

        return view
    }

    class NetworkThread(var url: String): Runnable {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun run() {
            try {
                val xml : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url)
                Log.d("TTT2", url)

                xml.documentElement.normalize()
                Log.d("ttt", xml.toString())

                //찾고자 하는 데이터가 어느 노드 아래에 있는지 확인
                val list: NodeList = xml.getElementsByTagName("item")

                //list.length-1 만큼 얻고자 하는 태그의 정보를 가져온다
                for(i in 0..list.length-1){

                    val n: Node = list.item(i)

                    if(n.getNodeType() == Node.ELEMENT_NODE){

                        val elem = n as Element

                        val map = mutableMapOf<String,String>()


                        // 이부분은 어디에 쓰이는지 잘 모르겠다.
                        for(j in 0..elem.attributes.length - 1) {

                            map.putIfAbsent(elem.attributes.item(j).nodeName, elem.attributes.item(j).nodeValue)

                        }


                        println("=========${i+1}=========")
                        text2 += "${i + 1}번 충전소 \n"

                        println("1. 주소 : ${elem.getElementsByTagName("addr").item(0).textContent}")
                        text2 += "1. 주소 : ${elem.getElementsByTagName("addr").item(0).textContent} \n"

                        println("2. 이름 : ${elem.getElementsByTagName("statNm").item(0).textContent}")
                        text2 += "2. 이름 : ${elem.getElementsByTagName("statNm").item(0).textContent} \n"

                        println("3. 위도 : ${elem.getElementsByTagName("lat").item(0).textContent}")
                        text2 += "3. 위도 : ${elem.getElementsByTagName("lat").item(0).textContent} \n"

                        println("4. 경도 : ${elem.getElementsByTagName("lng").item(0).textContent}")
                        text2 += "4. 경도 : ${elem.getElementsByTagName("lng").item(0).textContent} \n"


                    }
                }




            } catch (e: Exception) {
                Log.d("TTT3", "오픈API"+e.toString())
            }
        }

    }

}