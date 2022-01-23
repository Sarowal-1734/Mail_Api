package com.example.mailapi.models

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("hydra:member")
    val hydra_member: MutableList<Message>,
    @SerializedName("hydra:totalItems")
    val hydra_totalItems: Int
)