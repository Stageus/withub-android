package com.example.withub.mainActivityAdapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.R
import com.example.withub.Repositories

class FriendRepoRVAdapter(var context: Context,var repoList: List<Repositories>) : RecyclerView.Adapter<FriendRepoRVAdapter.Holder>(){

    inner class Holder(itemView: View?):RecyclerView.ViewHolder(itemView!!){

        val repositoryItem = itemView?.findViewById<LinearLayout>(R.id.friend_activity_repository_linear_view)
        val repositoryTextView = itemView?.findViewById<TextView>(R.id.friend_activity_repository_textview)
        fun bind(position: Int){
            repositoryTextView!!.text = "${repoList[position].owner}/${repoList[position].name}"
            repositoryItem!!.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/${repoList[position].owner}/${repoList[position].name}"))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_friend, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    override fun onBindViewHolder(holder: FriendRepoRVAdapter.Holder, position: Int) {
        holder.bind(position)
    }
}