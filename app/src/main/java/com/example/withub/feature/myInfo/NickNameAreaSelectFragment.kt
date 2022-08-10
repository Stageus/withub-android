package com.example.withub.feature.myInfo

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.withub.*
import com.example.withub.data.network.NickNameCheckApi
import com.example.withub.data.network.NickNameCheckData
import com.example.withub.data.network.NickNameValue
import com.example.withub.data.network.RetrofitClient
import com.example.withub.feature.signUp.SignupActivity
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class NickNameAreaSelectFragment: Fragment() {
    lateinit var areaSelect: String
    lateinit var nickNameValue: String
    var nickNameCheckBoolean = false
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.nickname_areaselect_fragment, container, false)
        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<ImageButton>(R.id.signup_back_btn)
        val spinner: Spinner = view.findViewById(R.id.area_spinner_signup)
        val nickNameText = view.findViewById<EditText>(R.id.nickname_edittext_signup)
        val nickNameDuplicateBtn = view.findViewById<Button>(R.id.nickname_duplicate_check_btn_signup)
        val nickNameChangeBtn = view.findViewById<Button>(R.id.nickname_change_btn_signup)
        val nickNameCheckView = view.findViewById<TextView>(R.id.nickname_check_textview_signup)
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

        signupBackBtn.setOnClickListener {
            signupActivity.nicknameAreaSelectToEmailCerti()
        }

        nickNameChangeBtn.setOnClickListener{
            nickNameCheckBoolean = false
            nickNameDuplicateBtn.visibility = VISIBLE
            nickNameChangeBtn.visibility = GONE
            nickNameText.isEnabled = true
            nextBtnActivate(view)
        }

        nickNameDuplicateBtn.setOnClickListener{
                nickNameDuplicateApi(view,nickNameText,nickNameDuplicateBtn,nickNameChangeBtn)
        }

        nickNameRegExp(nickNameText,nickNameDuplicateBtn,nickNameCheckView)
        areaSpinnerSelect(view,spinner)
        return view
    }

    fun nickNameDuplicateApi(view:View,nickNameText: EditText,nickNameDuplicateBtn:Button,nickNameChangeBtn:Button) {
        val inform = NickNameValue(nickNameText.text.toString())
        val requestNickNameCheckApi = retrofit.create(NickNameCheckApi::class.java)
        requestNickNameCheckApi.nickNameCheck(inform).enqueue(object : retrofit2.Callback<NickNameCheckData> {
            override fun onFailure(
                call: Call<NickNameCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<NickNameCheckData>, response: Response<NickNameCheckData>) {
                if (!response.body()!!.success) {
                    dialogMessage("이미 사용중인 닉네임입니다.")
                } else {
                    nickNameValue =nickNameText.text.toString()
                    nickNameCheckBoolean = true
                    nickNameDuplicateBtn.visibility = GONE
                    nickNameChangeBtn.visibility = VISIBLE
                    nickNameText.isEnabled = false
                    nextBtnActivate(view)
                    dialogMessage("${response.body()!!.message}")
                }
            }
        })
    }

    fun nextBtnActivate(view: View) {
        var nextBtn = view.findViewById<Button>(R.id.next_btn_nickname_area_select)
        if (nickNameCheckBoolean == true && areaSelect != "--선택--") {
            nextBtn.setBackgroundResource(R.drawable.login_btn)
            nextBtn.setEnabled(true)
            nextBtn.setOnClickListener {
                var signupActivity = activity as SignupActivity
                signupActivity.nickNameAreaInform(nickNameValue,areaSelect)
            }
        } else {
            nextBtn.setBackgroundResource(R.drawable.disabled_button)
            nextBtn.setEnabled(false)
        }
    }

    fun areaSpinnerSelect(view:View,spinner:Spinner) {
        ArrayAdapter.createFromResource(
            requireContext(),
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
                nextBtnActivate(view)
            }
        }
    }

    fun nickNameRegExp(nickNameText:EditText,nickNameDuplicateBtn:Button,nickNameCheckView:TextView){
        nickNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var boolean = Pattern.matches("^[a-z0-9가-힣]*$", nickNameText.getText().toString())
                if (boolean==false || nickNameText.getText().length < 2) {
                    nickNameCheckView.setText("2~10자 한글, 영어, 숫자만 사용하세요.")
                    nickNameDuplicateBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    nickNameDuplicateBtn.setTextColor(ContextCompat.getColor(context!!, R.color.gray))
                    nickNameDuplicateBtn.setEnabled(false)
                } else {
                    nickNameCheckView.setText("")
                    nickNameDuplicateBtn.setBackgroundResource(R.drawable.stroke_btn)
                    nickNameDuplicateBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    nickNameDuplicateBtn.setEnabled(true)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}