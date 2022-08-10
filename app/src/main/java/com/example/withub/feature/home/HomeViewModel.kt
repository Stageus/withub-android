package com.example.withub.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.withub.data.network.MyData

class HomeViewModel : ViewModel() {

    private val homeRepository = HomeRepository()
    private val _myHomeData = homeRepository.myHomeData
    private var _bannerPosition = MutableLiveData<Int>()

    val myHomeData: LiveData<MyData>
        get() = _myHomeData
    val bannerPosition: LiveData<Int>
        get() = _bannerPosition

    fun setBannerPosition(position: Int) {
        _bannerPosition.value = position
    }

    fun callMyHomeData() = homeRepository.callMyDataApi()
}