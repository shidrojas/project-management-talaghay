package com.example.projectmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewAllTaskScreen : AppCompatActivity() {

    private val taskList = TaskRepository.tasks
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
            adapter.addTask(newTask) // ✅ use adapter helper to insert
        }
    }

    private val editTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult

            // ✅ Deletion branch
            val deletedTaskId = data.getStringExtra("deletedTaskId")
            if (deletedTaskId != null) {
                val index = taskList.indexOfFirst { it.id == deletedTaskId }
                if (index != -1) {
                    taskList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
                return@registerForActivityResult
            }

            // ✅ Update branch
            val id = data.getStringExtra("id") ?: return@registerForActivityResult
            val subject = data.getStringExtra("subject") ?: return@registerForActivityResult
            val description = data.getStringExtra("description") ?: ""
            val assignedTo = data.getStringExtra("assignedTo") ?: ""
            val status = data.getStringExtra("status") ?: "TO DO"

            val index = taskList.indexOfFirst { it.id == id }
            if (index != -1) {
                val existing = taskList[index]
                // Keep existing projectTitle if not provided in result
                val projectTitle = data.getStringExtra("projectTitle") ?: existing.projectTitle
                val updatedTask = Task(id, subject, description, assignedTo, status, projectTitle)
                taskList[index] = updatedTask
                adapter.updateTaskAt(index, updatedTask) // ✅ use adapter helper
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_all_task_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TaskAdapter(taskList) { task ->
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

        findViewById<Button>(R.id.returnBtn).setOnClickListener { finish() }

        findViewById<Button>(R.id.tabProjects).setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }


    }
}
