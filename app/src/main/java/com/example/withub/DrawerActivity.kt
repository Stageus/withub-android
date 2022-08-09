package com.example.withub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.withub.databinding.ActivityDrawerBinding
import com.example.withub.mainActivityAdapters.DrawerFriendListRVAdapter
import kotlinx.coroutines.*

class DrawerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDrawerBinding
    lateinit var navFriendRVAdapter: DrawerFriendListRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_drawer
        )
        binding.activity = this@DrawerActivity
        val friendApi = RetrofitClient.initRetrofit().create(FriendApi::class.java)
        val myDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)
        val handler = CoroutineExceptionHandler{_,exception->
            Log.d("error",exception.toString())
        }
        //헤더 설정

        CoroutineScope(Dispatchers.Main).launch() {
            //내이름 가져오기
            val job = async(Dispatchers.IO){myDataApi.getMyNickname(MyApp.prefs.accountToken!!)}
            binding.drawerHeaderNickname.text = job.await().nickname
            Glide.with(this@DrawerActivity)
                .load(job.await().avatar_url.toUri())
                .error(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.drawerHeaderImg)
            
            //리사이클러뷰 설정
            val getFriendList = async(Dispatchers.IO) {
                friendApi.getFriendList(MyApp.prefs.accountToken!!).friends
            }

            binding.drawerFriendRecyclerView.apply {
                addItemDecoration(DividerItemDecoration(applicationContext, RecyclerView.VERTICAL))
                adapter = DrawerFriendListRVAdapter(this@DrawerActivity, getFriendList.await().toMutableList(), job.await())
            }

            //네비게이션 드로어 친구추가 AlterDialog
            binding.drawerAddFriendButton.setOnClickListener {
                val input = EditText(this@DrawerActivity)
                input.hint = "닉네임"
                input.setSingleLine()
                val inputContainer = LinearLayout(this@DrawerActivity)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                input.layoutParams = params
                inputContainer.addView(input)

                val dialog : AlertDialog.Builder = AlertDialog.Builder(this@DrawerActivity)
                dialog.setTitle("친구추가")
                    .setMessage("친구의 닉네임을 입력해 주세요.")
                    .setView(inputContainer)
                    .setPositiveButton("추가"){ _, _ ->
                        CoroutineScope(Dispatchers.Main).launch(handler){
                            val addFriendToList = async(Dispatchers.IO) {
                                friendApi.addFriend(FriendNameData(MyApp.prefs.accountToken!!,input.text.toString()))
                            }
                            //추가 성공
                            if(addFriendToList.await().success){
                                val friendList = withContext(Dispatchers.IO) {
                                    friendApi.getFriendList(MyApp.prefs.accountToken!!).friends
                                }
                                navFriendRVAdapter.addItem(friendList[friendList.lastIndex],friendList.size)
                            }else{
                                val failDialog : AlertDialog.Builder = AlertDialog.Builder(this@DrawerActivity)
                                failDialog.setTitle("친구추가 실패")
                                    .setMessage(addFriendToList.await().message)
                                    .setPositiveButton("확인"){_,_->}
                                    .show()
                            }
                        }
                    }
                    .setNegativeButton("취소"){ _, _ ->  }
                    .show()
            }
        }

        //설정창
        binding.drawerOptionButton.setOnClickListener {
            val intent = Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.hold,R.anim.rightroleft_animation)
    }

    //닫기
    private fun closeDrawer(){
        navFriendRVAdapter.closeSwipeView()
        this.finish()
    }
}