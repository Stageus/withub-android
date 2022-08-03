package com.example.withub.mainFragments.mainFragmentAdapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.withub.databinding.RecyclerViewItmeHomeTipBinding

class HomePagerRecyclerAdapter(val context: Context, private val requestManager: RequestManager,
                               private val imgList : ArrayList<Int>, private val urlList : ArrayList<String>) :
    RecyclerView.Adapter<HomePagerRecyclerAdapter.HomePagerViewHolder>() {

    inner class HomePagerViewHolder(val binding : RecyclerViewItmeHomeTipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePagerViewHolder {
        val binding = RecyclerViewItmeHomeTipBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomePagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomePagerViewHolder, position: Int) {
        holder.binding.tipView.apply {
            requestManager
                .load(imgList[position])
                .into(this)
        }.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse(urlList[position]))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}