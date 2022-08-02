package com.example.withub.mainFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.withub.*
import com.example.withub.mainFragments.mainFragmentAdapters.CommitRVAdapter
import kotlinx.coroutines.*

class CommitFragment : Fragment() {

    var commitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    lateinit var mainActivity : ActivityMainBinding
    lateinit var adapter : CommitRVAdapter
    val handler = CoroutineExceptionHandler{_,exception->
        Log.d("error",exception.toString())
    }
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.commit_fragment,container,false)
        mainActivity = activity as ActivityMainBinding

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var list: List<CommitData>
        //커밋 리사이클러뷰 생성
        CoroutineScope(Dispatchers.Main).launch(handler){
            val recyclerView = view.findViewById<RecyclerView>(R.id.commit_recycler_view)
            list = withContext(Dispatchers.IO) {
                commitApi.getMyCommitList(MyApp.prefs.accountToken!!).commits
            }
            adapter  = CommitRVAdapter(mainActivity,list)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
        }
        //리프레시 구현
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.commit_swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                list = withContext(Dispatchers.IO) {
                    commitApi.getMyCommitList(MyApp.prefs.accountToken!!).commits
                }
                adapter.updateDataset(list)
                Log.d("eee",list.toString())
            }
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(mainActivity,"업데이트 완료", Toast.LENGTH_SHORT).show()
        }
    }
}