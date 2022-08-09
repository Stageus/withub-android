package com.example.withub

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.withub.databinding.ActivityFriendBinding
import com.example.withub.mainActivityAdapters.FriendRepoListRVAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import kotlinx.coroutines.*

class FriendActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFriendBinding
    private val friendApi: FriendApi = RetrofitClient.initRetrofit().create(FriendApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_friend
        )

        //친구 이름 가져오기
        val handler = CoroutineExceptionHandler{_,exception->
            Log.d("error",exception.toString())
        }
        val friendNickName = intent.getStringExtra("friendNickName")!!
        val myNickName = intent.getStringExtra("myNickName")!!
        val myAvatar = intent.getStringExtra("myAvatar")!!
        val friendAvatar = intent.getStringExtra("friendAvatar")!!

        //뒤로가기
        binding.friendActivityBackBtn.setOnClickListener {
            finish()
        }

        //커밋 승자 뷰
        glide(myAvatar,binding.friendActivityUserImage)
        glide(friendAvatar,binding.friendActivityFriendImage)

        //닉네임설정
        binding.friendActivityToolbarTextview.text = getString(R.string.neam,friendNickName)
        binding.friendActivityFriendName.text = friendNickName
        binding.friendActivityUserName.text = myNickName

        binding.friendActivityRepositoryTitle.text = getString(R.string.friend_repository_list,friendNickName)

        //그래프 설정
        binding.friendHorizontalScroll.post { binding.friendHorizontalScroll.scrollTo(binding.friendLineChart.width,0) }

        CoroutineScope(Dispatchers.Main).launch(handler) {
            val callFriendDataApi = withContext(Dispatchers.IO) {
                friendApi.getFriendInfo(MyApp.prefs.accountToken!!,friendNickName)
            }

            binding.friendActivityFriendTodayCommit.text = callFriendDataApi.friend_daily.toString()//오늘 커밋

            //친구 커밋
            initLineChart(callFriendDataApi.thirty_commit)//차트 x축
            setDataToLineChart(callFriendDataApi.thirty_commit)//차트 커밋수 조절
            getGrassImg(binding.friendCommitGrassImgView,callFriendDataApi.committer) //잔디 불러오기

            //승자
            binding.friendActivityUserMonthCommitNum.text = callFriendDataApi.my_month_total.toString()+"회"
            binding.friendActivityFriendMonthCommitNum.text = callFriendDataApi.friend_month_total.toString()+"회"

            if(callFriendDataApi.friend_month_total>callFriendDataApi.my_month_total){
                //친구승
                binding.friendActivityLeftCrown.visibility = View.INVISIBLE
                binding.friendActivityRightCrown.visibility = View.VISIBLE
                binding.friendActivityVs.text = "<"

            }else if(callFriendDataApi.friend_month_total<callFriendDataApi.my_month_total){
                //나 승
                binding.friendActivityLeftCrown.visibility = View.VISIBLE
                binding.friendActivityRightCrown.visibility = View.INVISIBLE
                binding.friendActivityVs.text = ">"
            }else if(callFriendDataApi.friend_month_total==callFriendDataApi.my_month_total){
                //같을 때
                binding.friendActivityLeftCrown.visibility = View.GONE
                binding.friendActivityRightCrown.visibility = View.GONE
                binding.friendActivityVs.text = "="
            }

            //레포 설정
            binding.friendActivityRecyclerView.apply {
                adapter = FriendRepoListRVAdapter(this@FriendActivity,callFriendDataApi.repository)
                setHasFixedSize(true)
            }
        }

        binding.friendSwipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                val callFriendDataApi = withContext(Dispatchers.IO) { friendApi.getFriendInfo(MyApp.prefs.accountToken!!,friendNickName) }
                binding.friendActivityFriendTodayCommit.text = callFriendDataApi.friend_daily.toString()//오늘 커밋

                //친구 커밋
                initLineChart(callFriendDataApi.thirty_commit)//차트 x축
                setDataToLineChart(callFriendDataApi.thirty_commit)//차트 커밋수 조절
                getGrassImg(binding.friendCommitGrassImgView,callFriendDataApi.committer) //잔디 불러오기

                //승자
                binding.friendActivityUserMonthCommitNum.text = callFriendDataApi.my_month_total.toString()
                binding.friendActivityFriendMonthCommitNum.text = callFriendDataApi.friend_month_total.toString()

                if(callFriendDataApi.friend_month_total>callFriendDataApi.my_month_total){
                    //친구승
                    binding.friendActivityLeftCrown.visibility = View.INVISIBLE
                    binding.friendActivityRightCrown.visibility = View.VISIBLE

                }else if(callFriendDataApi.friend_month_total<callFriendDataApi.my_month_total){
                    //나 승
                    binding.friendActivityLeftCrown.visibility = View.VISIBLE
                    binding.friendActivityRightCrown.visibility = View.INVISIBLE
                }else if(callFriendDataApi.friend_month_total==callFriendDataApi.my_month_total){
                    //같을 때
                    binding.friendActivityLeftCrown.visibility = View.GONE
                    binding.friendActivityRightCrown.visibility = View.GONE
                }

                //레포 설정
                val decoration = DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)
                binding.friendActivityRecyclerView.apply {
                    adapter = FriendRepoListRVAdapter(this@FriendActivity,callFriendDataApi.repository)
                    setHasFixedSize(true)
                    addItemDecoration(decoration)
                }
                binding.friendSwipeRefreshLayout.isRefreshing = false
                Toast.makeText(this@FriendActivity,"업데이트 완료", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initLineChart(dateList : List<MyThirtyCommits>){
        binding.friendLineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled  = false
            axisLeft.setDrawGridLines(false)
            description.isEnabled = false
            isDragXEnabled = true
            isScaleYEnabled = false
            isScaleXEnabled = false
        }
        val xAxis: XAxis = binding.friendLineChart.xAxis
        // to draw label on xAxis
        xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(true)
            setDrawLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisCustomFormatter(addXAisle(dateList))
            textColor = resources.getColor(R.color.text_color,null)
            textSize = 10f
            labelRotationAngle = 0f
            setLabelCount(30,false)
        }
    }

    inner class XAxisCustomFormatter(private val xAxisData : ArrayList<String>) : ValueFormatter(){

        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }
    }
    private fun setDataToLineChart(commitList : List<MyThirtyCommits>){

        val entries: ArrayList<Entry> = ArrayList()
        for (i in commitList.indices){
            entries.add(Entry(i.toFloat(),commitList[i].commit.toFloat()))
        }

        val lineDataSet = LineDataSet(entries,"entries")
        lineDataSet.apply {
            color = resources.getColor(R.color.point_color,null)
            circleRadius = 5f
            lineWidth = 3f
            setCircleColor(resources.getColor(R.color.graph_dot_color,null))
            circleHoleColor = resources.getColor(R.color.graph_dot_color,null)
            setDrawHighlightIndicators(false)
            setDrawValues(true) // 숫자표시
            valueTextColor = resources.getColor(R.color.text_color,null)
            valueFormatter = DefaultValueFormatter(0)
            valueTextSize = 10f
        }

        binding.friendLineChart.apply {
            data = LineData(lineDataSet)
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun addXAisle(dateList : List<MyThirtyCommits>) : ArrayList<String>{
        val dataTextList = ArrayList<String>()
        for (i in dateList.indices){
            val textSize = dateList[i].date.length
            val dateText = dateList[i].date.substring(textSize-2,textSize)
            if(dateText=="01"){
                dataTextList.add(dateList[i].date)
            }else{
                dataTextList.add(dateText)
            }
        }
        return dataTextList
    }


    private fun getGrassImg(imageView: ImageView, url : String){
        GlideToVectorYou.init()
            .with(this)
            .withListener(object : GlideToVectorYouListener{
                override fun onLoadFailed() {
                }
                override fun onResourceReady() {
                }
            })
            .load("https://ghchart.rshah.org/219138/$url".toUri(),imageView)
    }

    private fun glide(url: String, imageView: ImageView){
        Glide.with(this@FriendActivity)
            .load(url.toUri())
            .error(R.mipmap.ic_launcher)
            .fallback(R.mipmap.ic_launcher)
            .circleCrop()
            .into(imageView)
    }
}