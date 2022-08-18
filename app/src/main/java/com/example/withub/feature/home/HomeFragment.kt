package com.example.withub.feature.home

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.withub.R
import com.example.withub.data.network.MyThirtyCommits
import com.example.withub.databinding.FragmentHomeBinding
import com.example.withub.feature.drawer.DrawerActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HomeViewModel::class.java]
        binding.apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.callHomeDataApi()

        val imgList = listOf(
            R.drawable.view_pager1,
            R.drawable.view_pager2,
            R.drawable.view_pager3,
            R.drawable.view_pager4
        )

        val urlList = listOf(
            "https://www.youtube.com/watch?v=NOVDVW4dask",
            "https://www.youtube.com/watch?v=shZtNaSV4Tk",
            "https://www.youtube.com/watch?v=kp4CEADyTFs",
            "https://www.youtube.com/watch?v=Ru_bHWAqdSM"
        )
        //배너 이미지와 URL 뷰모델에 삽입
        initBanner(imgList, urlList)

        //Drawer 버튼 클릭 시 DrawerActivity 전환
        binding.navButton.setOnClickListener {
            val intent = Intent(requireActivity(), DrawerActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.lefttoright_animation, R.anim.hold)
        }

        //30일 커밋 차트
        binding.homeHorizontalScroll.post {
            binding.homeHorizontalScroll.scrollTo(
                binding.lineChart.width,
                0
            )
        }

        //swipeRefreshLayout
        binding.homeSwipeToRefresh.setOnRefreshListener {
            homeViewModel.callHomeDataApi()
            binding.homeSwipeToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), "업데이트 완료", Toast.LENGTH_SHORT).show()
        }

        homeViewModel.homeData.observe(viewLifecycleOwner) {
            if (it != null) {
                initLineChart(it.thirty_commit)//차트 x축
                setDataToLineChart(it.thirty_commit)//차트 커밋수 조절
                getGrassImg(it.committer) //잔디 불러오기
            }
        }
    }

    private fun initBanner(imgList: List<Int>, urlList: List<String>) {
        //배너 설정
        binding.mainViewPager.apply {
            adapter = HomePagerRecyclerAdapter(
                imgList,
                urlList
            )
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setCurrentItem(homeViewModel.bannerPosition.value!!, false)
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    homeViewModel.bannerPosition.value = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 배너에서 손 떼었을때 / 배너 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            if (!job.isActive) {
                                bannerAutoScroll()
                            }
                        }
                        // 배너 손으로 드래그 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            job.cancel()
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {}
                    }
                }
            })
        }
    }

    private fun initLineChart(dateList: List<MyThirtyCommits>) {
        binding.lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisLeft.setDrawGridLines(false)
            description.isEnabled = false
            isDragXEnabled = true
            isScaleYEnabled = false
            isScaleXEnabled = false
        }
        val xAxis: XAxis = binding.lineChart.xAxis
        // to draw label on xAxis
        xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(true)
            setDrawLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisCustomFormatter(addXAisle(dateList))
            textColor = resources.getColor(R.color.text_color, null)
            textSize = 10f
            labelRotationAngle = 0f
            setLabelCount(30, false)
        }
    }

    inner class XAxisCustomFormatter(private val xAxisData: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }
    }

    private fun setDataToLineChart(commitList: List<MyThirtyCommits>) {

        val entries: ArrayList<Entry> = ArrayList()
        for (i in commitList.indices) {
            entries.add(Entry(i.toFloat(), commitList[i].commit.toFloat()))
        }
        val lineDataSet = LineDataSet(entries, "entries")
        lineDataSet.apply {
            color = resources.getColor(R.color.point_color, null)
            circleRadius = 5f
            lineWidth = 3f
            setCircleColor(resources.getColor(R.color.graph_dot_color, null))
            circleHoleColor = resources.getColor(R.color.graph_dot_color, null)
            setDrawHighlightIndicators(false)
            setDrawValues(true) // 숫자표시
            valueTextColor = resources.getColor(R.color.text_color, null)
            valueFormatter = DefaultValueFormatter(0)
            valueTextSize = 10f
        }
        binding.lineChart.apply {
            data = LineData(lineDataSet)
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun addXAisle(dateList: List<MyThirtyCommits>): ArrayList<String> {
        val dataTextList = ArrayList<String>()
        for (i in dateList.indices) {
            val textSize = dateList[i].date.length
            val dateText = dateList[i].date.substring(textSize - 2, textSize)
            if (dateText == "01") {
                dataTextList.add(dateList[i].date)
            } else {
                dataTextList.add(dateText)
            }
        }
        return dataTextList
    }

    private fun getGrassImg(url: String) {
        Glide.get(requireActivity()).clearMemory()
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        GlideApp.with(requireActivity())
            .load("https://ghchart.rshah.org/219138/$url".toUri())
            .apply(requestOptions)
            .into(binding.mainCommitGrassImgView)
//
//        GlideToVectorYou.init()
//            .with(mainActivity)
//            .withListener(object : GlideToVectorYouListener {
//                override fun onLoadFailed() {
//                    Log.d("ff", "fail")
//                }
//
//                override fun onResourceReady() {
//                }
//            })
//            .load(, binding.mainCommitGrassImgView)
    }

    /** 설계 : 배너 자동스크롤이 Main스레드를 중단시켜서는 안되기 때문에 코루틴을 사용
     *        사용자와 상호작용 할 수 있는 상태가 Resume이기 때문에 launchWhenResumed을 사용
     *        프래그먼트가 멈출때 onPause에서 job을 취소해준다.
     *
     * 기능 : setCurrentItemWithDuration 확장함수에 매 4초마다 현재 배너 위치에 1을 더한 위치를 보내줌
     *       매 0.5초마다 자동 스크롤됨*/
    private fun bannerAutoScroll() {
        job = lifecycleScope.launchWhenResumed {
            delay(4000)
            homeViewModel.bannerPosition.value?.let {
                binding.mainViewPager.setCurrentItemWithDuration(
                    it.plus(1),
                    500
                )
            }
        }
    }

    private fun ViewPager2.setCurrentItemWithDuration(
        item: Int, duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = binding.mainViewPager.width
    ) {
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
            override fun onAnimationStart(animation: Animator?) {
                beginFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator?) {
                endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator?) { /* Ignored */
            }

            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
            }
        })

        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        bannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }
}