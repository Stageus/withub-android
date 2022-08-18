package com.example.withub.feature.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.R
import com.example.withub.data.network.RankData
import com.example.withub.databinding.RecyclerViewItemRankingBinding

class ExpandableRVAdapter:
    RecyclerView.Adapter<ExpandableRVAdapter.ExpandableHolder>() {

    private var rankingDataList: MutableList<ArrayList<RankData>> = mutableListOf()
    private var visibilityList: ArrayList<Boolean> = arrayListOf(false, false, false, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableHolder {
        val binding = RecyclerViewItemRankingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpandableHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpandableHolder, position: Int) {
        val isExpandable: Boolean = visibilityList[position]
        holder.apply {
            binding.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE
            binding.cardViewTitle.text =
                itemView.resources.getStringArray(R.array.ranking_title)[position]
            rankingDataList[position].apply {
                binding.friendName1.text = this[0].nickname
                binding.friendName2.text = this[1].nickname
                binding.friendName3.text = this[2].nickname
                binding.friendName4.text = this[3].nickname
                binding.friendName5.text = this[4].nickname
                binding.friendName6.text = this[5].nickname
                binding.friendName7.text = this[6].nickname
                binding.friendName8.text = this[7].nickname
                binding.friendName9.text = this[8].nickname
                binding.friendName10.text = this[9].nickname
                binding.friendCommitNum1.text = changeNull(this[0].count)
                binding.friendCommitNum2.text = changeNull(this[1].count)
                binding.friendCommitNum3.text = changeNull(this[2].count)
                binding.friendCommitNum4.text = changeNull(this[3].count)
                binding.friendCommitNum5.text = changeNull(this[4].count)
                binding.friendCommitNum6.text = changeNull(this[5].count)
                binding.friendCommitNum7.text = changeNull(this[6].count)
                binding.friendCommitNum8.text = changeNull(this[7].count)
                binding.friendCommitNum9.text = changeNull(this[8].count)
                binding.friendCommitNum10.text = changeNull(this[9].count)
                binding.cardView.setOnClickListener {
                    val viewVisibility = visibilityList[position]
                    visibilityList[position] = !viewVisibility
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = rankingDataList.size

    inner class ExpandableHolder(val binding: RecyclerViewItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setItems(newList: MutableList<ArrayList<RankData>>) {
        rankingDataList.clear()
        rankingDataList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun changeNull(count: Int): String {
        if (count == -1) {
            return ""
        } else (
                return count.toString()
                )
    }
}