package com.example.projectmanagementapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity2 : AppCompatActivity() {

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail2)

        val projectTitleView = findViewById<TextView>(R.id.projectTitleText)
        val subjectView = findViewById<EditText>(R.id.detailSubject)
        val descriptionView = findViewById<EditText>(R.id.detailDescription)
        val assignedToView = findViewById<EditText>(R.id.detailAssignedTo)

        val radios = listOf(
            findViewById<RadioButton>(R.id.radio1),
            findViewById<RadioButton>(R.id.radio2),
            findViewById<RadioButton>(R.id.radio3),
            findViewById<RadioButton>(R.id.radio4)
        )

        val updateBtn = findViewById<Button>(R.id.updateButton)
        val deleteBtn = findViewById<Button>(R.id.deleteButton)

        // Get task from intent
        task = intent.getSerializableExtra("task") as? Task

        // Populate fields
        task?.let { t ->
            projectTitleView.text = t.projectTitle
            subjectView.setText(t.subject)
            descriptionView.setText(t.description)
            assignedToView.setText(t.assignedTo)

            val selectedRadio = when (t.status.uppercase()) {
                "TO DO" -> R.id.radio1
                "IN PROGRESS" -> R.id.radio2
                "FOR REVIEW" -> R.id.radio3
                "COMPLETED" -> R.id.radio4
                else -> -1
            }
            if (selectedRadio != -1) findViewById<RadioButton>(selectedRadio).isChecked = true
        }

        // Enforce single selection in GridLayout
        radios.forEach { rb ->
            rb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    radios.filter { it != buttonView }.forEach { it.isChecked = false }
                }
            }
        }

        // Update task
        updateBtn.setOnClickListener {
            val statusText = radios.find { it.isChecked }?.text.toString()
            val updatedTask = task?.copy(
                subject = subjectView.text.toString(),
                description = descriptionView.text.toString(),
                assignedTo = assignedToView.text.toString(),
                status = statusText
            )

            updatedTask?.let {
                val globalIndex = TaskRepository.tasks.indexOfFirst { t -> t.id == it.id }
                if (globalIndex != -1) TaskRepository.tasks[globalIndex] = it

                val resultIntent = Intent().apply { putExtra("updatedTask", it) }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        findViewById<Button>(R.id.doneButton).setOnClickListener { finish() }

        // Delete task
        deleteBtn.setOnClickListener {
            task?.let {
                val globalIndex = TaskRepository.tasks.indexOfFirst { t -> t.id == it.id }
                if (globalIndex != -1) TaskRepository.tasks.removeAt(globalIndex)

                val resultIntent = Intent().apply { putExtra("deletedTaskId", it.id) }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
