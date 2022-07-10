package com.example.withub.loginFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.*
import com.example.withub.mainFragments.mainFragmentAdapters.SignupRVAdapter
import retrofit2.Call
import retrofit2.Response
import java.lang.NullPointerException

class GitHubRepositoryAddFragment: Fragment() {
    lateinit var githubNickNameValue:String
    lateinit var adapter : SignupRVAdapter
    var repositoryList = ArrayList<UserRepoData>()
    var githubNickNameCheckBoolean = false
    val retrofit = RetrofitClient.initRetrofit()
    val githubRetrofit = GithubClient.getApi()
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.github_repositoryadd_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val githubNickNameText = view.findViewById<EditText>(R.id.github_nickname_edittext_signup)
        val githubNickNameConfirmBtn = view.findViewById<Button>(R.id.github_nickname_confirm_btn_signup)
        val githubNickNameChangeBtn = view.findViewById<Button>(R.id.github_nickname_change_btn_signup)
        val repositoryAddBtn = view.findViewById<Button>(R.id.repository_add_btn_signup)
        val githubOwnerText = view.findViewById<EditText>(R.id.owner_edittext_signup)
        val githubRepositoryText = view.findViewById<EditText>(R.id.repository_edittext_signup)
        val signupBtn = view.findViewById<Button>(R.id.signup_btn_github_repository_add)
        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<ImageButton>(R.id.signup_back_btn)
        val signupText = signupActivity.findViewById<TextView>(R.id.signup_text)
        val warningInform1 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_1)
        val warningInform2 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_2)
        val warningInform3 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_3)
        val warningInform4 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_4)
        signupText.visibility = GONE
        warningInform1.visibility = VISIBLE
        warningInform2.visibility = VISIBLE
        warningInform3.visibility = VISIBLE
        warningInform4.visibility = VISIBLE

        textViewColorChange(warningInform1,16,24,R.color.timer)
        textViewColorChange(warningInform2,1,6,R.color.timer)
        textViewColorChange(warningInform4,0,34,R.color.timer)

        adapter = SignupRVAdapter(repositoryList,view)
        recyclerView.adapter = adapter

        signupBackBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragmentArea, GetTokenFragment())
                .commit()
        }

        githubNickNameConfirmBtn.setOnClickListener{
            githubNickNameDuplicateApi(githubNickNameText,githubNickNameConfirmBtn,githubNickNameChangeBtn)
        }

        repositoryAddBtn.setOnClickListener{
            if (!githubNickNameCheckBoolean) {
                dialogMessage("GitHub 닉네임을 먼저 확인해주세요.")
            } else {
                githubOwnerRepoCheckApi(githubOwnerText,githubRepositoryText,githubNickNameText,repositoryList,adapter)
            }
        }

        githubNickNameChangeBtn.setOnClickListener{
            if (repositoryList.isNotEmpty()) {
                builder.setMessage("닉네임을 변경하면 추가한 레포지토리가 전부 삭제됩니다.")
                builder.setPositiveButton("확인") { _, _ ->
                    repositoryList.clear()
                    adapter.deleteAllItem()
                    githubOwnerText.text = null
                    githubRepositoryText.text = null
                    githubNickNameText.text = null
                    confirmBtnVisible(githubNickNameConfirmBtn,githubNickNameChangeBtn,githubNickNameText)
                }
                builder.setNegativeButton("취소", null)
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            } else {
                githubNickNameText.text = null
                confirmBtnVisible(githubNickNameConfirmBtn,githubNickNameChangeBtn,githubNickNameText)
            }
        }

        signupBtn.setOnClickListener{
            //데이터 끌고 액티비티로 이동
            signupActivity.githubNickNameRepoInform(githubNickNameValue,repositoryList)
        }

        githubNickNameRegEx(githubNickNameText,githubNickNameConfirmBtn)
        githubOwnerRepoRegEx(githubOwnerText,githubRepositoryText,repositoryAddBtn)

        return view
    }

    fun confirmBtnVisible(githubNickNameConfirmBtn:Button,githubNickNameChangeBtn:Button,githubNickNameText:EditText) {
        githubNickNameCheckBoolean = false
        githubNickNameConfirmBtn.visibility = VISIBLE
        githubNickNameChangeBtn.visibility = GONE
        githubNickNameText.isEnabled = true
    }

    fun textViewColorChange(textView: TextView, start:Int, end:Int, color: Int) {
        val builder = SpannableStringBuilder(textView.text.toString())
        val colorPoint = ForegroundColorSpan(resources.getColor(color,null))
        builder.setSpan(colorPoint,start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = builder
    }

    fun githubNickNameRegEx(githubNickNameText:EditText,githubNickNameConfirmBtn:Button) {
        githubNickNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (githubNickNameText.text.toString().isNotEmpty()) {
                    githubNickNameConfirmBtn.setBackgroundResource(R.drawable.stroke_btn)
                    githubNickNameConfirmBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    githubNickNameConfirmBtn.isEnabled = true
                } else {
                    githubNickNameConfirmBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    githubNickNameConfirmBtn.setTextColor(ContextCompat.getColor(context!!, R.color.thick_gray))
                    githubNickNameConfirmBtn.isEnabled = false
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun githubOwnerRepoRegEx(githubOwnerText: EditText,githubRepositoryText: EditText,repositoryAddBtn:Button){
        githubOwnerText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (githubOwnerText.text.toString().isNotEmpty() && githubRepositoryText.text.toString().isNotEmpty()) {
                    activateAddBtn(repositoryAddBtn)
                } else {
                    disabledAddBtn(repositoryAddBtn)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        githubRepositoryText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (githubOwnerText.text.toString().isNotEmpty() && githubRepositoryText.text.toString().isNotEmpty()) {
                    activateAddBtn(repositoryAddBtn)
                } else {
                    disabledAddBtn(repositoryAddBtn)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun activateAddBtn(repositoryAddBtn: Button){
        repositoryAddBtn.setBackgroundResource(R.drawable.stroke_btn)
        repositoryAddBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        repositoryAddBtn.isEnabled = true
    }

    fun disabledAddBtn(repositoryAddBtn: Button){
        repositoryAddBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
        repositoryAddBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.thick_gray))
        repositoryAddBtn.isEnabled = false
    }

    fun githubNickNameDuplicateApi( githubNickNameText: EditText,githubNickNameConfirmBtn: Button,githubNickNameChangeBtn:Button){
        val requestGithubNickNameCheckApi = githubRetrofit.create(GithubNickNameCheckApi::class.java)
        requestGithubNickNameCheckApi.githubNickNameCheck(githubNickNameText.text.toString()).enqueue(object : retrofit2.Callback<GithubNickNameData> {
            override fun onFailure(
                call: Call<GithubNickNameData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<GithubNickNameData>, response: Response<GithubNickNameData>) {
                try {
                    response.body()!!.login
                    githubNickNameValue =githubNickNameText.text.toString()
                    githubNickNameCheckBoolean = true
                    githubNickNameConfirmBtn.visibility = GONE
                    githubNickNameChangeBtn.visibility = VISIBLE
                    githubNickNameText.isEnabled = false
                    dialogMessage("GiHub 닉네임이 확인되었습니다.")
                } catch (e:NullPointerException) {
                    dialogMessage("일치하는 GitHub 닉네임이 없습니다.")
                }
            }
        })
    }

    fun githubOwnerRepoCheckApi(githubOwnerText: EditText, githubRepositoryText:EditText,githubNickNameText:EditText, repositoryList: ArrayList<UserRepoData>, adapter:SignupRVAdapter) {
        val requestGithubOwnerRepoCheckApi = githubRetrofit.create(GithubOwnerRepoCheckApi::class.java)
        requestGithubOwnerRepoCheckApi.githubOwnerRepoCheck(githubOwnerText.text.toString(),githubRepositoryText.text.toString()).enqueue(object : retrofit2.Callback<List<LoginListData>> {
            override fun onFailure(
                call: Call<List<LoginListData>>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<List<LoginListData>>, response: Response<List<LoginListData>>) {
                try{
                    response.body()!![0]
                    val committerList = ArrayList<String>()
                    for (i in 0 until response.body()!!.size) {
                        committerList.add(response.body()!![i].login)
                    }
                    var commiterInList = committerList.contains(githubNickNameText.text.toString())
                     if (repositoryList.contains(UserRepoData(githubOwnerText.text.toString(),githubRepositoryText.text.toString()))) {
                        dialogMessage("이미 추가한 레포지토리입니다.")
                    } else if (commiterInList){
                        dialogMessage("레포지토리가 추가되었습니다.")
                         adapter.addItem(UserRepoData(githubOwnerText.text.toString(),githubRepositoryText.text.toString()))
                    } else {
                        dialogMessage("레포지토리에 본인의 커밋내역이 없습니다.")
                    }
                } catch (e:NullPointerException){
                    dialogMessage("일치하는 GitHub 레포지토리가 없습니다.")
                }
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



