package com.example.withub.mainFragments.mainFragmentAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.CommitData
import com.example.withub.R

class CommitRVAdapter(val context : Context, var list : List<CommitData>) : RecyclerView.Adapter<CommitRVAdapter.Holder>(){

    inner class Holder(itemView: View?):RecyclerView.ViewHolder(itemView!!){

        var dateTextview = itemView?.findViewById<TextView>(R.id.commit_date_text_view)
        var repositoryName = itemView?.findViewById<TextView>(R.id.commit_repository_name)
        var commitMessage = itemView?.findViewById<TextView>(R.id.commit_message_text_view)
        var commitClickArea = itemView?.findViewById<ConstraintLayout>(R.id.commit_constraint_layout)
        fun bind(position: Int){
            dateTextview?.text = list[position].date +" "+ list[position].time
            repositoryName?.text = list[position].repository
            commitMessage?.text = list[position].commit_message
            commitClickArea?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/${list[position].repository}/commit/${list[position].sha}"))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitRVAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.`recycler_view_item_commit.xml`, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataset(newList : List<CommitData>){
        list = newList
        notifyDataSetChanged()
    }
}