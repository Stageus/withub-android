package com.example.withub.feature.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.withub.*
import com.example.withub.data.network.*
import retrofit2.Call
import retrofit2.Response

class FindIdActivity : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var count: CountDownTimer
    lateinit var select: String
    lateinit var token: String
    private var running = false
    private var confirmBtnBoolean = false
    val retrofit = RetrofitClient.initRetrofit()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WITHUB)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.findid_activity)
        window.statusBarColor = getColor(R.color.background_color)

        val spinner: Spinner = findViewById(R.id.email_spinner_find_id)
        val emailText = findViewById<EditText>(R.id.email_edittext_find_id)
        val certiNumText = findViewById<EditText>(R.id.certi_num_edittext_find_id)
        val findIdBackBtn = findViewById<ImageButton>(R.id.find_id_back_btn)
        val certificationBtn = findViewById<Button>(R.id.email_certification_btn_find_id)
        val confirmBtn = findViewById<Button>(R.id.certi_num_confirm_btn_find_id)
        val timerTime = findViewById<TextView>(R.id.timer_text_find_id)
        val yourIdTextView = findViewById<TextView>(R.id.your_id_textview_find_id)
        val idText = findViewById<EditText>(R.id.id_edittext_find_id)

        findIdBackBtn.setOnClickListener{
            if (running) {
                count.cancel()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        certificationBtn.setOnClickListener{
            confirmBtnBoolean = true
            if (select == "--선택--") {
                dialogMessage("도메인을 선택해주세요.")
            } else {
                certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                certificationBtn.setTextColor(ContextCompat.getColor(this, R.color.thick_gray))
                certificationBtn.isEnabled = false
                certiNumText.isEnabled = true
                sendMailApi(timerTime)  //api로 메일 보내기

            }
        }

        confirmBtn.setOnClickListener{
            if(!confirmBtnBoolean)  {
                dialogMessage("이메일 인증을 해주세요.")
            } else if (running == false) {
                dialogMessage("시간이 초과되었습니다. 이메일을 다시 인증해주세요.")
                certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                certificationBtn.setTextColor(ContextCompat.getColor(this, R.color.black))
                certificationBtn.isEnabled = true
                certiNumText.isEnabled = true
            } else if (certiNumText.text.isEmpty()){
                dialogMessage("인증번호를 입력해주세요.")
            } else{
                emailCertifyApi(certiNumText,certificationBtn,yourIdTextView,idText)
            }
        }

        emailSpinnerSelect(spinner,emailText)
        emailRegEx(emailText,certificationBtn)
    }

    fun sendMailApi(timerTime: TextView) {
        var inform = FindIdEmailValue(userEmail)
        val requestFindIdSendEmailApi = retrofit.create(FindIdSendEmailApi::class.java)
        requestFindIdSendEmailApi.emailCheck(inform).enqueue(object : retrofit2.Callback<IdFindEmailCheckData> {
            override fun onFailure(
                call: Call<IdFindEmailCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<IdFindEmailCheckData>, response: Response<IdFindEmailCheckData>) {
                if (!response.body()!!.success) {
                    dialogMessage(response.body()!!.message)
                    if (running) {
                        count.cancel()
                    }
                } else {
                    dialogMessage("인증번호가 발송되었습니다.")
                    token = response.body()!!.token
                    if (running) {
                        count.cancel()
                    }
                    timerStart(timerTime)
                }
            }
        })
    }

    fun emailCertifyApi(certiNumText:EditText, certificationBtn:Button,yourIdTextView:TextView, idText: EditText) {
        var inform = FindIdAuthTokenEmailValue(certiNumText.text.toString(),token,userEmail)
        val requestCertiNumConfirmApi = retrofit.create(FindIdCertiNumConfirmApi::class.java)
        requestCertiNumConfirmApi.certiNumCheck(inform).enqueue(object : retrofit2.Callback<FindIdCertiNumCheckData> {
            override fun onFailure(
                call: Call<FindIdCertiNumCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<FindIdCertiNumCheckData>, response: Response<FindIdCertiNumCheckData>) {
                if (response.body()!!.success) {
                    count.cancel()
                    dialogMessage("인증번호가 확인되었습니다.")
                    certiNumText.isEnabled = false
                    appearId(yourIdTextView,idText,response.body()!!.id)
                } else {
                    count.cancel()
                    dialogMessage("인증번호가 틀렸습니다. 다시 인증해주세요.")
                    certiNumText.isEnabled = false
                    certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                    certificationBtn.setTextColor(ContextCompat.getColor(applicationContext,
                        R.color.black
                    ))
                    certificationBtn.isEnabled = true
                }
            }
        })
    }

    fun emailSpinnerSelect(
        spinner: Spinner,
        emailText: EditText,
    ) {
        ArrayAdapter.createFromResource(
            this,
            R.array.email_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                select = spinner.selectedItem.toString()
                userEmail = emailText.getText().toString() + "@" + select
            }
        }
    }

    fun emailRegEx(
        emailText: EditText,
        certificationBtn: Button
    ) {
        emailText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (emailText.text.toString().isNotEmpty()) {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                    certificationBtn.setTextColor(ContextCompat.getColor(this@FindIdActivity,
                        R.color.black
                    ))
                    certificationBtn.isEnabled = true
                    userEmail = emailText.text.toString() + "@" + select
                } else {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    certificationBtn.setTextColor(
                        ContextCompat.getColor(
                            this@FindIdActivity,
                            R.color.thick_gray
                        )
                    )
                    certificationBtn.isEnabled = false
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun appearId(yourIdTextView:TextView,idText: EditText, id : String) {
        yourIdTextView.visibility = View.VISIBLE
        idText.visibility = View.VISIBLE
        idText.setText(id)
    }

    fun timerStart(timerTime: TextView) {
        running = true
        count = object : CountDownTimer(1000 * 300, 1000) {
            override fun onTick(p0: Long) {
                Log.d("ss", "$p0")
                var minuteTime = (p0 / 1000 / 60).toString()
                var secondsTime = (p0 / 1000 % 60).toString()
                if (secondsTime.length == 1) {
                    timerTime.text = "$minuteTime:0$secondsTime"
                } else {
                    timerTime.text = "$minuteTime:$secondsTime"
                }
            }
            override fun onFinish() {
                running = false
            }
        }
        count.start()
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}

