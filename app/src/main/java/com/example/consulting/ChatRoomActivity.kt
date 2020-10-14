package com.example.consulting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consulting.adapters.ChatRoomAdapter
import com.example.consulting.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlin.collections.ArrayList

class ChatRoomActivity() : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var chatroomRef: DatabaseReference
    val TAG = "ChatRoomActivity"
    lateinit var chatroomMessages: ArrayList<ChatMessage>

    private val chatMessagesAdapter by lazy {
        ChatRoomAdapter(this, chatroomMessages)
    }
    private val chatMessagesLayoutManager by lazy {
        LinearLayoutManager(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        //get chatroom id
        val chatroomId = intent.getStringExtra(CHATROOM_ID)

        chatroomRef = Firebase.database.reference
            .child(this.getString(R.string.dbnode_chatrooms))
            .child(chatroomId)
            .child(this.getString(R.string.field_chatroom_message))

        chatroomRef.addValueEventListener(valueEventListener)

        chatroomMessages = ArrayList()

        messagesList.layoutManager = chatMessagesLayoutManager
        messagesList.adapter = chatMessagesAdapter
        auth = Firebase.auth

        init()


    }


    private fun init() {
        sendMessageBtn.setOnClickListener {
            if (!isEmpty(inputMessage.text.toString())) {
                val messageId = Firebase.database.reference.push().key.toString()
                val message = ChatMessage()

                message.message = inputMessage.text.toString()
                message.timestamp = getTimeStamp()
                message.user_id = auth.currentUser!!.uid

                chatroomRef.child(messageId).setValue(message).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: pushed message with id: $messageId")
                        inputMessage.setText("")
                    } else {
                        Log.d(TAG, "onComplete: can not push message, ", task.exception)
                    }
                }
            }
        }
    }

    private fun getChatroomMessages() {
        chatroomMessages.clear()
        val query = chatroomRef
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    val msg = singleSnapshot.getValue(ChatMessage::class.java)
                    chatroomMessages.add(msg!!)
                    messagesList.adapter?.notifyDataSetChanged()
                    Log.d(TAG, "Added msg to msg list : ${msg.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "con not add msg to msg list :", error.toException())
            }

        })

    }

    override fun onResume() {
        super.onResume()
        messagesList.adapter?.notifyDataSetChanged()
    }

    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onDataChange: getChatroomMessage")
            getChatroomMessages()
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