package com.example.withub.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.withub.data.network.MyData
import com.example.withub.data.network.MyDataApi
import com.example.withub.data.network.RetrofitClient
import com.example.withub.feature.base.MyApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeRepository : ViewModel() {

    private val _myHomeData = MutableLiveData<MyData>()
    val myHomeData: LiveData<MyData>
        get() = _myHomeData

    private var myDataApi: MyDataApi = RetrofitClient.initRetrofit().create(MyDataApi::class.java)

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("error", exception.toString())
    }

    fun callMyDataApi() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            try {
                _myHomeData.postValue(myDataApi.getMyData(MyApp.prefs.accountToken!!))
                Log.d("homeApI호출", myHomeData.value.toString())
            } catch (e: java.lang.Exception) {
                Log.d("homeAPI 에러", e.toString())
                _myHomeData.postValue(null)
            }
        }
    }
}