package com.example.projectmanagementapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProjectDetailsActivity : AppCompatActivity() {

    private var project: Project? = null
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        project = intent.getSerializableExtra("project") as? Project

        findViewById<TextView>(R.id.ProjectTitle).text = project?.title ?: "No Title"
        findViewById<TextView>(R.id.ProjectDescription).text = project?.description ?: "No Description"

        loadProjectTasks()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewProjectTasks)
        taskAdapter = TaskAdapter(tasks) { task ->
            task?.let {
                val intent = Intent(this, TaskDetailActivity2::class.java)
                intent.putExtra("task", it)
                intent.putExtra("projectTitle", project?.title)
                taskDetailLauncher.launch(intent)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        findViewById<FloatingActionButton>(R.id.fabAddTask).setOnClickListener {
            val intent = Intent(this, TaskCreation::class.java)
            project?.let { intent.putExtra("projectTitle", it.title) }
            taskCreationLauncher.launch(intent)
        }

        findViewById<Button>(R.id.terminateBtn).setOnClickListener {
            project?.let { proj ->
                // Remove all tasks related to this project
                TaskRepository.tasks.removeAll { it.projectTitle == proj.title }

                // Clear local list and notify adapter
                tasks.clear()
                taskAdapter.notifyDataSetChanged()

                // Return deleted project info
                val resultIntent = Intent().apply {
                    putExtra("projectToDelete", proj)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            }
            finish()
        }

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun loadProjectTasks() {
        tasks.clear()
        project?.let { proj ->
            tasks.addAll(TaskRepository.tasks.filter { it.projectTitle == proj.title }.distinctBy { it.id })
        }
    }

    private val taskCreationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newTask = result.data?.getSerializableExtra("task") as? Task
            newTask?.let { task ->
                if (TaskRepository.tasks.none { it.id == task.id }) {
                    TaskRepository.tasks.add(task)
                }
                loadProjectTasks()
                taskAdapter.notifyDataSetChanged()
            }
        }
    }

    private val taskDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedTask = result.data?.getSerializableExtra("updatedTask") as? Task
            val deletedTaskId = result.data?.getStringExtra("deletedTaskId")

            updatedTask?.let { ut ->
                val globalIndex = TaskRepository.tasks.indexOfFirst { it.id == ut.id }
                if (globalIndex != -1) TaskRepository.tasks[globalIndex] = ut

                val index = tasks.indexOfFirst { it.id == ut.id }
                if (index != -1) {
                    tasks[index] = ut
                    taskAdapter.notifyItemChanged(index)
                }
            }

            deletedTaskId?.let { dtId ->
                val globalIndex = TaskRepository.tasks.indexOfFirst { it.id == dtId }
                if (globalIndex != -1) TaskRepository.tasks.removeAt(globalIndex)

                val index = tasks.indexOfFirst { it.id == dtId }
                if (index != -1) {
                    tasks.removeAt(index)
                    taskAdapter.notifyItemRemoved(index)
                }
            }
        }
    }
}
