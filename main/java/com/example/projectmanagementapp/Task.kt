package com.example.projectmanagementapp

import java.io.Serializable
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var subject: String,
    var description: String,
    var assignedTo: String,
    var status: String,
    var projectTitle: String = "" // ✅ default value so old code won’t break
) : Serializable