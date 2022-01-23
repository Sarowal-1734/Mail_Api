package com.example.mailapi.models

data class Domain(
    val createdAt: String,
    val domain: String,
    val id: String,
    val isActive: Boolean,
    val isPrivate: Boolean,
    val updatedAt: String
)