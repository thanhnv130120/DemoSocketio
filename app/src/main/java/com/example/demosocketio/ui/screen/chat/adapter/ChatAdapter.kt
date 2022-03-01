package com.example.demosocketio.ui.screen.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demosocketio.data.model.Message
import com.example.demosocketio.databinding.ItemRcChatBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var listMessage = mutableListOf<Message>()

    fun setItem(list: MutableList<Message>) {
        listMessage.clear()
        listMessage.addAll(list)
    }

    fun addItem(message: Message) {
        listMessage.add(message)
        notifyItemInserted(listMessage.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val binding =
            ItemRcChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = listMessage.size

    inner class ChatViewHolder(val binding: ItemRcChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.messageModel = listMessage[position]
        }
    }
}