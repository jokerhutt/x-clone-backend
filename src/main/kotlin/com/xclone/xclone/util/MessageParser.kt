package com.xclone.xclone.util

object MessageParser {

    fun parseMessage(message: String): Map<String, String> {
        return mapOf("message" to message)
    }
}