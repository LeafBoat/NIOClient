package com.example.administrator.nioclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class MessageActivity : AppCompatActivity() {
    private var messageET: EditText? = null
    private var recyclerView: RecyclerView? = null
    var adapter: MyAdapter? = null
    var friendid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        findViews()
        initdata()
    }

    private fun findViews() {
        recyclerView = findViewById(R.id.list) as RecyclerView
        messageET = findViewById(R.id.message) as EditText
    }

    private var selfid: String? = ""
    private var content: String? = ""

    private fun initdata() {
        friendid = intent.getStringExtra("friend")
        selfid = intent.getStringExtra("account")
        adapter = MyAdapter(this, ArrayList())
        recyclerView!!.adapter = adapter
        ClientHandler.setReadListener { msg ->
            var message = Message()
            message.id = friendid
            message.msg = msg
            adapter!!.notify(message)
            recyclerView!!.smoothScrollToPosition(adapter!!.itemCount)
        }
    }

    fun send(v: View) {
        content = messageET!!.text.toString()
        var message = friendid + "\r\nsend\r\n" + content
        MessageParser.sendMessage(MessageParser.getSocketChannel(), message)
        var msg = Message()
        msg.id = selfid
        msg.msg = content
        adapter!!.notify(msg)
        recyclerView!!.smoothScrollToPosition(adapter!!.itemCount)
    }
}

class MyAdapter(activity: MessageActivity, messages: ArrayList<Message>) : RecyclerView.Adapter<ViewHolder>() {

    var activity: MessageActivity = activity
    var messages: ArrayList<Message> = messages

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var itemView = holder!!.itemView as TextView
        var message = messages.get(position)
        if (message.id.equals(activity.friendid)) {
            itemView.gravity = Gravity.RIGHT
            itemView.text = message.msg + "  " + message.id
        } else {
            itemView.gravity = Gravity.LEFT
            itemView.text = message.id + "  " + message.msg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var textView = TextView(activity)
        return ViewHolder(textView)
    }

    fun notify(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }

}

class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

}

class Message {
    var id: String? = null
    var msg: String? = null
}