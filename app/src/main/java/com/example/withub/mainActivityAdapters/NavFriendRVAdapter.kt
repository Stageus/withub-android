package com.example.withub.mainActivityAdapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.example.withub.*
import kotlinx.coroutines.*

class NavFriendRVAdapter(val context : Context, val items : MutableList<FriendName>, val myData : MyNickNameData) : RecyclerSwipeAdapter<NavFriendRVAdapter.Holder>(){

    val friendApi = RetrofitClient.initRetrofit().create(FriendApi::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavFriendRVAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_drawer, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: NavFriendRVAdapter.Holder, position: Int) {

        mItemManger.bindView(holder.itemView,position)
        holder.swipeView?.addSwipeListener(object : SwipeLayout.SwipeListener{
            override fun onStartOpen(layout: SwipeLayout?) {
                mItemManger.closeAllExcept(layout)
            }

            override fun onOpen(layout: SwipeLayout?) {}

            override fun onStartClose(layout: SwipeLayout?) {}

            override fun onClose(layout: SwipeLayout?) {}

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}

        })
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_view
    }

    inner class Holder(itemView: View?):RecyclerView.ViewHolder(itemView!!){

        val deleteBtn = itemView?.findViewById<TextView>(R.id.swipe_delete_button)
        val friendNameTextView = itemView?.findViewById<TextView>(R.id.nav_friend_name)
        val swipeView = itemView?.findViewById<SwipeLayout>(R.id.swipe_view)
        val friendItem = itemView?.findViewById<LinearLayout>(R.id.nav_friend_recycler_item)
        val friendImg = itemView?.findViewById<ImageView>(R.id.nav_friend_img)
        fun bind(position: Int){
            //닉네임
            friendNameTextView?.text = items[position].nickname
            //이미지
            Glide.with(context)
                .load(items[position].avatar_url.toUri())
                .into(friendImg!!)
            //친구페이지 전환
            friendItem?.setOnClickListener {
                val intent = Intent(context,FriendActivity::class.java)
                intent.putExtra("friendNickName",items[position].nickname)
                intent.putExtra("myNickName",myData.nickname)
                intent.putExtra("myAvatar",myData.avatar_url)
                intent.putExtra("friendAvatar",items[position].avatar_url)
                context.startActivity(intent)
            }
            //삭제
            deleteBtn?.setOnClickListener {
                val name = items[position].nickname
                val dialog = AlertDialog.Builder(context)
                dialog.setMessage(name+"님을 친구목록에서 삭제하시겠습니까?")
                    .setPositiveButton("삭제"){ _, _ ->
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                friendApi.deleteFriend(FriendNameData(MyApp.prefs.accountToken!!, name))
                            }
                            items.removeAt(position)
                            notifyItemRemoved(position)
                            closeSwipeView()
                        }
                    }
                    .setNegativeButton("취소"){ _, _ ->  }
                    .show()
            }
        }

    }

    fun addItem(item : FriendName,position: Int){
        items.add(item)
        notifyItemInserted(position)
    }

    fun closeSwipeView(){
        mItemManger.closeAllItems()
    }

}
