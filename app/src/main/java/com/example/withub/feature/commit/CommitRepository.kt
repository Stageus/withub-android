package com.example.withub.feature.commit

import com.example.withub.data.network.CommitApi
import com.example.withub.data.network.CommitData
import com.example.withub.data.network.RetrofitClient
import com.example.withub.feature.base.MyApp

class CommitRepository {
    private var commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)

    suspend fun callCommitApi(): List<CommitData> =
        commitApi.getMyCommitList(MyApp.prefs.accountToken!!).commits
}