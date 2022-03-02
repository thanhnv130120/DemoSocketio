package com.example.demosocketio.ui.screen.chat

import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.demosocketio.R
import com.example.demosocketio.data.base.BaseFragment
import com.example.demosocketio.data.model.Message
import com.example.demosocketio.data.model.Room
import com.example.demosocketio.data.model.SendMessage
import com.example.demosocketio.databinding.FragmentChatBinding
import com.example.demosocketio.ui.screen.chat.adapter.ChatAdapter
import com.example.demosocketio.utils.App
import com.example.demosocketio.utils.Constants
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener {

    private var mSocket: Socket? = null
    private var app: App? = null
    private var mChatAdapter: ChatAdapter? = null
    private val args: ChatFragmentArgs by navArgs()
    private var listMessage = mutableListOf<Message>()
    private var gson = Gson()

    override fun getLayoutID() = R.layout.fragment_chat

    override fun initViewModel() {

    }

    override fun initView() {
        app = requireActivity().application as App?

        binding!!.btnSend.setOnClickListener(this)

        mChatAdapter = ChatAdapter(listMessage)
        binding!!.rcChat.adapter = mChatAdapter

        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on("newUserToChatRoom", onNewUser)
        mSocket!!.on("updateChat", onUpdateChat)
        mSocket!!.on("userLeftChatRoom", onUserLeft)
        mSocket!!.connect()
    }

    var onUserLeft = Emitter.Listener {
        val leftUserName = it[0] as String
        val chat = Message(leftUserName, "", "", 3)
        addItemToRecyclerView(chat)
    }

    var onUpdateChat = Emitter.Listener {
        val chat: Message = gson.fromJson(it[0].toString(), Message::class.java)
        chat.type = 1
        addItemToRecyclerView(chat)
    }

    var onConnect = Emitter.Listener {
        val data = Room(args.userName, args.roomName)
        val jsonData = gson.toJson(data)
        mSocket!!.emit("subscribe", jsonData)

    }

    var onNewUser = Emitter.Listener {
        val name = it[0] as String //This pass the userName!
        val chat = Message(name, "", args.roomName, 2)
        addItemToRecyclerView(chat)
    }


    private fun sendMessage() {
        val content = binding!!.edtMsg.text.toString()
        val sendData = SendMessage(args.userName, content, args.roomName)
        val jsonData = gson.toJson(sendData)
        mSocket!!.emit("newMessage", jsonData)

        val chat = Message(args.userName, content, args.roomName, 0)
        addItemToRecyclerView(chat)
    }

    private fun addItemToRecyclerView(message: Message) {
        requireActivity().runOnUiThread {
            listMessage.add(message)
            mChatAdapter!!.notifyItemInserted(listMessage.size)
            binding!!.edtMsg.setText("")
            binding!!.rcChat.scrollToPosition(mChatAdapter!!.listMessage.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val data = Room(args.userName, args.roomName)
        val jsonData = Gson().toJson(data)
        mSocket!!.emit("unsubscribe", jsonData)
        mSocket!!.disconnect()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding!!.btnSend) {
            if (!binding!!.edtMsg.text.isNullOrBlank()) {
                sendMessage()
            }
        }
    }

}