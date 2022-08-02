package com.example.withub.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.withub.*
import com.example.withub.databinding.FragmentRankingAreaBinding
import com.example.withub.mainFragments.mainFragmentAdapters.ExpandableRVAdapter
import kotlinx.coroutines.*

class RankingAreaFragment : Fragment() {

    private var _binding : FragmentRankingAreaBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    private val commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    private val handler = CoroutineExceptionHandler{ _, exception->
        Log.d("error",exception.toString())
        Log.d("error",exception.cause.toString())
    }
    private var rankingDataList : MutableList<ArrayList<RankData>> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingAreaBinding.inflate(inflater,container,false)
        val view: View = binding.root
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //랭킹 리사이클러 설정
        val expandableAdapter = ExpandableRVAdapter(rankingDataList)
        CoroutineScope(Dispatchers.Main).launch(handler) {
            getRankingData()
            binding.rankingAreaRecyclerView.apply {
                adapter = expandableAdapter
                setHasFixedSize(true)
            }
        }
        
        //스와이프 리프레시 설정
        binding.rankingAreaSwipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                getRankingData()
                expandableAdapter.refresh(rankingDataList)
                binding.rankingAreaSwipeRefreshLayout.isRefreshing = false
                Toast.makeText(mainActivity,"업데이트 완료",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //랭킹데이터 가져오기
    private suspend fun getRankingData(){
        withContext(CoroutineScope(Dispatchers.Default).coroutineContext + handler) {
            val getAreaRanking = withContext(Dispatchers.IO) {
                commitApi.getAreaRank(MyApp.prefs.accountToken!!)
            }
            val newDailyRankData = ArrayList<RankData>()
            val newWeeklyRankData = ArrayList<RankData>()
            val newMonthlyRankData = ArrayList<RankData>()
            val newContinuousRankData = ArrayList<RankData>()
            withContext(Dispatchers.Default) {
                for (i in 0..9) {
                    if (i < getAreaRanking.daily_rank.size) {
                        newDailyRankData.add(getAreaRanking.daily_rank[i])
                        newWeeklyRankData.add(getAreaRanking.weekly_rank[i])
                        newMonthlyRankData.add(getAreaRanking.monthly_rank[i])
                        newContinuousRankData.add(getAreaRanking.continuous_rank[i])
                    } else {
                        val emptyRankData = RankData("-", -1)
                        newDailyRankData.add(emptyRankData)
                        newWeeklyRankData.add(emptyRankData)
                        newMonthlyRankData.add(emptyRankData)
                        newContinuousRankData.add(emptyRankData)
                    }
                }
            }
            rankingDataList.clear()
            rankingDataList.add(newDailyRankData)
            rankingDataList.add(newWeeklyRankData)
            rankingDataList.add(newMonthlyRankData)
            rankingDataList.add(newContinuousRankData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}