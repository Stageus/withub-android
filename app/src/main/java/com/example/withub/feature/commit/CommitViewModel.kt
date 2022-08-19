package com.example.withub.feature.commit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.withub.data.network.Commit
import com.example.withub.data.network.CommitData
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommitViewModel : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, exception ->
        Logger.d(exception.toString())
    }

    private val commitRepository = CommitRepository()

    private var _commitList = MutableLiveData<List<CommitData>>()
    val commitList: LiveData<List<CommitData>>
        get() = _commitList

    fun setListOfCommitData() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val newCommitList = commitRepository.callCommitApi()
            withContext(Dispatchers.Main) {
                _commitList.value = newCommitList
                Logger.d(commitList.value)
            }
        }
    }

}