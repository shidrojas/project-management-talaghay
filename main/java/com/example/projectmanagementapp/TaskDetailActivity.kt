package com.example.projectmanagementapp

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Get data from intent
        val projectTitle = intent.getStringExtra("projectTitle") ?: "No Project"
        val subject = intent.getStringExtra("subject") ?: "No Subject"
        val description = intent.getStringExtra("description") ?: ""
        val assignedTo = intent.getStringExtra("assignedTo") ?: "No Assignee"
        val status = intent.getStringExtra("status") ?: "TO DO"
        val taskIndex = intent.getIntExtra("taskIndex", -1)

        // Views
        val projectTitleView = findViewById<TextView>(R.id.projectTitleText)
        val subjectView = findViewById<TextView>(R.id.detailSubject)
        val descriptionEdit = findViewById<EditText>(R.id.detailDescription)
        val assignedToView = findViewById<TextView>(R.id.detailAssignedTo)

        // Make description scrollable and top-aligned
        descriptionEdit.movementMethod = ScrollingMovementMethod()
        descriptionEdit.gravity = android.view.Gravity.TOP or android.view.Gravity.START

        // Set view texts
        projectTitleView.text = projectTitle
        subjectView.text = subject
        descriptionEdit.setText(description)
        assignedToView.text = assignedTo

        // Radio buttons
        val radios = listOf(
            findViewById<RadioButton>(R.id.radio1), // TO DO
            findViewById<RadioButton>(R.id.radio2), // IN PROGRESS
            findViewById<RadioButton>(R.id.radio3), // FOR REVIEW
            findViewById<RadioButton>(R.id.radio4)  // DONE
        )

        // Make radio buttons exclusive
        radios.forEach { radio ->
            radio.setOnClickListener {
                radios.forEach { it.isChecked = it == radio }
            }
        }

        // Set initial status
        when (status.uppercase()) {
            "TO DO" -> radios[0].isChecked = true
            "IN PROGRESS" -> radios[1].isChecked = true
            "FOR REVIEW" -> radios[2].isChecked = true
            "DONE", "COMPLETED" -> radios[3].isChecked = true
        }

        // Update button
        findViewById<Button>(R.id.updateButton).setOnClickListener {
            if (taskIndex in TaskRepository.tasks.indices) {
                val updatedStatus = radios.find { it.isChecked }?.text?.toString() ?: status
                val updatedDescription = descriptionEdit.text.toString()

                val task = TaskRepository.tasks[taskIndex]
                task.description = updatedDescription
                task.status = updatedStatus

                val resultIntent = Intent().apply {
                    putExtra("id", task.id)
                    putExtra("subject", task.subject)
                    putExtra("description", updatedDescription)
                    putExtra("assignedTo", task.assignedTo)
                    putExtra("status", updatedStatus)
                    putExtra("taskIndex", taskIndex)
                    putExtra("projectTitle", projectTitle) // include project title
                }
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }

        // Done button
        findViewById<Button>(R.id.doneButton).setOnClickListener { finish() }
    }
}
