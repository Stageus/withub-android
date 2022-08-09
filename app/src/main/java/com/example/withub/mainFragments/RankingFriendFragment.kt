package com.example.withub.mainFragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.withub.*
import com.example.withub.databinding.FragmentRankingFriendBinding
import com.example.withub.mainFragments.mainFragmentAdapters.ExpandableRVAdapter
import kotlinx.coroutines.*

class RankingFriendFragment : Fragment() {
    private lateinit var binding : FragmentRankingFriendBinding
    lateinit var mainActivity: MainActivity

    private val commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    private val handler = CoroutineExceptionHandler{ _, exception->
        Log.d("error",exception.toString())
        Log.d("error",exception.cause.toString())
    }
    private val rankingDataList : MutableList<ArrayList<RankData>> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_ranking_friend,container,false)
        val view: View = binding.root
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expandableAdapter = ExpandableRVAdapter(rankingDataList)
        //랭킹 리사이클러 설정
        CoroutineScope(Dispatchers.Main).launch(handler) {
            getRankingData()
            binding.rankingFriendRecyclerView.apply {
                adapter = expandableAdapter
                setHasFixedSize(true)
            }
        }

        //스와이프 리프레시 설정
        binding.rankingFriendSwipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                getRankingData()
                expandableAdapter.refresh(rankingDataList)
                binding.rankingFriendSwipeRefreshLayout.isRefreshing = false
                Toast.makeText(mainActivity,"업데이트 완료",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //랭킹데이터 가져오기
    private suspend fun getRankingData(){
        withContext(CoroutineScope(Dispatchers.Main).coroutineContext + handler) {
            val getFriendRanking = withContext(Dispatchers.IO) {
                commitApi.getFriendRank(MyApp.prefs.accountToken!!)
            }
            val newDailyRankData = ArrayList<RankData>()
            val newWeeklyRankData = ArrayList<RankData>()
            val newMonthlyRankData = ArrayList<RankData>()
            val newContinuousRankData = ArrayList<RankData>()
            withContext(Dispatchers.Default) {
                for (i in 0..9) {
                    if (i < getFriendRanking.daily_rank.size) {
                        newDailyRankData.add(getFriendRanking.daily_rank[i])
                        newWeeklyRankData.add(getFriendRanking.weekly_rank[i])
                        newMonthlyRankData.add(getFriendRanking.monthly_rank[i])
                        newContinuousRankData.add(getFriendRanking.continuous_rank[i])
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
}