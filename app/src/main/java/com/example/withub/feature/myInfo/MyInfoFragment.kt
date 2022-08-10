package com.example.withub.feature.myInfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.withub.*
import com.example.withub.data.network.MyDataApi
import com.example.withub.data.network.RetrofitClient
import com.example.withub.databinding.FragmentMyInfoBinding
import com.example.withub.feature.account.AccountActivity
import com.example.withub.feature.account.GitHubInfoChangeActivity
import com.example.withub.feature.base.MainActivity
import com.example.withub.feature.base.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyInfoFragment: Fragment() {
    private lateinit var binding : FragmentMyInfoBinding
    lateinit var mainActivity : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_info,container,false)
        val view: View = binding.root
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myDataApi: MyDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val job = async(Dispatchers.IO){myDataApi.getMyNickname(MyApp.prefs.accountToken!!)}
            binding.myInfoNickname.text = job.await().nickname
            Glide.with(mainActivity)
                .load(job.await().avatar_url.toUri())
                .error(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.myInfoImgView)
        }

        //깃허브 정보 변경
        binding.myInfoChangeGithubInfo.setOnClickListener {
            val intent = Intent(mainActivity, GitHubInfoChangeActivity::class.java)
            startActivity(intent)
        }

        //계정 정보 변경
        binding.myInfoAccount.setOnClickListener {
            val intent = Intent(mainActivity, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}