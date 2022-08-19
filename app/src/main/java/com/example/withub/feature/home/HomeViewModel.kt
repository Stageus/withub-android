package com.example.withub.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.withub.data.network.MyData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
/**
 * bannerPosition은 _myHomeData와 다르게 View에서의 배너 작동이 ViewModel bannerPosition에 영향을 주기 때문에
 * 외부에서 접근가능하게 하고 myHomeData처럼 get전용 변수를 따로 두지 않았다.
 * 결과적으로 HomeFragment에서 bannerPosition변수에 직접 set, get을 할 수 있다.
 * */
class HomeViewModel : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("error", exception.toString())
    }
    private val homeRepository = HomeRepository()

    companion object {
        const val BannerNum = 4
    }

    private var _homeData = MutableLiveData<MyData>()
    var bannerPosition = MutableLiveData<Int>()

    val homeData: LiveData<MyData>
        get() = _homeData

    init {
        bannerPosition.value = Int.MAX_VALUE / 2
    }

    /**
     *  viewModelScope를 사용하여 프래그먼트 생명주기에 독립적으로 HomeRepository를 통해 API에 접근 가능하다.
     *  그때 API호출은 IO에서 해주고 LiveData인 _myHomeData는 메인스레드에서 변경해준다.
     * */
    fun setMyData() {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            val calledHomeData = homeRepository.callMyDataApi()
            withContext(Dispatchers.Main) {
                _homeData.value = calledHomeData
            }
        }
    }
}