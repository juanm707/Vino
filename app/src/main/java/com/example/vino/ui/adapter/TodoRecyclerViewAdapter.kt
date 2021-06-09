package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R

class TodoRecyclerViewAdapter : RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.todo_list_item, parent, false)
        return TodoRecyclerViewAdapter.TodoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
       return 10
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}