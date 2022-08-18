package com.example.withub.feature.ranking


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.R
import com.example.withub.data.network.RankData
import com.example.withub.databinding.FragmentRankingFriendBinding

class RankingFriendFragment : Fragment() {
    private lateinit var binding: FragmentRankingFriendBinding
    private lateinit var rankingFriendViewModel: RankingFriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ranking_friend, container, false)
        val view: View = binding.root
        rankingFriendViewModel = ViewModelProvider(this)[RankingFriendViewModel::class.java]
        binding.apply {
            viewModel = rankingFriendViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //데이터 호출
        rankingFriendViewModel.callFriendRankDataApi()

        //랭킹 리사이클러 설정
//        val expandableAdapter =
//
//            binding.rankingFriendRecyclerView.apply {
//                adapter = expandableAdapter
//                setHasFixedSize(true)
//            }

        //스와이프 리프레시 설정
        binding.rankingFriendSwipeRefreshLayout.setOnRefreshListener {
            rankingFriendViewModel.callFriendRankDataApi()
            binding.rankingFriendSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "업데이트 완료", Toast.LENGTH_SHORT).show()
        }

//        rankingFriendViewModel.friendRankData.observe(viewLifecycleOwner) {
//            if (it != null) {
//                expandableAdapter.setItems(it)
//            }
//        }
    }
}