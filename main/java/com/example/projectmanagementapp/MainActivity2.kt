package com.example.projectmanagementapp

import android.app.Activity
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

class MainActivity2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter

    // Launcher for Create Project screen
    private val createProjectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged()
        }
    }

    // Launcher for Project Details screen (for delete)
    private val detailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val projectToDelete = result.data?.getSerializableExtra("projectToDelete") as? Project
            if (projectToDelete != null) {
                ProjectRepository.projects.remove(projectToDelete)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProjectAdapter(ProjectRepository.projects) { project ->
            // Go to ProjectDetailsActivity when clicking a project
            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("project", project)
            detailsLauncher.launch(intent)
        }
        recyclerView.adapter = adapter

        // FAB to add project
        val fab: FloatingActionButton = findViewById(R.id.fabAddProject)
        fab.setOnClickListener {
            val intent = Intent(this, CreateProjectActivity::class.java)
            createProjectLauncher.launch(intent)
        }

        // Return button
        findViewById<Button>(R.id.returnBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Task button
        findViewById<Button>(R.id.taskBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
