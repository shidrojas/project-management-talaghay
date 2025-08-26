package com.example.projectmanagementapp

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TaskCreation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_creation)

        // Get project title from intent
        val projectTitle = intent.getStringExtra("projectTitle") ?: "No Project"
        val projectTitleView = findViewById<TextView>(R.id.ProjectTitle)
        projectTitleView.text = projectTitle

        val subjectInput = findViewById<EditText>(R.id.subject)
        val descriptionInput = findViewById<EditText>(R.id.description)
        val assignToInput = findViewById<EditText>(R.id.assignTo)

        // Make description scrollable and top-aligned
        descriptionInput.apply {
            gravity = android.view.Gravity.TOP or android.view.Gravity.START
            scrollBarStyle = android.view.View.SCROLLBARS_INSIDE_INSET
            setHorizontallyScrolling(false)
            isVerticalScrollBarEnabled = true
            overScrollMode = android.view.View.OVER_SCROLL_ALWAYS
        }

        val radioButtons = listOf(
            findViewById<RadioButton>(R.id.radio1),
            findViewById<RadioButton>(R.id.radio2),
            findViewById<RadioButton>(R.id.radio3),
            findViewById<RadioButton>(R.id.radio4)
        )

        // Make radio buttons exclusive
        radioButtons.forEach { radio ->
            radio.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                radio.isChecked = true
            }
        }

        // Create task button
        findViewById<Button>(R.id.createBtn).setOnClickListener {
            val subject = subjectInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val assignedTo = assignToInput.text.toString().trim()
            val selectedRadio = radioButtons.find { it.isChecked }

            if (subject.isBlank() || description.isBlank() || assignedTo.isBlank()) {
                Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedRadio == null) {
                Toast.makeText(this, "Please select a status.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val status = selectedRadio.text.toString()
            val newTask = Task(
                subject = subject,
                description = description,
                assignedTo = assignedTo,
                status = status,
                projectTitle = projectTitle
            )

            TaskRepository.tasks.add(newTask)

            val resultIntent = intent.apply { putExtra("task", newTask) }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Back button
        findViewById<Button>(R.id.backBtn).setOnClickListener { finish() }

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
