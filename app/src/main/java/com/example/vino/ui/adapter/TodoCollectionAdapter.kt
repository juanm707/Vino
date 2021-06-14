package com.example.vino.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vino.ui.todos.TodoListSubFragment

const val ARG_TODO_TYPE = "completed" // is this completed or incomplete todos

// The adapter for the view pager, "which fragments to show"
class TodoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 //one for incomplete and one for complete

    override fun createFragment(position: Int): Fragment {
        val fragment = TodoListSubFragment()
        if (position == 0) {
            fragment.arguments = Bundle().apply {
                putBoolean(ARG_TODO_TYPE, false)
            }
        } else if (position == 1) {
            fragment.arguments = Bundle().apply {
                putBoolean(ARG_TODO_TYPE, true)
            }
        }
        return fragment
    }
}
