package com.example.withub

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.withub.loginFragments.IdPwInputFragment
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class AccountActivity : AppCompatActivity() {
    lateinit var areaSelect : String
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.account_activity)
        val areaSelectSpinner = findViewById<Spinner>(R.id.area_select_spinner_account)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        areaSpinnerSelect(areaSelectSpinner)

        //뒤로가기
        findViewById<ImageButton>(R.id.account_activity_back_btn).setOnClickListener{
            finish()
        }

        //닉네임 변경
        findViewById<LinearLayout>(R.id.account_activity_change_nickname_view).setOnClickListener{
            nicknameChangeDialog()
        }

        //비밀번호 변경
        findViewById<LinearLayout>(R.id.account_activity_change_password_view).setOnClickListener {
            val intent = Intent(applicationContext, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        //회원탈퇴
        findViewById<LinearLayout>(R.id.account_activity_delete_view).setOnClickListener {
            val intent = Intent(applicationContext, ResignAccount::class.java)
            startActivity(intent)
        }

        //로그아웃
        findViewById<LinearLayout>(R.id.account_activity_logout_view).setOnClickListener {
            builder.setTitle("로그아웃 하시겠습니까?")
            builder.setMessage("다시 로그인 할 시 GitHub 토큰을 재발급 받으셔야합니다.")
            builder.setPositiveButton("확인") { _, _ ->
                deleteToken()
            }
            builder.setNegativeButton("취소", null)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    fun nicknameChangeDialog() {
        val input = EditText(this)
        input.hint = "닉네임"
        input.setSingleLine()
        val inputContainer = LinearLayout(this)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        input.layoutParams = params
        inputContainer.addView(input)
        val dialog : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(this)
        dialog.setTitle("닉네임 변경")
            .setMessage("변경할 닉네임을 입력해 주세요.")
            .setView(inputContainer)
            .setPositiveButton("변경"){ _, _ -> nicknameCheck(input.text.toString()) }
            .setNegativeButton("취소"){ _, _ ->  }
            .show()
    }

    fun nicknameCheck(nickname : String) {
        val boolean = Pattern.matches("^[a-z0-9가-힣]*$", nickname)
        if (!boolean || nickname.length < 2 || nickname.length>10) {
            dialogMessage("닉네임은 2~10자 한글, 영어, 숫자만 사용하세요.")
        } else {
            nicknameChangeApi(nickname)
        }
    }

    fun nicknameChangeApi(nickname : String) {
        val inform = TokenNickNameValue(MyApp.prefs.accountToken.toString(),nickname)
        val requestChangeNicknameApi = retrofit.create(ChangeNicknameApi::class.java)
        requestChangeNicknameApi.tokenCheck(inform).enqueue(object : retrofit2.Callback<TokenNickNameCheckData> {
            override fun onFailure(
                call: Call<TokenNickNameCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<TokenNickNameCheckData>, response: Response<TokenNickNameCheckData>) {
                if (response.body()!!.success) {
                    dialogMessage("아이디가 변경되었습니다.")
                } else {
                    dialogMessage("재시도 해주시기 바랍니다.")
                }
            }
        })
    }

    fun areaSpinnerSelect(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.area_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                areaSelect = spinner.selectedItem.toString()
            }
        }
    }

    fun deleteToken() {
        MyApp.prefs.accountToken = null
        MyApp.prefs.githubToken = null
        val stopIntent = Intent(applicationContext,ForegroundService::class.java)
        stopService(stopIntent)
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("확인", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                nicknameChangeDialog()
            }
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}