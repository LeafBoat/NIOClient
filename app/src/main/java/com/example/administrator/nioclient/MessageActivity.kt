package com.example.administrator.nioclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.administrator.nioclient.chat.Client
import com.example.administrator.nioclient.chat.ClientHandler

class MessageActivity : AppCompatActivity() {
    private var messageET: EditText? = null
    private var recyclerView: RecyclerView? = null
    var adapter: MyAdapter? = null
    var friendid: String = ""
    private var selfid: String? = ""
    var client: Client? = null
    var messages: ArrayList<Message>? = null
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


    private fun initdata() {
        friendid = intent.getStringExtra("friend")
        selfid = intent.getStringExtra("account")
        messages = ArrayList()
        adapter = MyAdapter(this, messages!!)
        recyclerView!!.adapter = adapter
        client = Client.Builder.getBuilder().build()
        client!!.setReplyListener { msg ->
            runOnUiThread {
                var message = Message()
                var position = msg.indexOf(":")
                message.id = msg.substring(0, position)
                message.msg = msg.substring(position + 1)
                adapter!!.notify(message)
                recyclerView!!.smoothScrollToPosition(adapter!!.itemCount)
            }
        }
    }

    fun send(v: View) {
        var message = messageET!!.text.toString()
        client!!.sendMessage(selfid, friendid, message)
        var msg = Message()
        msg.id = selfid
        msg.msg = message
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
        var message = messages[position]
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