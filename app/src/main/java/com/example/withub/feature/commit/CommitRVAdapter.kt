package com.example.withub.feature.commit

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.data.network.CommitData
import com.example.withub.databinding.RecyclerViewItemCommitBinding

class CommitRVAdapter : RecyclerView.Adapter<CommitRVAdapter.CommitHolder>() {

    private var commitList: List<CommitData> = listOf()

    inner class CommitHolder(val binding: RecyclerViewItemCommitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitHolder {
        val binding = RecyclerViewItemCommitBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CommitHolder(binding)
    }

    override fun onBindViewHolder(holder: CommitHolder, position: Int) {
        holder.apply {
            commitList[position].apply {
                binding.commitDateTextView.text = this.date + " " + this.time
                binding.commitRepositoryName.text = this.repository
                binding.commitMessageTextView.text = this.commit_message
                binding.commitConstraintLayout.setOnClickListener {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/${this.repository}/commit/${this.sha}")
                    )
                    binding.commitConstraintLayout.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = commitList.size

    fun setItems(newList: List<CommitData>) {
        commitList = newList
        notifyDataSetChanged()
    }
}