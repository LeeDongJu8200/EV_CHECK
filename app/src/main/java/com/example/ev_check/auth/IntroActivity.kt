package com.example.ev_check.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.ev_check.MainActivity
import com.example.ev_check.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IntroActivity : AppCompatActivity() {

    // ------------ 구글 로그인, 권한 설정에 필요한 변수 설정 ------------

    private val RC_SIGN_IN = 9001
    private var googleSignInClient : GoogleSignInClient?=null
    lateinit var auth : FirebaseAuth // 객체 공유 인스턴스

    private val REQUEST_PERMISSION_LOCATION = 10 // 권한 체크용 변수
    
    // *** 스낵바를 위한 View 객체 firstLayout 생성

    // ----------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        checkPermissionForLocation(this)

        // ------------------------------------------------

        auth = Firebase.auth

        // ------------------------------------------------

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("209508508120-eu6to9irjj3trfubotcjbc393cc5p0cs.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // ------------------------------------------------

        val signInGoogleBtn: SignInButton = findViewById(R.id.signInButton) // 구글 signIn 버튼 객체

        signInGoogleBtn.setOnClickListener { // 구글로그인버튼 온클릭이벤트
            //로그인 처리
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    } // create

    // ----------------------------------------------------

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuthSignOut()
    }

    // ----------------------------------------------------

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

    // ----------------------------------------------------

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

    // ----------------------------------------------------

    private fun firebaseAuthSignOut() {
        Firebase.auth.signOut() // 로그아웃
        googleSignInClient!!.signOut() // 로그인할때마다 계정선택
    }

    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}