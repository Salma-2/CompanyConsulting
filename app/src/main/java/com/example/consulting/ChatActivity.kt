package com.example.consulting

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consulting.adapters.ChatAdapter
import com.example.consulting.dialogs.CreateChatRoomDialog
import com.example.consulting.models.ChatMessage
import com.example.consulting.models.Chatroom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var chatroomList: ArrayList<Chatroom>
    private val TAG = "ChatActivity"
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
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(singleSnapshot in snapshot.children){
                    val objectMap = singleSnapshot.getValue() as Map<String, Object>
                    val chatroom = Chatroom()
                    chatroom.chatroom_name = objectMap.get(getString(R.string.field_chatroom_name)).toString()
                    chatroom.security_level = objectMap.get(getString(R.string.field_security_level)).toString()
                    chatroom.creator_id = objectMap.get(getString(R.string.field_creator_id)).toString()
                    chatroom.chatroom_id =objectMap.get(getString(R.string.field_chatroom_id)).toString()

                    Log.d(TAG, "new chatroom added to chatroom list -> ${chatroom.chatroom_name}")
                    val messages = ArrayList<ChatMessage>()
                    for(msgSnapshot in singleSnapshot.child(getString(R.string.field_chatroom_message)).children ){
                        val message = msgSnapshot.getValue(ChatMessage::class.java)!!
                        messages.add(message)
                        Log.d(TAG, "new message added to message list -> ${message.message}")
                    }


                    chatroom.chatroomMessages = messages
                    chatroomList.add(chatroom)
                    chatList.adapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

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