package com.example.consulting.models

import java.text.SimpleDateFormat
import java.util.*

class ChatMessage {
    var message = ""
    var user_id = ""
    var timestamp = ""

    override fun toString(): String {
        return "user_id: $user_id\n" +
                "message: $message\n" +
                "timestamp: $timestamp\n"
    }
}