package com.example.withub.mainFragments.mainFragmentAdapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.GitHubInfoChangeActivity
import com.example.withub.R
import com.example.withub.Repositories

class GithubInfoChangeRVAdapter (private val items: ArrayList<Repositories>,private val changeBtn:Button) : RecyclerView.Adapter<GithubInfoChangeRVAdapter.Holder>() {
    var firstRepoSize = false
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var num = itemView?.findViewById<TextView>(R.id.name_repository_num)
        var nameRepository = itemView?.findViewById<TextView>(R.id.name_repository)
        var deleteBtn = itemView?.findViewById<Button>(R.id.btnDelete)
        fun bind(position: Int) {
            num?.text = "${position + 1}"
            nameRepository?.text = "${items[position].owner} / ${items[position].name}"

            if (items.size == 0) {
                changeBtn.setBackgroundResource(R.drawable.disabled_button)
                changeBtn.isEnabled = false
            } else if (firstRepoSize) {
                changeBtn.setBackgroundResource(R.drawable.login_btn)
                changeBtn.isEnabled = true
                 if (items.size >= 1) {
                     changeBtn.setBackgroundResource(R.drawable.login_btn)
                     changeBtn.isEnabled = true
                 }
            }

            firstRepoSize = true
            deleteBtn?.setOnClickListener {
                firstRepoSize = true
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                if (items.size == 0) {
                    changeBtn.setBackgroundResource(R.drawable.disabled_button)
                    changeBtn.isEnabled = false
                }
            }
        }
    }

    fun addItem(item: Repositories) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GithubInfoChangeRVAdapter.Holder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.signup_repository_recyclerview, parent, false)
        return Holder(inflatedView)
    }

    override fun onBindViewHolder(holder: GithubInfoChangeRVAdapter.Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

}