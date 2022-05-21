package com.example.withub.mainFragments

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.withub.*
import com.example.withub.mainFragments.mainFragmentAdapters.HomePagerRecyclerAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.*

class HomeFragment : Fragment(){

    lateinit var mainActivity: MainActivity
    lateinit var commitGrassImgView : ImageView //잔디
    lateinit var pagerRecyclerView: ViewPager2
    lateinit var lineChart: LineChart //라인 차트
    lateinit var myTodayCommitTextView : TickerView
    lateinit var friendAvgCommitView : TickerView
    lateinit var areaAvgCommitView : TickerView
    lateinit var job : Job
    var myDataApi: MyDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)
    var bannerPosition = (Int.MAX_VALUE/2)+1
    var numBanner = 4
    val handler = CoroutineExceptionHandler{_,exception->
        Log.d("error",exception.toString())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.home_fragment,container,false)
        mainActivity = activity as MainActivity
        if (savedInstanceState !=null){
            bannerPosition = savedInstanceState.getInt("bannerPosition")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testGitHubConnection()
        //nav버튼
        val navButton = view.findViewById<ImageButton>(R.id.nav_button)
        navButton.setOnClickListener {
            val intent = Intent(mainActivity, DrawerActivity::class.java)
            startActivity(intent)
            mainActivity.overridePendingTransition(R.anim.lefttoright_animation, R.anim.hold)
        }

        //topView 설정
        myTodayCommitTextView = view.findViewById(R.id.my_today_commit)

        //30일 커밋 차트
        lineChart  = view.findViewById(R.id.line_chart)
        val horizontalScrollView = view.findViewById<HorizontalScrollView>(R.id.horizontal_scroll)
        horizontalScrollView.post { horizontalScrollView.scrollTo(lineChart.width,0) }

        //친구 평균 뷰
        friendAvgCommitView = view.findViewById(R.id.home_fragment_my_friend_commit_avg)

        //지역 평균 뷰
        areaAvgCommitView = view.findViewById(R.id.home_fragment_my_area_commit_avg)

        //커밋 잔디
        commitGrassImgView = view.findViewById(R.id.main_commit_grass_img_view)

        //팁 뷰페이저
        //이미지 넣기
        val imgList = arrayListOf(R.drawable.view_pager1,R.drawable.view_pager2,R.drawable.view_pager3,R.drawable.view_pager4)
        //url 넣기
        val urlList = arrayListOf(
            "https://www.youtube.com/watch?v=NOVDVW5dask",
            "https://www.youtube.com/watch?v=shZtNaSV5Tk",
            "https://www.youtube.com/watch?v=kp5CEADyTFs",
            "https://www.youtube.com/watch?v=Ru_bHWAqdSM")

        val homePagerRecyclerAdapter= HomePagerRecyclerAdapter(mainActivity,Glide.with(this),imgList,urlList)
        pagerRecyclerView = view.findViewById(R.id.main_view_pager)
        pagerRecyclerView.adapter = homePagerRecyclerAdapter
        pagerRecyclerView.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        pagerRecyclerView.setCurrentItem(bannerPosition,false)
        val totalBannerNumView = view.findViewById<TextView>(R.id.total_banner_text_view)
        totalBannerNumView.text = numBanner.toString()
        val currentBannerNumView = view.findViewById<TextView>(R.id.current_banner_text_view)

        pagerRecyclerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentBannerNumView.text = ((position%4)+1).toString()
                bannerPosition = position
                Log.d("position",position.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                    ViewPager2.SCROLL_STATE_IDLE -> {if (!job.isActive){scrollJobCreate()}}
                    // 뷰페이저 움직이는 중
                    ViewPager2.SCROLL_STATE_DRAGGING -> {job.cancel()}
                    ViewPager2.SCROLL_STATE_SETTLING -> {}
                }
            }
        })

        //swipeRefreshLayout
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.friend_swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                getMainData()
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(mainActivity,"업데이트 완료",Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch(handler) {
            getMainData()
        }
    }

    fun initLineChart(dateList : List<MyThirtyCommits>){
        lineChart.run {
            axisRight.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled  = false
            axisLeft.setDrawGridLines(false)
            description.isEnabled = false
            isDragXEnabled = true
            isScaleYEnabled = false
            isScaleXEnabled = false
        }
        val xAxis: XAxis = lineChart.xAxis
        // to draw label on xAxis
        xAxis.run {
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

    inner class XAxisCustomFormatter( val xAxisData : ArrayList<String>) : ValueFormatter(){

        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }
    }
    fun setDataToLineChart(commitList : List<MyThirtyCommits>){

        val entries: ArrayList<Entry> = ArrayList()
        for (i in commitList.indices){
            entries.add(Entry(i.toFloat(),commitList[i].commit.toFloat()))
        }
        val lineDataSet = LineDataSet(entries,"entries")
        lineDataSet.run {
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
        val data = LineData(lineDataSet)
        lineChart.data = data
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    fun addXAisle(dateList : List<MyThirtyCommits>) : ArrayList<String>{
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

    fun scrollJobCreate(){
        job = lifecycleScope.launchWhenResumed {
            delay(4000)
            pagerRecyclerView.setCurrentItemWithDuration(++bannerPosition,500)
        }
    }

    fun ViewPager2.setCurrentItemWithDuration(
        item : Int, duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = pagerRecyclerView.width
    ){
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0

        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) { beginFakeDrag() }
            override fun onAnimationEnd(animation: Animator?) { endFakeDrag() }
            override fun onAnimationCancel(animation: Animator?) { /* Ignored */ }
            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */ }
        })

        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        scrollJobCreate()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }


    suspend fun getMainData(){

        withContext(CoroutineScope(Dispatchers.Main).coroutineContext + handler) {
            //호출
            val callMainDataApi = withContext(Dispatchers.IO){
                myDataApi.getMyData(MyApp.prefs.accountToken!!)
            }
            myTodayCommitTextView.text = callMainDataApi.daily_commit.toString()//오늘 커밋
            if (callMainDataApi.friend_avg == -1f){
                friendAvgCommitView.text = "친구추가해주세요"
            }else{
                friendAvgCommitView.text = callMainDataApi.friend_avg.toString()
            }
            //친구 커밋
            areaAvgCommitView.text = callMainDataApi.area_avg.toString()//지역 커밋
            initLineChart(callMainDataApi.thirty_commit)//차트 x축
            setDataToLineChart(callMainDataApi.thirty_commit)//차트 커밋수 조절
            getGrassImg(callMainDataApi.committer) //잔디 불러오기
        }
    }
    
    fun getGrassImg(url : String){
        GlideToVectorYou.init()
            .with(mainActivity)
            .withListener(object : GlideToVectorYouListener{
                override fun onLoadFailed() {
                    Log.d("ff","fail")
                }
                override fun onResourceReady() {
                }
            })
            .load("https://ghchart.rshah.org/219138/$url".toUri(),commitGrassImgView)
    }

    fun testGitHubConnection(){
        val errorHandler = CoroutineExceptionHandler{_,exception->
            Log.d("error",exception.toString())
            Log.d("error","문제발생 로그아웃")
            CoroutineScope(Dispatchers.Main).launch(handler) {
                MyApp.prefs.accountToken = null
                MyApp.prefs.githubToken = null
                val dialog : AlertDialog.Builder = AlertDialog.Builder(mainActivity)
                dialog.setTitle("토큰에러")
                    .setMessage("깃허브 토큰에 문제가 있어 로그인 화면으로 돌아갑니다. 다시로그인 해주세요.")
                    .setPositiveButton("확인"){_,_->
                        //로그인 창으로 이동
                        val intent = Intent(mainActivity,LoginActivity::class.java)
                        mainActivity.startActivity(intent)
                        mainActivity.finish()
                    }.show()
            }
        }
        val requestCommitApi= GithubClient.getApi().create(GithubConnectionCheck::class.java)

        CoroutineScope(Dispatchers.IO).launch(errorHandler) {
            val gitHubCommitDatas= withContext(Dispatchers.IO) {
                requestCommitApi.checkGithubConnection(
                    MyApp.prefs.githubToken!!,
                    "want8607"
                )
            }
            Log.d("성공",gitHubCommitDatas.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("bannerPosition",bannerPosition)
    }
}