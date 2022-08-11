package com.example.withub.feature.home

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.withub.R
import com.robinhood.ticker.TickerView

@BindingAdapter("setTextInString")
fun TextView.setTextInString(text: Int) {
    this.text = text.toString()
}

@BindingAdapter("setTextInString")
fun TickerView.setTextInString(text: Int) {
    this.text = text.toString()
}

@BindingAdapter("setAreaCommit")
fun TickerView.setAreaCommit(text: Float) {
    this.text = text.toString()
}

@BindingAdapter("setFriendAvgCommit")
fun TickerView.setFriendAvgCommit(avgCommit: Float) {
    this.text =
        if (avgCommit != 1f) avgCommit.toString() else this.context.getString(R.string.add_friend)
}
