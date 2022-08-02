package com.example.withub

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import retrofit2.Call
import retrofit2.Response


class LoginActivity: AppCompatActivity() {
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WITHUB)
        window.statusBarColor = getColor(R.color.background_color)
        if(MyApp.prefs.accountToken!=null){
            tokenApi()
        }
//        if (Build.VERSION.SDK_INT >= 23) {
////            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.addFlags(WindowManager.LayoutParams.Fl)
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES

        val signupBtn = findViewById<TextView>(R.id.signup_btn)
        val idFindingBtn = findViewById<TextView>(R.id.id_finding_btn)
        val pwFindingBtn = findViewById<TextView>(R.id.pw_finding_btn)
        val idText = findViewById<EditText>(R.id.id_edittext_login_activity)
        val pwText = findViewById<EditText>(R.id.pw_edittext_login_activity)
        val loginBtn = findViewById<Button>(R.id.login_btn)

        signupBtn.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        idFindingBtn.setOnClickListener{
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }

        pwFindingBtn.setOnClickListener{
            val intent = Intent(this, FindPwActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener{
            loginApi(idText,pwText)
        }

        loginBtnRegEx(idText,pwText,loginBtn)
    }

    fun loginBtnRegEx(idText:EditText,pwText: EditText,loginBtn:Button){
        idText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                loginBtnActivate(idText,pwText,loginBtn)
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        pwText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                loginBtnActivate(idText,pwText,loginBtn)
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun loginBtnActivate(idText:EditText,pwText: EditText,loginBtn:Button) {
        if (idText.text.length >= 5 && pwText.text.length >= 8){
            loginBtn.setBackgroundResource(R.drawable.login_btn)
            loginBtn.isEnabled = true
        } else {
            loginBtn.setBackgroundResource(R.drawable.disabled_button)
            loginBtn.isEnabled = false
        }
    }

    fun loginApi(idText: EditText,pwText: EditText) {
        val inform = LoginIdPwValue(idText.text.toString(),pwText.text.toString())
        val requestLoginApi = retrofit.create(LoginApi::class.java)
        requestLoginApi.loginCheck(inform).enqueue(object : retrofit2.Callback<LoginCheckData> {
            override fun onFailure(
                call: Call<LoginCheckData>,
                t: Throwable
            ) {
                Log.d("D",t.toString())
            }
            override fun onResponse(call: Call<LoginCheckData>, response: Response<LoginCheckData>) {
                if (response.body()!!.success) {
                    MyApp.prefs.accountToken = response.body()!!.token
                    if (MyApp.prefs.githubToken == null) {
                        val intent = Intent(applicationContext, GetTokenActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    var builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                    builder.setMessage("아이디 혹은 비밀번호를 확인해주세요.")
                    builder.setPositiveButton("확인", null)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }
            }
        })
    }

    fun tokenApi(){
        val requestTokenApi = retrofit.create(TokenApi::class.java)
        requestTokenApi.loginCheck(MyApp.prefs.accountToken!!).enqueue(object : retrofit2.Callback<TokenCheckData> {
            override fun onFailure(
                call: Call<TokenCheckData>,
                t: Throwable
            ) {

            }
            override fun onResponse(call: Call<TokenCheckData>, response: Response<TokenCheckData>) {
                Log.d("message","${response.body()!!.success}")
                if (response.body()!!.success) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("message","darkmode")
    }


}