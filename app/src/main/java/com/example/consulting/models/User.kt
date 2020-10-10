package com.example.consulting.models

class User {
    var name = ""
    var phone = "1"
    var profile_image = ""
    var security_level = "1"
    var user_id = ""


    override fun toString(): String {
        return "User{" +
                "name: $name," +
                "phone #: $phone, " +
                "profile_image: $profile_image, " +
                "security_level: $security_level, " +
                "user_id: $user_id}"
    }
}