package com.example.withub.feature.ranking


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.withub.R
import com.example.withub.databinding.FragmentRankingFriendBinding

class RankingFriendFragment : Fragment() {
    private lateinit var binding: FragmentRankingFriendBinding
    private val rankingFriendViewModel: RankingFriendViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ranking_friend, container, false)
        val view: View = binding.root

        binding.apply {
            viewModel = rankingFriendViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //데이터 호출
        rankingFriendViewModel.setListOfFriendRankData()

        //스와이프 리프레시 설정
        binding.rankingFriendSwipeRefreshLayout.setOnRefreshListener {
            rankingFriendViewModel.setListOfFriendRankData()
            binding.rankingFriendSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "업데이트 완료", Toast.LENGTH_SHORT).show()
        }
    }
}