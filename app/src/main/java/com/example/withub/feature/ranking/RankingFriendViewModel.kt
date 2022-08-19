package com.example.withub.feature.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.withub.data.network.FriendRankData
import com.example.withub.data.network.RankData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingFriendViewModel : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("RankingFriendViewModelError", exception.toString())
    }

    private val rankingRepository = RankingRepository()

    private var _friendRankData = MutableLiveData<MutableList<ArrayList<RankData>>>()
    val friendRankData: LiveData<MutableList<ArrayList<RankData>>>
        get() = _friendRankData

    fun setListOfFriendRankData() {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            val unRefinedFriendRankData: FriendRankData =
                rankingRepository.callFriendRankDataApi()
            withContext(Dispatchers.Default) {
                val newDailyRankData = ArrayList<RankData>()
                val newWeeklyRankData = ArrayList<RankData>()
                val newMonthlyRankData = ArrayList<RankData>()
                val newContinuousRankData = ArrayList<RankData>()
                for (i in 0..9) {
                    if (i < unRefinedFriendRankData.daily_rank.size) {
                        newDailyRankData.add(unRefinedFriendRankData.daily_rank[i])
                        newWeeklyRankData.add(unRefinedFriendRankData.weekly_rank[i])
                        newMonthlyRankData.add(unRefinedFriendRankData.monthly_rank[i])
                        newContinuousRankData.add(unRefinedFriendRankData.continuous_rank[i])
                    } else {
                        val emptyRankData = RankData("-", -1)
                        newDailyRankData.add(emptyRankData)
                        newWeeklyRankData.add(emptyRankData)
                        newMonthlyRankData.add(emptyRankData)
                        newContinuousRankData.add(emptyRankData)
                    }
                }
                val rankingDataList: MutableList<ArrayList<RankData>> = mutableListOf()
                rankingDataList.add(newDailyRankData)
                rankingDataList.add(newWeeklyRankData)
                rankingDataList.add(newMonthlyRankData)
                rankingDataList.add(newContinuousRankData)
                withContext(Dispatchers.Main) {
                    _friendRankData.value = rankingDataList
                }
            }
        }
    }
}