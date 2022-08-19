package com.example.withub.feature.commit

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.withub.data.network.CommitData

@BindingAdapter("setCommitItems")
fun RecyclerView.setCommitItems(items: List<CommitData>?) {
    val adapter: CommitRVAdapter
    if (this.adapter == null) {
        adapter = CommitRVAdapter()
        this.adapter = adapter
    } else {
        adapter = this.adapter as CommitRVAdapter
    }
    if (items != null) {
        adapter.setItems(items)
    }
    this.setHasFixedSize(true)
}
