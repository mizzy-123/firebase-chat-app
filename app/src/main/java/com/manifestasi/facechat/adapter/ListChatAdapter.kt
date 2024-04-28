package com.manifestasi.facechat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manifestasi.facechat.databinding.ItemListChatBinding
import com.manifestasi.facechat.firebase.data.DataChat
import com.manifestasi.facechat.firebase.helper.QueryFirebase

class ListChatAdapter(private val chatList: ArrayList<DataChat>): RecyclerView.Adapter<ListChatAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    internal fun setOnClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataChat, position: Int)
    }
    
    inner class ListViewHolder(var binding: ItemListChatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dataChatList = chatList[position]
        holder.binding.username.text = dataChatList.id
        QueryFirebase.getLastChatUser(dataChatList.id){ message, user ->
            if (user == "mobile"){
                holder.binding.chatPreview.text = "Anda: $message"
            } else {
                holder.binding.chatPreview.text = message
            }
        }
        if (dataChatList.status == 1){
            holder.binding.readable.visibility = View.VISIBLE
        } else {
            holder.binding.readable.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(dataChatList, position) }
    }

}