package com.example.projectmanagementapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>, // ✅ mutable list to allow updates
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subject = itemView.findViewById<TextView>(R.id.subjectText)
        val description = itemView.findViewById<TextView>(R.id.descText)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(taskList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.subject.text = task.subject
        holder.description.text = task.description

        val statusCircle = holder.itemView.findViewById<View>(R.id.statusCircle)
        val color = when (task.status.lowercase()) {
            "to do" -> R.color.light_blue
            "in progress" -> R.color.yellow
            "for review" -> R.color.red
            "done", "completed" -> R.color.green
            else -> R.color.dark_grey
        }

        statusCircle.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, color)
        )
    }

    // ✅ helper function to update a task and refresh UI
    fun updateTaskAt(index: Int, updatedTask: Task) {
        if (index in 0 until taskList.size) {
            taskList[index] = updatedTask
            notifyItemChanged(index)
        }
    }

    // ✅ helper to add a task
    fun addTask(task: Task) {
        taskList.add(task)
        notifyItemInserted(taskList.size - 1)
    }
}
