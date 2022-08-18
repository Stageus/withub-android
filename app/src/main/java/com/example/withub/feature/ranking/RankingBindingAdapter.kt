package com.example.withub.feature.ranking

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.data.network.RankData


@BindingAdapter("setItems")
fun RecyclerView.setItems(items: MutableList<ArrayList<RankData>>?) {
    val adapter: ExpandableRVAdapter
    if (this.adapter == null) {
        adapter = ExpandableRVAdapter()
        this.adapter = adapter
    } else {
        adapter = this.adapter as ExpandableRVAdapter
    }
    if (items != null) {
        adapter.setItems(items)
    }
    this.setHasFixedSize(true)
}
