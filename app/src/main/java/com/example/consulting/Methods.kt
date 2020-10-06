package com.example.consulting

 fun isEmpty(str: String): Boolean {
    return str.equals("")
}


 fun isValidDomain(email: String): Boolean {
    val startIndex = email.indexOf("@") + 1
    val domain = email.substring(startIndex)
    return domain.equals(DOMAIN_NAME)
}