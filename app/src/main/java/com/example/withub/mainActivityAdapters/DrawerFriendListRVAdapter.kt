package com.example.withub.mainActivityAdapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.example.withub.*
import com.example.withub.databinding.RecyclerViewItemDrawerFriendListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawerFriendListRVAdapter(val context : Context, val items : MutableList<FriendName>, val myData : MyNickNameData)
    : RecyclerSwipeAdapter<DrawerFriendListRVAdapter.DrawerFriendListHolder>(){

    private val friendApi: FriendApi = RetrofitClient.initRetrofit().create(FriendApi::class.java)

    inner class DrawerFriendListHolder(val binding: RecyclerViewItemDrawerFriendListBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerFriendListRVAdapter.DrawerFriendListHolder {
        val binding = RecyclerViewItemDrawerFriendListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DrawerFriendListHolder(binding)
    }

    override fun onBindViewHolder(holder: DrawerFriendListRVAdapter.DrawerFriendListHolder, position: Int) {
        holder.apply {
            mItemManger.bindView(this.itemView,position)
            this.binding.swipeView.addSwipeListener(object : SwipeLayout.SwipeListener{
                override fun onStartOpen(layout: SwipeLayout?) {
                    mItemManger.closeAllExcept(layout)
                }

                override fun onOpen(layout: SwipeLayout?) {}

                override fun onStartClose(layout: SwipeLayout?) {}

                override fun onClose(layout: SwipeLayout?) {}

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}

            })
            items[position].apply {
                //닉네임
                binding.navFriendName.text = this.nickname
                //이미지
                Glide.with(context)
                    .load(items[position].avatar_url.toUri())
                    .into(binding.navFriendImg)
                //친구페이지 전환
                binding.swipeView.setOnClickListener {
                    val intent = Intent(context,FriendActivity::class.java)
                    intent.putExtra("friendNickName",this.nickname)
                    intent.putExtra("myNickName",myData.nickname)
                    intent.putExtra("myAvatar",myData.avatar_url)
                    intent.putExtra("friendAvatar",this.avatar_url)
                    context.startActivity(intent)
                }
                //삭제
                binding.swipeDeleteButton.setOnClickListener {
                    val name = this.nickname
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
    }

    override fun getItemCount(): Int = items.size

    override fun getSwipeLayoutResourceId(position: Int): Int = R.id.swipe_view

    fun addItem(item : FriendName,position: Int){
        items.add(item)
        notifyItemInserted(position)
    }

    fun closeSwipeView(){
        mItemManger.closeAllItems()
    }

}
