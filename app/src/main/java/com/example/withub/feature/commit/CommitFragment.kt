package com.example.withub.feature.commit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.withub.*
import com.example.withub.data.network.CommitApi
import com.example.withub.data.network.CommitData
import com.example.withub.data.network.RetrofitClient
import com.example.withub.databinding.FragmentCommitBinding
import com.example.withub.feature.base.MainActivity
import com.example.withub.feature.base.MyApp
import kotlinx.coroutines.*

class CommitFragment : Fragment() {

    private lateinit var binding : FragmentCommitBinding

    private var commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    lateinit var mainActivity : MainActivity
    lateinit var adapter : CommitRVAdapter
    private val handler = CoroutineExceptionHandler{ _, exception->
        Log.d("error",exception.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_commit,container,false)
        val view  = binding.root
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var list: List<CommitData>
        //커밋 리사이클러뷰 생성
        CoroutineScope(Dispatchers.Main).launch(handler){
            list = withContext(Dispatchers.IO) {
                commitApi.getMyCommitList(MyApp.prefs.accountToken!!).commits
            }
            binding.commitRecyclerView.apply {
                adapter = CommitRVAdapter(mainActivity,list)
                setHasFixedSize(true)
            }
        }
        //리프레시 구현
        binding.commitSwipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                list = withContext(Dispatchers.IO) {
                    commitApi.getMyCommitList(MyApp.prefs.accountToken!!).commits
                }
                adapter.updateDataset(list)
                Log.d("eee", list.toString())
            }
            binding.commitSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(mainActivity, "업데이트 완료", Toast.LENGTH_SHORT).show()
        }
    }

}