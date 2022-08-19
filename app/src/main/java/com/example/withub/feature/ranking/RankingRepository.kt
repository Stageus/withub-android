package com.example.withub.feature.ranking

import com.example.withub.data.network.AreaRankData
import com.example.withub.data.network.CommitApi
import com.example.withub.data.network.FriendRankData
import com.example.withub.data.network.RetrofitClient
import com.example.withub.feature.base.MyApp

class RankingRepository {
    private val commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)

    suspend fun callAreaRankDataApi(): AreaRankData =
        commitApi.getAreaRank(MyApp.prefs.accountToken!!)

    suspend fun callFriendRankDataApi(): FriendRankData =
        commitApi.getFriendRank(MyApp.prefs.accountToken!!)
}