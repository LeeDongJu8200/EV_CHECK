package com.example.ev_check

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.ev_check.auth.IntroActivity
import com.google.android.material.snackbar.Snackbar

class SplashActivity : AppCompatActivity() {

    /* ----------- 전역변수 선언 영역 ----------- */

    // 스낵바를 위한 View 객체 생성
    lateinit var splashPageLayout: View

    // 필요 권한 모음 배열
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_BACKGROUND_LOCATION // 백그라운드
    )

    val PERMISSIONS_REQUEST = 0x0000001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashPageLayout = findViewById<ConstraintLayout>(R.id.splashPageLayout)

        /* ----------- 이벤트 영역 ----------- */

        Log.d("권한체크", ""+checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))

        // 권한체크용 임시변수
        var check = ""+checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (check == "0"){
            // 권한 있을 때 동작
            // SplashActivity에서 MainActivity로 3초 후 이동
            // 3초지연 --> SubThread --> **Handler --> MainThread
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        } else {
            // 권한 없을 때 동작
            OnCheckPermission()
        }


        

    }

    /* ----------- 함수, 스레드 영역 ----------- */
    
    // 권한 설정 체크
    private fun OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
//            || ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permission_list,
                    PERMISSIONS_REQUEST
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permission_list,
                    PERMISSIONS_REQUEST
                )
            }
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한이 허용되었을 경우
                Snackbar.make(splashPageLayout, "권한이 설정되었습니다", Snackbar.LENGTH_LONG).show()

                // SplashActivity에서 MainActivity로 3초 후 이동
                // 3초지연 --> SubThread --> **Handler --> MainThread
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)

            } else {

                // 앱 정보 화면 이동용 스낵바
                Snackbar.make(
                    splashPageLayout,
                    "앱 사용을 위해서는 권한 설정이 필요합니다.\n설정으로 이동하시겠습니까?",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    "OK"
                ) { // 스낵바 OK 클릭시 실행작업
                    startActivity(
                        // 앱 정보 화면으로 이동
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                    // 어플리케이션 종료
                    finish()

                }.show()
            }
        }
    }
}