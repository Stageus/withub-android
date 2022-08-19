package com.example.withub.feature.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.withub.data.network.AreaRankData
import com.example.withub.data.network.RankData
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingAreaViewModel : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("RankingAreaViewModelError", exception.toString())
    }

    private val rankingRepository = RankingRepository()

    private var _areaRankData = MutableLiveData<MutableList<ArrayList<RankData>>>()
    val areaRankData: LiveData<MutableList<ArrayList<RankData>>>
        get() = _areaRankData

    fun setListOfAreaRankData() {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            val unRefinedAreaRankData: AreaRankData = rankingRepository.callAreaRankDataApi()
            withContext(Dispatchers.Default) {
                val newDailyRankData = ArrayList<RankData>()
                val newWeeklyRankData = ArrayList<RankData>()
                val newMonthlyRankData = ArrayList<RankData>()
                val newContinuousRankData = ArrayList<RankData>()
                for (i in 0..9) {
                    if (i < unRefinedAreaRankData.daily_rank.size) {
                        newDailyRankData.add(unRefinedAreaRankData.daily_rank[i])
                        newWeeklyRankData.add(unRefinedAreaRankData.weekly_rank[i])
                        newMonthlyRankData.add(unRefinedAreaRankData.monthly_rank[i])
                        newContinuousRankData.add(unRefinedAreaRankData.continuous_rank[i])
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
                    _areaRankData.value = rankingDataList
                    Logger.d("랭킹정보 호출됨")
                }
            }
        }
    }
}