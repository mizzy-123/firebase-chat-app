package com.manifestasi.facechat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.manifestasi.facechat.R
import com.manifestasi.facechat.firebase.data.DataDetailChat
import com.manifestasi.facechat.helper.TimeStamp

class ListChatDetailAdapter(private val listChatDetail: ArrayList<DataDetailChat>) : RecyclerView.Adapter<ListChatDetailAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textChat: TextView = view.findViewById(R.id.text)
        val textClock: TextView = view.findViewById(R.id.msg_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listChatDetail.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val chatDetail = listChatDetail[position]
        holder.textClock.text = TimeStamp.convertToHour(chatDetail.timestamp)
        holder.textChat.text = chatDetail.message
    }

    override fun getItemViewType(position: Int): Int {
        val user = listChatDetail[position].user
        return if (user == "mobile"){
            R.layout.layout_chat_right
        } else {
            R.layout.layout_chat_left
        }
    }
}