package com.gl.messenger_server

import java.io.Serializable

data class User(
    val name: String,
    val age: Int
): Serializable