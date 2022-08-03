package com.example.withub.mainFragments.mainFragmentAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.withub.mainFragments.RankingAreaFragment
import com.example.withub.mainFragments.RankingFriendFragment


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
