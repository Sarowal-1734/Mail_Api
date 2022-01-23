package com.example.mailapi.models

data class Message(
    val id: String,
    val from: From,
    val to: List<To>,
    val subject: String,
    val intro: String,
    val seen: Boolean,
    val hasAttachments: Boolean,
    val createdAt: String,
    val updatedAt: String
)