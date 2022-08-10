package com.example.withub.feature.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.withub.*
import com.example.withub.data.network.*
import com.example.withub.feature.base.MyApp
import com.example.withub.feature.login.LoginActivity
import retrofit2.Call
import retrofit2.Response

class ResignAccount : AppCompatActivity() {
    var pwConfirm = false
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.resign_activity)

        val pwText = findViewById<EditText>(R.id.pw_edittext_resign)

        //뒤로가기
        findViewById<Button>(R.id.back_btn_resign).setOnClickListener{
            finish()
        }

        //비밀번호 확인
        findViewById<Button>(R.id.confirm_btn_resign).setOnClickListener{
            if (pwText.text.isEmpty()) {
                dialogMessage("현재 비밀번호를 입력해주세요.")
            } else {
                pwConfirmApi(pwText)
            }        }


    }

    fun pwConfirmApi(pwText: EditText) {
        val requestCurrentPwConfirmApi = retrofit.create(CurrentPwConfirmApi::class.java)
        requestCurrentPwConfirmApi.tokenPwCheck(MyApp.prefs.accountToken!!,pwText.text.toString()).enqueue(object : retrofit2.Callback<PwCheckData> {
            override fun onFailure(
                call: Call<PwCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<PwCheckData>, response: Response<PwCheckData>) {
                if (response.body()!!.success) {
                    pwConfirm = true
                    pwText.isEnabled = false
                    resignBtnActivate()
                    dialogMessage("비밀번호가 확인되었습니다.")
                } else {
                    resignBtnActivate()
                    dialogMessage("비밀번호가 틀렸습니다.")
                }
            }
        })
    }

    fun resignBtnActivate(){
        val resignBtn = findViewById<Button>(R.id.resign_btn_resign)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        if (pwConfirm) {
            resignBtn.setBackgroundResource(R.drawable.login_btn)
            resignBtn.isEnabled = true
            resignBtn.setOnClickListener{
                builder.setTitle("정말로 탈퇴하시겠습니까?")
                builder.setMessage("탈퇴하시면 계정을 복구할 수 없습니다.")
                builder.setPositiveButton("확인") { _, _ ->
                    resignApi()
                }
                builder.setNegativeButton("취소", null)
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
        }
    }

    fun resignApi() {
        val inform = TokenValue(MyApp.prefs.accountToken!!)
        val requestResignApi = retrofit.create(ResignApi::class.java)
        requestResignApi.tokenCheck(inform).enqueue(object : retrofit2.Callback<ResignTokenCheckData> {
            override fun onFailure(
                call: Call<ResignTokenCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<ResignTokenCheckData>, response: Response<ResignTokenCheckData>) {
                if (response.body()!!.success) {
                    Toast.makeText(this@ResignAccount, "WITHUB 계정이 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    resignBtnActivate()
                    dialogMessage("재시도 해주시기 바랍니다.")
                }
            }
        })
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


}