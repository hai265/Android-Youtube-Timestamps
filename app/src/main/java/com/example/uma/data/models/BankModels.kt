package com.example.uma.data.models

data class Bank (
    val id: Int,
    val name: String,
    val isExternal: Boolean,
    val type: String,
    val balance: Money,
    val last4Account: String,
    val image: String,
    //don't add transfer estimate here since it depends on where we're transferring money
)

data class Money (
    val amount: String,
    val currency: String
)