package com.example.withub

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Insets
import android.graphics.Insets.add
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.withub.loginFragments.*
import retrofit2.Call
import retrofit2.Response

class SignupActivity: AppCompatActivity() {
    lateinit var email: String
    lateinit var id: String
    lateinit var pw: String
    lateinit var nickName: String
    lateinit var area: String
    lateinit var areaNum : String
    lateinit var githubNickName : String
    lateinit var repositoryList : List<UserRepoData>
    val retrofit = RetrofitClient.initRetrofit()
    val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        window.statusBarColor = getColor(R.color.background_color)

        if (savedInstanceState == null) {
            fragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragmentArea, TermsOfUseFragment(), "termsOfUseFragment")
                }
            }
        }

    fun nicknameAreaSelectToEmailCerti() {
        var fragment = EmailCertifyFragment()
        var myBundle = Bundle()
        myBundle.putString("id", id)
        fragment.arguments = myBundle
        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragment).commit()

    }


    fun idPwInform(idValue:String, pwValue:String){
        id = idValue
        pw = pwValue
        var fragment = EmailCertifyFragment()
        var myBundle = Bundle()
        myBundle.putString("id", id)
        fragment.arguments = myBundle
        fragmentManager.commit {
            add(R.id.fragmentArea, fragment, "emailCertifyFragment")
            addToBackStack(null)
        }
    }

    fun emailInform(emailValue:String) {
        email = emailValue
        var fragment = NickNameAreaSelectFragment()
        fragmentManager.commit {
            add(R.id.fragmentArea, fragment, "nickNameAreaSelectFragment")
            addToBackStack(null)
        }
    }

    fun nickNameAreaInform(nickNameValue:String,areaValue: String){
        nickName = nickNameValue
        area = areaValue
        var fragment = GetTokenFragment()
        fragmentManager.commit {
            add(R.id.fragmentArea, fragment, "getTokenFragment")
            addToBackStack(null)
        }
//        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragment).commit()
    }

    fun githubNickNameRepoInform(githubNickNameValue: String, repositoryListValue: List<UserRepoData>) {
        githubNickName = githubNickNameValue
        repositoryList = repositoryListValue
        var getArray = resources.getStringArray(R.array.area_array)
        for (i in getArray.indices) {
            if (getArray[i] == area) {
                areaNum = i.toString()
            }
        }
        val inform = SignupValue(id,pw,nickName,email,areaNum.toInt(),githubNickName,repositoryList)
        val requestSignupApi = retrofit.create(SignupApi::class.java)
        requestSignupApi.signupCheck(inform).enqueue(object : retrofit2.Callback<SignupCheckData> {
            override fun onFailure(
                call: Call<SignupCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<SignupCheckData>, response: Response<SignupCheckData>) {
                if (response.body()!!.success) {
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@SignupActivity, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    var builder: AlertDialog.Builder = AlertDialog.Builder(this@SignupActivity)
                    builder.setMessage("서버연결에 문제가 발생하였습니다.")
                    builder.setPositiveButton("확인", null)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }
            }
        })
    }


}


