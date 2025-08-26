package com.example.projectmanagementapp

import java.io.Serializable
import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val password: String? = null,
    val tasks: MutableList<Task> = mutableListOf()
) : Serializable