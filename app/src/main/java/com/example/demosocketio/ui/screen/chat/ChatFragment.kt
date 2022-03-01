package com.example.demosocketio.ui.screen.chat

import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.demosocketio.R
import com.example.demosocketio.data.base.BaseFragment
import com.example.demosocketio.data.model.Message
import com.example.demosocketio.data.model.Room
import com.example.demosocketio.databinding.FragmentChatBinding
import com.example.demosocketio.ui.screen.chat.adapter.ChatAdapter
import com.example.demosocketio.utils.App
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter

class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener {

    private var mSocket: Socket? = null
    private var app: App? = null
    private var mChatAdapter: ChatAdapter? = null
    private val args: ChatFragmentArgs by navArgs()

    override fun getLayoutID() = R.layout.fragment_chat

    override fun initViewModel() {

    }

    override fun initView() {
        app = requireActivity().application as App?

        binding!!.btnSend.setOnClickListener(this)

        mChatAdapter = ChatAdapter()
        binding!!.rcChat.adapter = mChatAdapter

        mSocket = app?.mSocket

        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on("newUserToChatRoom", onNewUser)
        mSocket!!.on("updateChat", onUpdateChat)
        mSocket!!.on("userLeftChatRoom", onUserLeft)
        mSocket!!.connect()
    }

    var onConnect = Emitter.Listener {
        val data = Room(args.userName, args.roomName)
        val jsonData = Gson().toJson(data)
        mSocket!!.emit("subscribe", jsonData)
    }

    var onNewUser = Emitter.Listener {
        val name = it[0] as String
        val chat = Message(0, name,"", args.roomName)
        mChatAdapter!!.addItem(chat)
    }

    var onUserLeft = Emitter.Listener {
        val leftUserName = it[0] as String
        val chat = Message(0, leftUserName, "", args.roomName)
        mChatAdapter!!.addItem(chat)
    }

    var onUpdateChat = Emitter.Listener {
        val chat: Message = Gson().fromJson(it[0].toString(), Message::class.java)
        mChatAdapter!!.addItem(chat)
    }


    private fun sendMessage() {
        val content = binding!!.edtMsg.text.toString()
        val sendData = Message(0,args.userName, content, args.roomName)
        val jsonData = Gson().toJson(sendData)
        mSocket!!.emit("newMessage", jsonData)

        val chat = Message(0, args.userName, content, args.roomName)
        mChatAdapter!!.addItem(chat)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.disconnect()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding!!.btnSend){
            if (!binding!!.edtMsg.text.isNullOrBlank()){
                sendMessage()
            }
        }
    }

}