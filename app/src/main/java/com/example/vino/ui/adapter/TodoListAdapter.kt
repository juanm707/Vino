package com.example.vino.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
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
import com.example.vino.model.Todo
import java.text.SimpleDateFormat
import java.util.*

class TodoListAdapter(private val completed: Boolean, private val context: Context, val checkBoxListener: OnTodoCheckBoxListener) : ListAdapter<Todo, TodoListAdapter.TodoViewHolder>(DiffCallback) {

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

        val sdf = SimpleDateFormat("dd MMM", Locale.US)
        val dueOrCompletedDate = sdf.format(Date(todo.dueDate))

        holder.job.text = todo.job
        holder.description.text = todo.description

        if (completed) {
            holder.job.setTextColor(ContextCompat.getColor(context, R.color.greenHeaderText))
            holder.job.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.dueDate.setTextColor(ContextCompat.getColor(context, R.color.greenDarkerText))
            holder.description.setTextColor(ContextCompat.getColor(context, R.color.light_green_dark))
            holder.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4B830D"))
            holder.checkBox.isChecked = true
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_green_background))
            holder.dueDate.text = context.getString(R.string.completed_on, dueOrCompletedDate)
        } else {
            holder.checkBox.isChecked = false
            holder.dueDate.text = context.getString(R.string.due_by, dueOrCompletedDate)
        }

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkBoxListener.onCheckboxClick(holder.adapterPosition) // position or task id
            }
        }
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val job: TextView = itemView.findViewById(R.id.todo_title)
        val description: TextView = itemView.findViewById(R.id.description)
        val dueDate: TextView = itemView.findViewById(R.id.due_by_date)
        val checkBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        val cardView: CardView = itemView.findViewById(R.id.todo_card_view)

    }

    interface OnTodoCheckBoxListener {
        fun onCheckboxClick(position: Int) {}
    }
}