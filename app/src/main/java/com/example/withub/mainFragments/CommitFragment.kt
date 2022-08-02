package com.example.withub.mainFragments

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
import com.example.withub.databinding.FragmentCommitBinding
import com.example.withub.mainFragments.mainFragmentAdapters.CommitRVAdapter
import kotlinx.coroutines.*

class CommitFragment : Fragment() {

    private var _binding : FragmentCommitBinding? = null
    private val binding get() = _binding!!

    private var commitApi: CommitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    lateinit var mainActivity : MainActivity
    lateinit var adapter : CommitRVAdapter
    private val handler = CoroutineExceptionHandler{ _, exception->
        Log.d("error",exception.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommitBinding.inflate(inflater,container,false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}