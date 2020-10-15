package com.example.consulting.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consulting.CHATROOM_ID
import com.example.consulting.ChatRoomActivity
import com.example.consulting.R
import com.example.consulting.models.Chatroom
import com.example.consulting.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader

class ChatAdapter(val context: Context, val chatList: ArrayList<Chatroom>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val createdBy = itemView.findViewById<TextView>(R.id.createdByTv)
        val chatroomName = itemView.findViewById<TextView>(R.id.chatroomName)
        val userProfileImage = itemView.findViewById<ImageView>(R.id.userProfileImage)
        val numberChatMessages = itemView.findViewById<TextView>(R.id.numberChatMessages)
        val deleteChatBtn = itemView.findViewById<ImageView>(R.id.deleteChatBtn)
        var chatroomId = ""

        init{
            itemView.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra(CHATROOM_ID , chatroomId)
                context.startActivity(intent)
            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.card_chat, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.chatroomName.text = chat.chatroom_name
        holder.numberChatMessages.text = "${chat.chatroomMessages.size} messages"
        getUserDetails(chat, holder)
        holder.chatroomId = chat.chatroom_id
    }

    override fun getItemCount() = chatList.size

    private fun getUserDetails(chatroom: Chatroom, holder: ViewHolder) {

        val usersRef = Firebase.database.reference
            .child(context.getString(R.string.dbnode_users))
        val query = usersRef.orderByKey().equalTo(chatroom.creator_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    val user = singleSnapshot.getValue(User::class.java)!!
                    ImageLoader.getInstance()
                        .displayImage(user.profile_image, holder.userProfileImage)
                    holder.createdBy.text = "Created by ${user.name} "
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}