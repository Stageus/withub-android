package com.example.withub.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.withub.MainActivity
import com.example.withub.R
import com.example.withub.mainFragments.mainFragmentAdapters.RankingPagerViewAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RankingFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var rankingTabLayout: TabLayout
    lateinit var rankingViewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.ranking_fragment,container,false)
        mainActivity = activity as MainActivity
        rankingTabLayout = view.findViewById(R.id.ranking_tapLayout)
        rankingViewPager = view.findViewById(R.id.ranking_view_pager)
        //팁 뷰페이저 어댑터 생성

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rankingPagerViewAdapter = RankingPagerViewAdapter(mainActivity)

        rankingViewPager.adapter = rankingPagerViewAdapter

        val tabTitles : ArrayList<String> = arrayListOf("친구 랭킹","지역 랭킹")
        TabLayoutMediator(rankingTabLayout,rankingViewPager){tab,position ->
            tab.text = tabTitles[position]
        }.attach()

    }
}