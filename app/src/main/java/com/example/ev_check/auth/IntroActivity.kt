package com.example.ev_check.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    // ------------ 구글 로그인에 필요한 변수 설정 ------------

    private val RC_SIGN_IN = 9001
    private var googleSignInClient : GoogleSignInClient?=null
    lateinit var auth : FirebaseAuth // 객체 공유 인스턴스

    // ----------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

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

                        // 액티비티 기능 구현 자리
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
}