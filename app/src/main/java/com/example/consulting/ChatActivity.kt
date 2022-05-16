package com.example.consulting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consulting.adapters.ChatAdapter
import com.example.consulting.dialogs.CreateChatRoomDialog
import com.example.consulting.models.ChatMessage
import com.example.consulting.models.Chatroom
import com.example.consulting.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var chatroomList: ArrayList<Chatroom>
    private val TAG = "ChatActivity"
    private val auth = Firebase.auth
    private lateinit var chatroomRef: DatabaseReference
  
    private val chatroomAdapter by lazy {
        ChatAdapter(this, chatroomList)
    }
    private val chatroomLayoutManager by lazy {
        LinearLayoutManager(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.toolbar))

        createNewChatRoom.setOnClickListener {
            val dialog = CreateChatRoomDialog(this)
            dialog.show(supportFragmentManager, "CreateChatRoom")
        }


        chatroomRef = Firebase.database.reference
            .child(getString(R.string.dbnode_chatrooms))
        chatroomList = ArrayList()
        chatroomRef.addValueEventListener(valueEventListener)

        chatList.adapter = chatroomAdapter
        chatList.layoutManager = chatroomLayoutManager

    }

    override fun onResume() {
        super.onResume()
        chatList.adapter?.notifyDataSetChanged()
    }


    private fun getChatRoomList() {
        chatroomList.clear()
        val query = chatroomRef
        val userSecurityLevel = getUserSecurityLevel().toInt()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    val objectMap = singleSnapshot.getValue() as Map<String, Object>
                    val chatroom = Chatroom()
                    chatroom.security_level =
                        objectMap.get(getString(R.string.field_security_level)).toString()
                    if (userSecurityLevel >= chatroom.security_level.toInt()) {
                        chatroom.chatroom_name =
                            objectMap.get(getString(R.string.field_chatroom_name)).toString()
                        chatroom.security_level =
                            objectMap.get(getString(R.string.field_security_level)).toString()
                        chatroom.creator_id =
                            objectMap.get(getString(R.string.field_creator_id)).toString()
                        chatroom.chatroom_id =
                            objectMap.get(getString(R.string.field_chatroom_id)).toString()
                        Log.d(
                            TAG,
                            "new chatroom added to chatroom list -> ${chatroom.chatroom_name}"
                        )
                        val messages = ArrayList<ChatMessage>()
                        for (msgSnapshot in singleSnapshot.child(getString(R.string.field_chatroom_message)).children) {
                            val message = msgSnapshot.getValue(ChatMessage::class.java)!!
                            messages.add(message)
                            Log.d(TAG, "new message added to message list -> ${message.message}")
                        }
                        chatroom.chatroomMessages = messages
                        chatroomList.add(chatroom)
                        chatList.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun getUserSecurityLevel(): String {
        var user = User()
        val usersRef = Firebase.database.reference
            .child(this.getString(R.string.dbnode_users))
        val query = usersRef.orderByKey().equalTo(auth.currentUser!!.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    user = singleSnapshot.getValue(User::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return user.security_level
    }


    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onDataChange: getChatroomMessage")
            getChatRoomList()
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onDestroy() {
        Log.d(TAG, "------------onDestroy-------------")
        chatroomRef.removeEventListener(valueEventListener)
        super.onDestroy()
    }
}