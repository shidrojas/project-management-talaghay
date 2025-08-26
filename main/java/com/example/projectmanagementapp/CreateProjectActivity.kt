package com.example.projectmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_project)

        val titleInput = findViewById<EditText>(R.id.ProjectTitleInput)
        val descriptionInput = findViewById<EditText>(R.id.ProjectDescriptionInput)
        val passwordInput = findViewById<EditText>(R.id.ProjectPasswordInput)

        val projectCreation = findViewById<Button>(R.id.createProjectButton)
        projectCreation.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val password = passwordInput.text.toString().trim().ifEmpty { null }

            if (title.isBlank()) {
                Toast.makeText(this, "Project title is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duplicateExists = ProjectRepository.projects.any {
                it.title.equals(title, ignoreCase = true)
            }
            if (duplicateExists) {
                Toast.makeText(this, "A project with this title already exists.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newProject = Project(
                title = title,
                description = description,
                password = password
            )

            // ✅ Add to repository so it appears in the list
            ProjectRepository.projects.add(newProject)

            // ✅ Pass result back to main activity
            val resultIntent = Intent().apply {
                putExtra("id", newProject.id)
                putExtra("title", newProject.title)
                putExtra("description", newProject.description)
                putExtra("password", newProject.password)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        val backbtn = findViewById<Button>(R.id.backButton)
        backbtn.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
