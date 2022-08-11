package com.example.withub.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.withub.data.network.MyData

class HomeViewModel : ViewModel() {

    companion object {
        const val BannerNum = 4
    }

    private val homeRepository = HomeRepository()
    private val _myHomeData = homeRepository.myHomeData
    private val _bannerPosition = MutableLiveData<Int>()

    val myHomeData: LiveData<MyData>
        get() = _myHomeData
    val bannerPosition: LiveData<Int>
        get() = _bannerPosition

    fun setBannerPosition(position: Int) {
        _bannerPosition.value = position
        Log.d("positionSetting", bannerPosition.value.toString())
    }

    fun callMyHomeData() = homeRepository.callMyDataApi()
}