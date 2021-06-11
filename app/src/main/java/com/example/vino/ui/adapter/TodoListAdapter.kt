package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.network.Todo

class TodoListAdapter : ListAdapter<Todo, TodoListAdapter.TodoViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return ((oldItem.description == newItem.description) &&
                    (oldItem.dueDate == newItem.dueDate) &&
                    (oldItem.job == newItem.job))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.todo_list_item, parent, false)
        return TodoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.job.text = todo.job
        holder.description.text = todo.description
        holder.dueDate.text = "Due by ${todo.dueDate}"
    }


    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val job: TextView = itemView.findViewById(R.id.todo_title)
        val description: TextView = itemView.findViewById(R.id.description)
        val dueDate: TextView = itemView.findViewById(R.id.due_by_date)
    }
}