package com.example.mailapi.models

data class CurrentUserResponse(
    val address: String,
    val createdAt: String,
    val id: String,
    val isDeleted: Boolean,
    val isDisabled: Boolean,
    val quota: Int,
    val updatedAt: String,
    val used: Int
)