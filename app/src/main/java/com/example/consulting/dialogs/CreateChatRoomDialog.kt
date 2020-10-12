package com.example.consulting.dialogs


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.consulting.R
import com.example.consulting.isEmpty
import com.example.consulting.models.ChatMessage
import com.example.consulting.models.Chatroom
import com.example.consulting.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.new_chatroom_dialog.*
import kotlinx.android.synthetic.main.new_chatroom_dialog.view.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class CreateChatRoomDialog(val mContext: Context) : DialogFragment() {

    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    private val TAG = "CreateChatRoomDialog"
    var userSecurityLevel = 0
    var choosedSecurityLevel = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.new_chatroom_dialog, container, false)

        getUserSecurityLevel()

        view.securityLevelSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                view.securityLevel.setText(i.toString())
                choosedSecurityLevel = i

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "onStartTrackingTouch: start tracking")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "onStopTrackingTouch: stop tracking")
            }
        })


        view.createChatroomBtn.setOnClickListener {

            if (!isEmpty(chatroomName.text.toString())) {
                if (userSecurityLevel >= choosedSecurityLevel && choosedSecurityLevel != 0) {
                    dbRef =
                        Firebase.database.reference.child(mContext.getString(R.string.dbnode_chatrooms))
                    val chatroomId = dbRef.push().key

                    val chatroom = Chatroom()
                    chatroom.chatroom_name = chatroomName.text.toString()
                    chatroom.security_level = choosedSecurityLevel.toString()
                    chatroom.chatroom_id = chatroomId.toString()
                    chatroom.creater_id = auth.currentUser!!.uid

                    dbRef = Firebase.database.reference
                        .child(mContext.getString(R.string.dbnode_chatrooms))
                        .child(chatroomId!!)
                    dbRef.setValue(chatroom).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(
                                TAG,
                                "OnComplete : chatroom inserted with id: ${chatroom.chatroom_id}"
                            )
                            Toast.makeText(mContext, "Chatroom created", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Log.d(TAG, "OnComplete : can not insert chatroom, ", task.exception)
                            dismiss()
                        }
                    }


                    //create welcome msg

                    val chatMessage = ChatMessage()
                    chatMessage.message = "Welcome to the new chatroom!"
                    chatMessage.timestamp = getTimeStamp()

                    val messageId = Firebase.database.reference
                        .push().key.toString()
                    dbRef = Firebase.database.reference
                        .child(mContext.getString(R.string.dbnode_chatrooms))
                        .child(chatroomId!!)
                        .child(mContext.getString(R.string.field_chatroom_message))
                        .child(messageId)
                    dbRef.setValue(chatMessage)


                } else {
                    Toast.makeText(mContext, "insuffient security level", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(mContext, "chatroom name can not be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }




        return view
    }


    private fun getUserSecurityLevel() {
        dbRef = Firebase.database.reference
            .child(mContext.getString(R.string.dbnode_users))

        val query = dbRef.orderByKey().equalTo(auth.currentUser!!.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.children.iterator().next().getValue(User::class.java)
                userSecurityLevel = user!!.security_level.toInt()
                Log.d(TAG, "onDataChange -> found user with securityLevel: $userSecurityLevel ")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled -> ", error.toException())
            }

        })
    }

    fun getTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("Canada/Pacific")
        return sdf.format(Date())
    }


}


