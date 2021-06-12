package com.example.vino.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.network.Todo

class TodoListAdapter(private val completed: Boolean, private val context: Context) : ListAdapter<Todo, TodoListAdapter.TodoViewHolder>(DiffCallback) {

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
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false)
        return TodoViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.job.text = todo.job
        holder.description.text = todo.description

        if (completed) {
            holder.job.setTextColor(ContextCompat.getColor(context, R.color.greyMedium))
            holder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.greyDark))
            holder.description.setTextColor(ContextCompat.getColor(context, R.color.greyText))
            holder.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#434343"))
            holder.checkBox.isChecked = true
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.greyBackground))
            holder.dueDate.text = "Completed on ${todo.dueDate}"
        } else
            holder.dueDate.text = "Due by ${todo.dueDate}"
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val job: TextView = itemView.findViewById(R.id.todo_title)
        val description: TextView = itemView.findViewById(R.id.description)
        val dueDate: TextView = itemView.findViewById(R.id.due_by_date)
        val checkBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        val cardView: CardView = itemView.findViewById(R.id.todo_card_view)
    }
}