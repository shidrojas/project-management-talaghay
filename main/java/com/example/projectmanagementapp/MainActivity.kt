package com.example.projectmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val taskList = TaskRepository.tasks
    private val displayedTasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    private val createTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val id = data.getStringExtra("id") ?: return@registerForActivityResult
            val subject = data.getStringExtra("subject") ?: return@registerForActivityResult
            val description = data.getStringExtra("description") ?: ""
            val assignedTo = data.getStringExtra("assignedTo") ?: ""
            val status = data.getStringExtra("status") ?: "TO DO"
            val projectTitle = data.getStringExtra("projectTitle") ?: "No Project"

            val newTask = Task(id, subject, description, assignedTo, status, projectTitle)
            taskList.add(newTask)
            updateDisplayedTasks()
            updateTaskCount()
        }
    }

    private val editTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult

            val deletedTaskId = data.getStringExtra("deletedTaskId")
            if (deletedTaskId != null) {
                val index = taskList.indexOfFirst { it.id == deletedTaskId }
                if (index != -1) {
                    taskList.removeAt(index)
                }
                updateDisplayedTasks()
                updateTaskCount()
                return@registerForActivityResult
            }

            val id = data.getStringExtra("id") ?: return@registerForActivityResult
            val subject = data.getStringExtra("subject") ?: return@registerForActivityResult
            val description = data.getStringExtra("description") ?: ""
            val assignedTo = data.getStringExtra("assignedTo") ?: ""
            val status = data.getStringExtra("status") ?: "TO DO"

            val index = taskList.indexOfFirst { it.id == id }
            if (index != -1) {
                val existing = taskList[index]
                val projectTitle = data.getStringExtra("projectTitle") ?: existing.projectTitle
                val updatedTask = Task(id, subject, description, assignedTo, status, projectTitle)
                taskList[index] = updatedTask

                updateDisplayedTasks()
                updateTaskCount()
            }
        }
    }

    private val projectDetailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val projectToDelete = result.data?.getSerializableExtra("projectToDelete") as? Project
            if (projectToDelete != null) {
                ProjectRepository.projects.removeIf { it.id == projectToDelete.id }
                updateProjectCount()
            }
        }
    }

    private fun updateDisplayedTasks() {
        displayedTasks.clear()
        displayedTasks.addAll(taskList.take(3))
        adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.tabProjects).setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

        findViewById<ImageView>(R.id.profileIcon).setOnClickListener {
            startActivity(Intent(this, Profile_section::class.java))
        }

        findViewById<Button>(R.id.ViewAllTasks).setOnClickListener {
            startActivity(Intent(this, ViewAllTaskScreen::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TaskAdapter(displayedTasks) { task ->
            val index = taskList.indexOf(task)
            val intent = Intent(this, TaskDetailActivity::class.java).apply {
                putExtra("id", task.id)
                putExtra("subject", task.subject)
                putExtra("description", task.description)
                putExtra("assignedTo", task.assignedTo)
                putExtra("status", task.status)
                putExtra("projectTitle", task.projectTitle)
                putExtra("taskIndex", index)
            }
            editTaskLauncher.launch(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateDisplayedTasks()
        updateTaskCount()
        updateProjectCount()
    }

    private fun updateTaskCount() {
        val totalCountText = findViewById<TextView>(R.id.taskCountNumber)
        val completedCountText = findViewById<TextView>(R.id.completedCountNumber)
        val todoCountText = findViewById<TextView>(R.id.todoCountNumber)

        totalCountText.text = taskList.size.toString()
        completedCountText.text = taskList.count { it.status.trim().equals("completed", ignoreCase = true) }.toString()
        todoCountText.text = taskList.count { it.status.trim().equals("TO DO", ignoreCase = true) }.toString()
    }

    private fun updateProjectCount() {
        val projectCountText = findViewById<TextView>(R.id.projectCountNumber)
        projectCountText.text = ProjectRepository.projects.size.toString()
    }
}

