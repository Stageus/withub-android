package com.example.withub.feature.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.withub.*
import com.example.withub.data.network.FindPwChangePwApi
import com.example.withub.data.network.FindPwChangePwCheckData
import com.example.withub.data.network.FindPwTokenPwValue
import com.example.withub.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class ChangePwFragment:Fragment() {
    lateinit var token : String
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.changepw_fragment, container, false)
        val requestToken = arguments?.getString("token")
        if (requestToken != null) {
            token = requestToken
        }
        val pwText = view.findViewById<EditText>(R.id.pw_edittext_find_pw)
        val pwCheckView = view.findViewById<TextView>(R.id.pw_check_textview_find_pw)
        val safetyView = view.findViewById<TextView>(R.id.pw_safety_textview_find_pw)
        val pwConfirmText = view.findViewById<EditText>(R.id.pw_confirm_edittext_find_pw)
        val pwMismatchView = view.findViewById<TextView>(R.id.pw_confirm_mismatch_textview_find_pw)
        val changePwBtn = view.findViewById<Button>(R.id.change_pw_btn_find_pw)
        val findPwActivity = activity as FindPwActivity
        val backBtn = findPwActivity.findViewById<ImageButton>(R.id.back_btn_find_pw)

        backBtn.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.fragmentArea_find_pw, PwCertifyFragment())
                .commit()
        }

        changePwBtn.setOnClickListener{
            changePwApi(pwText.text.toString())
        }

        pwRegExp(view,pwText,pwCheckView,safetyView,pwConfirmText,pwMismatchView)

        return view
    }

    private fun pwRegExp(view:View, pwText: EditText, pwCheckView: TextView, safetyView: TextView, pwConfirmText: EditText, pwMismatchView: TextView) {
        pwText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var numSpecialBoolean: Boolean = Pattern.matches("^.*(?=^.{8,20}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$", pwText.getText().toString())
                if (!numSpecialBoolean) {
                    pwCheckView.text = "8~20자 영문, 숫자, 특수문자를 사용하세요."
                    safetyView.text = "사용 불가"
                    safetyView.setTextColor(ContextCompat.getColor(context!!, R.color.timer))
                    changePwBtnActivate(view)
                } else{
                    safetyView.setTextColor(ContextCompat.getColor(context!!, R.color.correct_color))
                    safetyView.text = "안전"
                    pwCheckView.text = ""
                    changePwBtnActivate(view)
                }

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (pwConfirmText.length() != 0 && pwText.length() != 0) {
                    if (pwText.text.toString() != pwConfirmText.text.toString()){
                        pwMismatchDisplay(view,pwMismatchView)
                    } else {
                        pwMatchDisplay(view,pwMismatchView)
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
                        pwMismatchDisplay(view, pwMismatchView)
                    } else {
                        pwMatchDisplay(view, pwMismatchView)
                    }
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun pwMismatchDisplay(view: View,pwMismatchView: TextView) {
        pwMismatchView.setTextColor(ContextCompat.getColor(requireContext(), R.color.timer))
        pwMismatchView.text = "불일치"
        changePwBtnActivate(view)
    }

    fun pwMatchDisplay(view: View,pwMismatchView: TextView){
        pwMismatchView.setTextColor(ContextCompat.getColor(requireContext(), R.color.correct_color))
        pwMismatchView.text = "일치"
        changePwBtnActivate(view)
    }

    fun changePwBtnActivate(view: View) {
        var safetyView = view.findViewById<TextView>(R.id.pw_safety_textview_find_pw)
        var pwMismatchView = view.findViewById<TextView>(R.id.pw_confirm_mismatch_textview_find_pw)
        var changePwBtn = view.findViewById<Button>(R.id.change_pw_btn_find_pw)

        if (safetyView.getText().toString() == "안전" && pwMismatchView.getText().toString() == "일치" ){
            changePwBtn.setBackgroundResource(R.drawable.login_btn)
            changePwBtn.isEnabled = true
        } else {
            changePwBtn.setBackgroundResource(R.drawable.disabled_button)
            changePwBtn.isEnabled = false
        }
    }

    fun changePwApi(pwText: String) {
        var inform = FindPwTokenPwValue(token,pwText)
        val requestPwChangeApi = retrofit.create(FindPwChangePwApi::class.java)
        requestPwChangeApi.changePw(inform).enqueue(object : retrofit2.Callback<FindPwChangePwCheckData> {
            override fun onFailure(
                call: Call<FindPwChangePwCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<FindPwChangePwCheckData>, response: Response<FindPwChangePwCheckData>) {
                if (response.body()!!.success) {
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}