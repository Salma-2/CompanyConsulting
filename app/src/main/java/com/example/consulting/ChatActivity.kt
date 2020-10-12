package com.example.consulting

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.consulting.dialogs.CreateChatRoomDialog
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.toolbar))

        createNewChatRoom.setOnClickListener {
            val dialog = CreateChatRoomDialog(this)
            dialog.show(supportFragmentManager, "CreateChatRoom")
        }


    }
}