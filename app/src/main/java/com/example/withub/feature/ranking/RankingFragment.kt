package com.example.withub.feature.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.withub.feature.base.MainActivity
import com.example.withub.R
import com.example.withub.databinding.FragmentRankingBinding
import com.google.android.material.tabs.TabLayoutMediator

class RankingFragment : Fragment() {
    private lateinit var binding : FragmentRankingBinding

    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_ranking,container,false)
        val view: View = binding.root
        mainActivity = activity as MainActivity

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rankingViewPager.adapter = RankingPagerViewAdapter(mainActivity)
        val tabTitles : ArrayList<String> = arrayListOf("친구 랭킹","지역 랭킹")
        TabLayoutMediator(binding.rankingTapLayout,binding.rankingViewPager){tab,position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}