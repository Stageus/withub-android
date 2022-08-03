package com.example.withub.mainFragments.mainFragmentAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.R
import com.example.withub.UserRepoData
import java.security.AccessController.getContext

class SignupRVAdapter (private val items: ArrayList<UserRepoData>, private val view: View) : RecyclerView.Adapter<SignupRVAdapter.Holder>() {

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var num = itemView?.findViewById<TextView>(R.id.name_repository_num)
        var nameRepository = itemView?.findViewById<TextView>(R.id.name_repository)
        var deleteBtn = itemView?.findViewById<Button>(R.id.btnDelete)
        var signupBtn = view.findViewById<Button>(R.id.signup_btn_github_repository_add)
        fun bind(position: Int) {
            num?.text = "${position + 1}"
            nameRepository?.text = "${items[position].owner} / ${items[position].name}"
            if (items.size >= 1) {
                signupBtn?.setBackgroundResource(R.drawable.login_btn)
                signupBtn?.isEnabled = true
            }
            deleteBtn?.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                if (items.size == 0) {
                    signupBtn?.setBackgroundResource(R.drawable.disabled_button)
                    signupBtn?.isEnabled = false
                }
            }
        }
    }

    fun addItem(item: UserRepoData) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun deleteAllItem() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.signup_repository_recyclerview, parent, false)
        return Holder(inflatedView)
    }

    override fun onBindViewHolder(holder: SignupRVAdapter.Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size


}