package com.example.withub.feature.home

import com.example.withub.data.network.MyData
import com.example.withub.data.network.MyDataApi
import com.example.withub.data.network.RetrofitClient
import com.example.withub.feature.base.MyApp

/**
 * 모듈화를 위해 HomeRepository를 따로 ViewModel에서 분리
 */
class HomeRepository() {
    private val myDataApi: MyDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)

    /**
     * Retrofit을 이용해 서버에서 내 최근 30일 간의 커밋을 가져온다.
     */
    suspend fun callMyDataApi(): MyData {
        return myDataApi.getMyData(MyApp.prefs.accountToken!!)
    }
}