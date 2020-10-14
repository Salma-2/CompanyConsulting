package com.example.consulting.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consulting.R
import com.example.consulting.isEmpty
import com.example.consulting.models.ChatMessage
import com.example.consulting.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader


class ChatRoomAdapter(val context: Context, val chatroomMessages: ArrayList<ChatMessage>) :
    RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userNameTv)
        val userProfileImage = itemView.findViewById<ImageView>(R.id.userProfileImage)
        val message = itemView.findViewById<TextView>(R.id.messageTv)
    }

    private fun getUserDetails(userId: String, holder: ViewHolder) {

        val usersRef = Firebase.database.reference
            .child(context.getString(R.string.dbnode_users))
        val query = usersRef.orderByKey().equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    val user = singleSnapshot.getValue(User::class.java)!!
                    holder.userName.text = user.name
                    ImageLoader.getInstance().displayImage(user.profile_image, holder.userProfileImage)
                }

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.card_message, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = chatroomMessages[position]

        if (!isEmpty(message.user_id)) {
           getUserDetails(message.user_id, holder)
            holder.message.text = message.message
        } else {
            holder.message.text = message.message
        }
    }


    override fun getItemCount() = chatroomMessages.size
}
