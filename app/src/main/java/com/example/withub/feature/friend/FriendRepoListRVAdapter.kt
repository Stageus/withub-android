package com.example.withub.feature.friend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.data.network.Repositories
import com.example.withub.databinding.RecyclerViewItemFriendRepoListBinding

class FriendRepoListRVAdapter(val context: Context,private var repoList: List<Repositories>)
    : RecyclerView.Adapter<FriendRepoListRVAdapter.FriendRepoListHolder>(){

    inner class FriendRepoListHolder(val binding: RecyclerViewItemFriendRepoListBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRepoListHolder {
        val binding = RecyclerViewItemFriendRepoListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FriendRepoListHolder(binding)
    }

    override fun getItemCount(): Int = repoList.size

    override fun onBindViewHolder(holder: FriendRepoListHolder, position: Int) {
        holder.apply {
            repoList[position].apply {
                binding.friendActivityRepositoryTextview.text = "${this.owner}/${this.name}"
                binding.friendActivityRepositoryLinearView.setOnClickListener {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/${this.owner}/${this.name}"))
                    context.startActivity(intent)
                }
            }
        }
    }
}