package com.example.withub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.withub.mainActivityAdapters.NavFriendRVAdapter
import kotlinx.coroutines.*

class DrawerActivity : AppCompatActivity() {

    lateinit var navFriendRVAdapter: NavFriendRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.activity_drawer)
        val friendApi = RetrofitClient.initRetrofit().create(FriendApi::class.java)
        val myDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)

        //헤더 설정
        val headerImgView = findViewById<ImageView>(R.id.drawer_header_img)
        val headerNicknameView = findViewById<TextView>(R.id.drawer_header_nickname)
        
        CoroutineScope(Dispatchers.Main).launch() {
            //내이름 가져오기
            val job = async(Dispatchers.IO){myDataApi.getMyNickname(MyApp.prefs.accountToken!!)}
            headerNicknameView.text = job.await().nickname
            Glide.with(this@DrawerActivity)
                .load(job.await().avatar_url.toUri())
                .error(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .circleCrop()
                .into(headerImgView)
            
            //리사이클러뷰 설정
            val getFriendList = async(Dispatchers.IO) {
                friendApi.getFriendList(MyApp.prefs.accountToken!!).friends
            }
            val decoration = DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)
            val recyclerView = findViewById<RecyclerView>(R.id.drawer_friend_recycler_View)
            recyclerView.addItemDecoration(decoration)
            navFriendRVAdapter  = NavFriendRVAdapter(this@DrawerActivity, getFriendList.await().toMutableList(), job.await())
            recyclerView.adapter = navFriendRVAdapter

            //네비게이션 드로어 친구추가 AlterDialog
            findViewById<ImageButton>(R.id.drawer_add_friend_button).setOnClickListener {
                val input = EditText(this@DrawerActivity)
                input.hint = "닉네임"
                input.setSingleLine()
                val inputContainer = LinearLayout(this@DrawerActivity)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                input.layoutParams = params
                inputContainer.addView(input)
                val handler = CoroutineExceptionHandler{_,exception->
                    Log.d("error",exception.toString())
                }
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
        findViewById<ImageButton>(R.id.drawer_option_button).setOnClickListener {
            val intent = Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }

        //닫기
        findViewById<ImageButton>(R.id.drawer_exit_button).setOnClickListener {
            navFriendRVAdapter.closeSwipeView()
            this.finish()
        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.hold,R.anim.rightroleft_animation)
    }
}