package com.example.withub.mainFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.withub.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyInfoFragment: Fragment() {
    val myDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)
    lateinit var mainActivity : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.myinfo_fragment, container, false)
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickName = view.findViewById<TextView>(R.id.my_info_nickname)
        val myImg = view.findViewById<ImageView>(R.id.my_info_img_view)
        CoroutineScope(Dispatchers.Main).launch {
            val job = async(Dispatchers.IO){myDataApi.getMyNickname(MyApp.prefs.accountToken!!)}
            nickName.text = job.await().nickname
            Glide.with(mainActivity)
                .load(job.await().avatar_url.toUri())
                .error(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .circleCrop()
                .into(myImg)
        }

        val changeGitHubInfoView = view.findViewById<LinearLayout>(R.id.my_info_change_github_info)
        val changeAccountView = view.findViewById<LinearLayout>(R.id.my_info_account)
        //깃허브 정보 변경
        changeGitHubInfoView.setOnClickListener {
            val intent = Intent(mainActivity, GitHubInfoChangeActivity::class.java)
            startActivity(intent)
        }

        //계정 정보 변경
        changeAccountView.setOnClickListener {
            val intent = Intent(mainActivity, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}