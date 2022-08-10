package com.example.withub.feature.account

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.withub.*
import com.example.withub.data.network.*
import com.example.withub.feature.base.MyApp
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.lang.NullPointerException
import java.time.LocalDateTime
import java.time.ZoneId

class GitHubInfoChangeActivity: AppCompatActivity() {
    lateinit var adapter : GithubInfoChangeRVAdapter
    var repositoryList = arrayListOf<Repositories>()
    lateinit var committer : String
    val retrofit = RetrofitClient.initRetrofit()
    val githubRetrofit = GithubClient.getApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.githubinfo_change_activity)
        window.statusBarColor = getColor(R.color.point_color)
        val githubOwnerText = findViewById<EditText>(R.id.owner_edittext_info_change)
        val githubRepositoryText = findViewById<EditText>(R.id.repository_edittext_info_change)
        val repositoryAddBtn = findViewById<Button>(R.id.repository_add_btn_info_change)
        val changeBtn = findViewById<Button>(R.id.change_btn_info_change)
        val backBtn = findViewById<ImageButton>(R.id.back_btn_info_change)

        adapter = GithubInfoChangeRVAdapter(repositoryList,changeBtn)

        backBtn.setOnClickListener{
            finish()
        }

        repositoryAddBtn.setOnClickListener{
            githubOwnerRepoCheckApi(githubOwnerText,githubRepositoryText,repositoryList,adapter)
        }

        changeBtn.setOnClickListener{
            changeGithubRepositoryApi()
            finish()
        }
        getRepositoryApi()
        githubOwnerRepoRegEx(githubOwnerText,githubRepositoryText,repositoryAddBtn)
    }
    fun getRepositoryApi(){
        val requestMyRepoDataApi = retrofit.create(GetRepoDataApi::class.java)
        requestMyRepoDataApi.getMyRepoData(MyApp.prefs.accountToken!!).enqueue(object : retrofit2.Callback<MyRepoData> {
            override fun onFailure(
                call: Call<MyRepoData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<MyRepoData>, response: Response<MyRepoData>) {
                if (response.body()!!.success) {
                    committer = response.body()!!.committer
                    for (i in 0 until response.body()!!.repository.size) {
                        adapter.addItem(Repositories(response.body()!!.repository[i].owner,response.body()!!.repository[i].name))
                    }
                } else {
                    dialogMessage("레포지토리 데이터를 가져오지 못했습니다.")
                }
            }
        })
    }

    fun changeGithubRepositoryApi() {
        var inform = TokenRepositoryValue(MyApp.prefs.accountToken!!,repositoryList)
        val requestChangeGitHubRepoApi = retrofit.create(ChangeRepositoryApi::class.java)
        requestChangeGitHubRepoApi.tokenRepoCheck(inform).enqueue(object : retrofit2.Callback<TokenRepositoryCheckData> {
            override fun onFailure(
                call: Call<TokenRepositoryCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<TokenRepositoryCheckData>, response: Response<TokenRepositoryCheckData>) {
                if (response.body()!!.success) {
                    forceUpdate()
                    Toast.makeText(this@GitHubInfoChangeActivity, "레포지토리가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    dialogMessage("레포지토리 데이터 수정에 실패하였습니다.")
                }
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
        repositoryAddBtn.setTextColor(ContextCompat.getColor(this, R.color.black))
        repositoryAddBtn.isEnabled = true
    }

    fun disabledAddBtn(repositoryAddBtn: Button){
        repositoryAddBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
        repositoryAddBtn.setTextColor(ContextCompat.getColor(this, R.color.thick_gray))
        repositoryAddBtn.isEnabled = false
    }

    fun githubOwnerRepoCheckApi(githubOwnerText: EditText, githubRepositoryText:EditText, repositoryList: ArrayList<Repositories>, adapter: GithubInfoChangeRVAdapter) {
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
                    var commiterInList = committerList.contains(committer)
                    if (repositoryList.contains(Repositories(githubOwnerText.text.toString(),githubRepositoryText.text.toString()))) {
                        dialogMessage("이미 추가한 레포지토리입니다.")
                    } else if (commiterInList){
                        dialogMessage("레포지토리가 추가되었습니다.")
                        adapter.addItem(Repositories(githubOwnerText.text.toString(),githubRepositoryText.text.toString()))
                    } else {
                        dialogMessage("레포지토리에 본인의 커밋내역이 없습니다.")
                    }
                } catch (e: NullPointerException){
                    dialogMessage("일치하는 GitHub 레포지토리가 없습니다.")
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

    fun forceUpdate(){
        val handler = CoroutineExceptionHandler{_,exception->
            Log.d("error",exception.toString())
        }
        val requestCommitApi= GithubClient.getApi().create(GitHubInfoApi::class.java)
        val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
        val myRepoApi= RetrofitClient.initRetrofit().create(MyRepoDataApi::class.java)
        val infoApi =  RetrofitClient.initRetrofit().create(InfoApi::class.java)
        CoroutineScope(Dispatchers.IO).launch(handler) {
            // 시간포멧
            val nowDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            val sinceTime =
                nowDateTime.minusDays(30L).withHour(0).withMinute(0).withSecond(0).withNano(0).toString()

            val myRepoData : MyRepoData = withContext(Dispatchers.IO) {
                myRepoApi.getMyRepoData(MyApp.prefs.accountToken!!)
            }
            //                깃허브 정보 호출
            val committer = myRepoData.committer
            val commitsInAllRepo = arrayListOf<GitHubCommitDatasItem>()
            for (i in myRepoData.repository.indices) {
                val commitsInOneRepo = ArrayList<GitHubCommitDatasItem>()
                var count = 1
                while (true){
                    val gitHubCommitDatas: Deferred<GitHubCommitDatas> = async(Dispatchers.IO) {
                        requestCommitApi.getInfo(
                            MyApp.prefs.githubToken!!,
                            myRepoData.repository[i].owner,
                            myRepoData.repository[i].name,
                            committer,
                            sinceTime,
                            nowDateTime.toString(),
                            100,
                            count
                        )
                    }
                    val a = gitHubCommitDatas.await()
                    if(a.isEmpty()){
                        break
                    }else{
                        commitsInOneRepo.addAll(a)
                        count++
                    }
                }
                commitsInAllRepo.addAll(commitsInOneRepo)
            }

            val result = gsonBuilder.toJson(commitsInAllRepo)
            Log.d("결과",result)
            val infoData = InfoData(MyApp.prefs.accountToken!!,commitsInAllRepo)
            val resultMessage = infoApi.sendGithubDataToServer(infoData).message
            Log.d("강제결과",resultMessage)
        }
    }
}