package com.example.ev_check.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.ev_check.BuildConfig
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/* ----------- 현재 IntroActivity는 사용되지 않음, 추후 회원가입 및 로그인용으로 전환 ----------- */

class IntroActivity : AppCompatActivity() {

    /* ----------- 전역변수 선언 영역 ----------- */

    private val RC_SIGN_IN = 9001
    private var googleSignInClient : GoogleSignInClient?=null
    lateinit var auth : FirebaseAuth // 객체 공유 인스턴스

    // private val REQUEST_PERMISSION_LOCATION = 10 // 권한 체크용 변수

    // 스낵바 사용을 위한 레이아웃 뷰 객체 선언
    lateinit var introPageLayout: View

    // 필요 권한 모음 배열
    var permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val PERMISSIONS_REQUEST = 0x0000001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        /* ----------- 테스트 영역 (사용 후 삭제) ----------- */

        OnCheckPermission()


        /* ----------- 변수 선언 및 초기화 영역 ----------- */
        introPageLayout = findViewById<ConstraintLayout>(R.id.introPageLayout)

        // 파이어베이스
        auth = Firebase.auth

        // 구글연동로그인옵션
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("209508508120-eu6to9irjj3trfubotcjbc393cc5p0cs.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // 구글 signIn 버튼 객체
        val signInGoogleBtn: SignInButton = findViewById(R.id.signInButton)


        /* ----------- 이벤트 영역 ----------- */

        // 구글 로그인 버튼 온클릭이벤트
        signInGoogleBtn.setOnClickListener {
            // 로그인 처리
            try{
                googleSignInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent = googleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }catch (e: ApiException){

            }

        }

    } // create

    /* ----------- 기타 영역 ----------- */

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuthSignOut()
    }

    /* ----------- 함수, 메서드, 기타기능 영역 ----------- */

    // 구글로그인
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{ // 로그인 성공
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) // MainActivity로 이동

            } catch (e: ApiException){
                // 로그인 실패
            }
        }
    }

    // 구글로그인시 받아오는 정보
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){ // 성공
                    val user = auth.currentUser
                    user?.let {
                        val name = user.displayName
                        val email = user.email
                        val displayName = user.displayName
                        val photoUrl = user.photoUrl
                        val emailVerified = user.isEmailVerified
                        val uid = user.uid
                        Log.d("xxxx name", name.toString())
                        Log.d("xxxx email", email.toString())
                        Log.d("xxxx displayName", displayName.toString())

                        Toast.makeText(this, name + "님 접속을 환영합니다!", Toast.LENGTH_SHORT).show()

                    }
                } else { // 실패
                    Log.d("xxxx ", "signInWithCredential:failure", task.exception)
                }
            }

    }

    // 파이어베이스 로그아웃
    private fun firebaseAuthSignOut() {
        Firebase.auth.signOut() // 로그아웃
        googleSignInClient!!.signOut() // 로그인할때마다 계정선택
    }

    // 권한 설정 체크
    private fun OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
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
                Snackbar.make(introPageLayout, "권한이 설정되었습니다", Snackbar.LENGTH_LONG).show()
            } else {

                Snackbar.make(
                    introPageLayout,
                    "앱 사용을 위해서는 권한 설정이 필요합니다.\n설정으로 이동하시겠습니까?",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    "OK"
                ) { // 스낵바 OK 클릭시 실행작업
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                }.show()
            }
        }
    }


    /* ----------- 테스트 영역 (완성 후 삭제) ----------- */

//    // 위치 권한이 있는지 확인하는 메서드
//    private fun checkPermissionForLocation(context: Context): Boolean {
//        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//                true
//            } else {
//                // 권한이 없으므로 권한 요청 알림 보내기
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
//                false
//            }
//        } else {
//            true
//        }
//    }

//    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 권한 승인시, 필요하다면 Toast메세지 추가할것
//
//            } else {
//                // 권한 거절시, 설정페이지로 이동하는 창 생성
//                // #1 스낵바
//                Snackbar.make(
//                    introPageLayout,
//                    "앱 사용을 위해서는 권한 설정이 필요합니다.\n설정으로 이동하시겠습니까?",
//                    Snackbar.LENGTH_INDEFINITE
//                ).setAction(
//                    "OK"
//                ) { // 스낵바 OK 클릭시 실행작업
//                    startActivity(
//                        Intent(
//                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
//                        )
//                    )
//                }.show()
//
////                // #2 alert dialog
////                val builder = AlertDialog.Builder(this)
////                builder.setMessage("앱 사용을 위해서는 권한 설정이 필요합니다.\\n설정으로 이동하시겠습니까?")
////                builder.setPositiveButton("확인") { dialog, which ->
////                    startActivity(
////                        Intent(
////                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
////                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
////                        )
////                    )
////                }
////                builder.setNegativeButton("취소") { dialog, which ->
////
////                }
////                builder.show()
//            }
//        }
//    }

}