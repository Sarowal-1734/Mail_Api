package com.example.mailapi.models

import com.google.gson.annotations.SerializedName

data class DomainResponse(
    @SerializedName("hydra:member")
    val hydra_member: List<Domain>,
    @SerializedName("hydra:totalItems")
    val hydra_totalItems: Int
)