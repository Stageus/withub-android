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
import com.example.withub.databinding.FragmentRankingAreaBinding

class RankingAreaFragment : Fragment() {

    private lateinit var binding: FragmentRankingAreaBinding
    private lateinit var rankingAreaViewModel: RankingAreaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ranking_area, container, false)
        val view: View = binding.root
        rankingAreaViewModel = ViewModelProvider(this)[RankingAreaViewModel::class.java]
        binding.apply {
            viewModel = rankingAreaViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //데이터 호출
        rankingAreaViewModel.callAreaRankDataApi()

//        //랭킹 리사이클러 설정
//        val expandableAdapter = ExpandableRVAdapter()

//        binding.rankingAreaRecyclerView.apply {
//            adapter = expandableAdapter
//            setHasFixedSize(true) //레이아웃이 새로 생성되어도 고정된 크기를 가질것이라고 지정해줌으로써 작업비용을 감소시킴
//        }

        //스와이프 리프레시 설정
        binding.rankingAreaSwipeRefreshLayout.setOnRefreshListener {
            rankingAreaViewModel.callAreaRankDataApi()
            binding.rankingAreaSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "업데이트 완료", Toast.LENGTH_SHORT).show()
        }

//        rankingAreaViewModel.areaRankData.observe(viewLifecycleOwner) {
//            if (it != null) {
//                expandableAdapter.setItems(it)
//            }
//        }
    }
}