package com.example.withub.feature.ranking

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.withub.feature.ranking.RankingAreaFragment
import com.example.withub.feature.ranking.RankingFriendFragment


class RankingPagerViewAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    var fragments : ArrayList<Fragment> = arrayListOf(
        RankingFriendFragment(),
        RankingAreaFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
