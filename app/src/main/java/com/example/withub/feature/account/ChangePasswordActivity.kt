package com.example.withub.feature.account

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.withub.*
import com.example.withub.data.network.*
import com.example.withub.feature.base.MyApp
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var pwValue : String
    var pwMatch = false
    var pwSafety = false
    var pwConfirm = false
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.change_password_activity)

        val backBtn = findViewById<ImageButton>(R.id.back_btn_change_password)
        val confirmBtn = findViewById<Button>(R.id.confirm_btn_change_password)
        val currentPwText = findViewById<EditText>(R.id.current_pw_edittext_change_password)
        val pwText = findViewById<EditText>(R.id.pw_edittext_change_password)
        val pwConfirmText = findViewById<EditText>(R.id.pw_confirm_edittext_change_password)
        val safetyView = findViewById<TextView>(R.id.pw_safety_textview_change_password)
        val pwMismatchView = findViewById<TextView>(R.id.pw_confirm_mismatch_textview_change_password)
        val pwCheckView = findViewById<TextView>(R.id.pw_check_textview_change_password)

        backBtn.setOnClickListener{
            finish()
        }

        confirmBtn.setOnClickListener{
            if (currentPwText.text.isEmpty()) {
                dialogMessage("현재 비밀번호를 입력해주세요.")
            } else {
                currentPwConfirmApi(currentPwText)
            }
        }

        pwRegExp(pwText,pwCheckView,safetyView,pwConfirmText,pwMismatchView)
    }

    private fun pwRegExp( pwText:EditText, pwCheckView: TextView, safetyView: TextView, pwConfirmText:EditText, pwMismatchView: TextView) {
        pwText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var numSpecialBoolean: Boolean = Pattern.matches("^.*(?=^.{8,20}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$", pwText.getText().toString())
                if (!numSpecialBoolean) {
                    pwCheckView.text = "8~20자 영문, 숫자, 특수문자를 사용하세요."
                    safetyView.text = "사용 불가"
                    safetyView.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,
                        R.color.timer
                    ))
                    pwChangeBtnActivate()
                } else{
                    safetyView.setTextColor(ContextCompat.getColor(this@ChangePasswordActivity,
                        R.color.correct_color
                    ))
                    safetyView.text = "안전"
                    pwCheckView.text = ""
                    pwSafety = true
                    pwValue = pwText.text.toString()
                    pwChangeBtnActivate()
                }

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (pwConfirmText.length() != 0 && pwText.length() != 0) {
                    if (pwText.text.toString() != pwConfirmText.text.toString()){
                        pwMismatchDisplay(pwMismatchView)
                    } else {
                        pwMatchDisplay(pwMismatchView)
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        pwConfirmText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (pwConfirmText.length() != 0 && pwText.length() != 0) {
                    if (pwText.text.toString() != pwConfirmText.text.toString()) {
                        pwMismatchDisplay(pwMismatchView)
                    } else {
                        pwMatchDisplay(pwMismatchView)
                    }
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun pwMismatchDisplay(pwMismatchView: TextView) {
        pwMismatchView.setTextColor(ContextCompat.getColor(this, R.color.timer))
        pwMismatchView.text = "불일치"
        pwChangeBtnActivate()
    }

    fun pwMatchDisplay(pwMismatchView: TextView){
        pwMismatchView.setTextColor(ContextCompat.getColor(this, R.color.correct_color))
        pwMismatchView.text = "일치"
        pwMatch = true
        pwChangeBtnActivate()
    }


    fun currentPwConfirmApi(currentPwText:EditText){
        val requestCurrentPwConfirmApi = retrofit.create(CurrentPwConfirmApi::class.java)
        requestCurrentPwConfirmApi.tokenPwCheck(MyApp.prefs.accountToken!!,currentPwText.text.toString()).enqueue(object : retrofit2.Callback<PwCheckData> {
            override fun onFailure(
                call: Call<PwCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<PwCheckData>, response: Response<PwCheckData>) {
                if (response.body()!!.success) {
                    pwConfirm = true
                    currentPwText.isEnabled = false
                    dialogMessage("비밀번호가 확인되었습니다.")
                    pwChangeBtnActivate()
                } else {
                    dialogMessage("비밀번호가 틀렸습니다.")

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

    fun pwChangeBtnActivate() {
        var changePwBtn = findViewById<Button>(R.id.pw_change_btn_change_password)
        if (pwMatch && pwSafety && pwConfirm) {
            changePwBtn.setBackgroundResource(R.drawable.login_btn)
            changePwBtn.isEnabled = true
            changePwBtn.setOnClickListener{
                changePwApi()
            }
        }
    }

    fun changePwApi() {
        val inform = PwTokenPwValue(MyApp.prefs.accountToken.toString(),pwValue)
        val requestCurrentPwConfirmApi = retrofit.create(ChangePwApi::class.java)
        requestCurrentPwConfirmApi.changePw(inform).enqueue(object : retrofit2.Callback<ChangePwCheckData> {
            override fun onFailure(
                call: Call<ChangePwCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<ChangePwCheckData>, response: Response<ChangePwCheckData>) {
                if (response.body()!!.success) {
                    Toast.makeText(this@ChangePasswordActivity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(applicationContext, AccountActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}