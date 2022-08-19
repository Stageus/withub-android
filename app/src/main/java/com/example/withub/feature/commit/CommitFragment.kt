package com.example.withub.feature.commit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.withub.R
import com.example.withub.databinding.FragmentCommitBinding

class CommitFragment : Fragment() {

    private lateinit var binding: FragmentCommitBinding
    private val commitViewModel: CommitViewModel by viewModels()
    lateinit var adapter: CommitRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_commit, container, false)
        binding.apply {
            viewModel = commitViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 데이터 호출
        commitViewModel.setListOfCommitData()

        //리프레시 구현
        binding.commitSwipeRefreshLayout.setOnRefreshListener {
            commitViewModel.setListOfCommitData()
            binding.commitSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "업데이트 완료", Toast.LENGTH_SHORT).show()
            Log.d("커밋값", commitViewModel.commitList.value.toString())
        }
    }

}