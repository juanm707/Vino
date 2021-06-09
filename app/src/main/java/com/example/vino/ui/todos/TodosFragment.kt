package com.example.vino.ui.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.vino.R
import com.example.vino.databinding.FragmentTodosBinding
import com.example.vino.ui.adapter.TodoRecyclerViewAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TodosFragment : Fragment() {

    private lateinit var todosViewModel: TodosViewModel
    private var _binding: FragmentTodosBinding? = null

    private lateinit var todoCollectionAdapter: TodoCollectionAdapter
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        todosViewModel =
            ViewModelProvider(this).get(TodosViewModel::class.java)

        _binding = FragmentTodosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoCollectionAdapter = TodoCollectionAdapter(this)
        viewPager = binding.pager
        viewPager.adapter = todoCollectionAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Incomplete"
                    tab.contentDescription = "Incomplete tasks"
                    val badge = tab.orCreateBadge
                    badge.number = 3
                }
                1 -> {
                    tab.text = "Completed"
                    tab.contentDescription = "Completed tasks"
                }
                else -> {
                    tab.text = "Oops"
                }
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TodoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = DemoObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }

}

private const val ARG_OBJECT = "object"

// Instances of this class are fragments representing a single
// object in our collection.
class DemoObjectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_collection_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.todo_recycler_view)
        recyclerView.adapter = TodoRecyclerViewAdapter()
        recyclerView.setHasFixedSize(true)
    }
}