package com.example.withub.feature.login

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.withub.R
import com.example.withub.feature.signUp.SignupActivity
import com.example.withub.data.network.IdCheckApi
import com.example.withub.data.network.IdCheckData
import com.example.withub.data.network.IdValue
import com.example.withub.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class IdPwInputFragment: Fragment() {
    val retrofit = RetrofitClient.initRetrofit()
    var idCheckBoolean = false
    var idValue : String = ""
    var pwValue : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.idpwinput_fragment, container, false)
        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<ImageButton>(R.id.signup_back_btn)
        val idText = view.findViewById<EditText>(R.id.id_edittext_signup)
        val idCheckView = view.findViewById<TextView>(R.id.id_check_textview_signup)
        val idDuplicateBtn = view.findViewById<Button>(R.id.id_duplicate_check_btn_signup)
        val idChangeBtn = view.findViewById<Button>(R.id.id_change_btn_signup)
        val pwText = view.findViewById<EditText>(R.id.pw_edittext_signup)
        val pwCheckView = view.findViewById<TextView>(R.id.pw_check_textview_signup)
        val safetyView = view.findViewById<TextView>(R.id.pw_safety_textview_signup)
        val pwConfirmText = view.findViewById<EditText>(R.id.pw_confirm_edittext_signup)
        val pwMismatchView = view.findViewById<TextView>(R.id.pw_confirm_mismatch_textview_signup)
        val signupText = signupActivity.findViewById<TextView>(R.id.signup_text)
        val warningInform1 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_1)
        val warningInform2 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_2)
        val warningInform3 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_3)
        val warningInform4 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_4)
        signupText.visibility = VISIBLE
        warningInform1.visibility = GONE
        warningInform2.visibility = GONE
        warningInform3.visibility = GONE
        warningInform4.visibility = GONE

        signupBackBtn.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.fragmentArea, TermsOfUseFragment()).commit()
        }

        idDuplicateBtn.setOnClickListener{
            idDuplicateApi(view,idText,idDuplicateBtn,idChangeBtn)
        }

        idChangeBtn.setOnClickListener{
            idDuplicateBtnVisible(view,idDuplicateBtn,idChangeBtn,idText)
        }

        idRegExp(idText,idCheckView,idDuplicateBtn)
        pwRegExp(view,pwText,pwCheckView,safetyView,pwConfirmText,pwMismatchView)

        return view
    }

    fun idDuplicateBtnVisible(view:View,idDuplicateBtn:Button,idChangeBtn: Button,idText: EditText) {
        idCheckBoolean = false
        idDuplicateBtn.visibility = VISIBLE
        idChangeBtn.visibility = GONE
        idText.isEnabled = true
        nextBtnActivate(view)
    }

    fun idDuplicateApi(view:View,idText:EditText,idDuplicateBtn:Button,idChangeBtn:Button) {
        var inform = IdValue(idText.text.toString())
        val requestIdCheckApi = retrofit.create(IdCheckApi::class.java)    // 메뉴를 불러올때는 for문 써서 각각의 index값으로 불러오기
        requestIdCheckApi.idCheck(inform).enqueue(object : retrofit2.Callback<IdCheckData> {   //enqueue 로 콜백함수 결과가 들어옴
            override fun onFailure(
                call: Call<IdCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<IdCheckData>, response: Response<IdCheckData>) {
                if (!response.body()!!.success) {
                    dialogMessage("이미 사용중인 아이디입니다.")
                    nextBtnActivate(view)
                } else {
                    idValue = idText.text.toString()
                    idCheckBoolean = true
                    idDuplicateBtn.visibility = GONE
                    idChangeBtn.visibility = VISIBLE
                    idText.isEnabled = false
                    nextBtnActivate(view)
                    dialogMessage("${response.body()!!.message}")
                }
            }
        })
    }

    fun idRegExp(idText: EditText,idCheckView:TextView,idDuplicateBtn:Button) {
        idText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var boolean = Pattern.matches("^[a-z0-9]*$", idText.getText().toString())
                if (boolean==false || idText.getText().length < 5) {
                    idCheckView.setText("5~15자 영문(소문자), 숫자만 사용하세요.")
                    idDuplicateBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    idDuplicateBtn.setTextColor(ContextCompat.getColor(context!!, R.color.gray))
                    idDuplicateBtn.setEnabled(false)
                } else {
                    idCheckView.setText("")
                    idDuplicateBtn.setBackgroundResource(R.drawable.stroke_btn)
                    idDuplicateBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    idDuplicateBtn.setEnabled(true)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun pwRegExp(view:View, pwText:EditText, pwCheckView:TextView, safetyView:TextView, pwConfirmText:EditText, pwMismatchView: TextView) {
        pwText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var numSpecialBoolean: Boolean = Pattern.matches("^.*(?=^.{8,20}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$", pwText.getText().toString())
                if (!numSpecialBoolean) {
                    pwCheckView.text = "8~20자 영문, 숫자, 특수문자를 사용하세요."
                    safetyView.text = "사용 불가"
                    safetyView.setTextColor(ContextCompat.getColor(context!!, R.color.timer))
                    nextBtnActivate(view)
                } else{
                    safetyView.setTextColor(ContextCompat.getColor(context!!, R.color.correct_color))
                    safetyView.text = "안전"
                    pwCheckView.text = ""
                    pwValue = pwText.text.toString()
                    nextBtnActivate(view)
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
        nextBtnActivate(view)
    }

    fun pwMatchDisplay(view: View,pwMismatchView: TextView){
        pwMismatchView.setTextColor(ContextCompat.getColor(requireContext(), R.color.correct_color))
        pwMismatchView.text = "일치"
        nextBtnActivate(view)
    }

    fun nextBtnActivate(view: View) {
        var safetyView = view.findViewById<TextView>(R.id.pw_safety_textview_signup)
        var pwMismatchView = view.findViewById<TextView>(R.id.pw_confirm_mismatch_textview_signup)
        var nextBtn = view.findViewById<Button>(R.id.next_btn_id_pw_input)

        if (safetyView.getText().toString() == "안전" && pwMismatchView.getText().toString() == "일치" && idCheckBoolean){
            nextBtn.setBackgroundResource(R.drawable.login_btn)
            nextBtn.isEnabled = true
            nextBtn.setOnClickListener{
                var signupActivity = activity as SignupActivity
                signupActivity.idPwInform(idValue,pwValue)
            }
        } else {
            nextBtn.setBackgroundResource(R.drawable.disabled_button)
            nextBtn.setEnabled(false)
        }
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}