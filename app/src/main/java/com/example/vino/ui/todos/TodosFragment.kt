package com.example.vino.ui.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.vino.R
import com.example.vino.databinding.FragmentHomeBinding
import com.example.vino.databinding.FragmentTodosBinding
import com.example.vino.databinding.TodoFragmentCollectionObjectBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.VinoApiStatus
import com.example.vino.ui.adapter.TodoListAdapter
import com.example.vino.ui.adapter.TodoRecyclerViewAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TodosFragment : Fragment() {

    private val vinoUserModel: UserViewModel by activityViewModels()
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
        //todosViewModel = ViewModelProvider(this).get(TodosViewModel::class.java)

        _binding = FragmentTodosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPagerAdapter()

        setUpTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewPagerAdapter() {
        todoCollectionAdapter = TodoCollectionAdapter(this)
        viewPager = binding.pager
        viewPager.adapter = todoCollectionAdapter
    }

    private fun setUpTabLayout() {
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Incomplete"
                    tab.contentDescription = "Incomplete tasks"
                    if (vinoUserModel.vinoUser.value != null) {
                        val badge = tab.orCreateBadge
                        badge.number = vinoUserModel.vinoUser.value?.todoAmount!!
                    }
                }
                1 -> {
                    tab.text = "Completed"
                    tab.contentDescription = "Completed tasks"
                }
                else -> {
                    tab.text = "Oops" // should never see this
                }
            }
        }.attach()
    }
}

// TODO MOVE TO FILE
// The adapter for the view pager, "which fragments to show"
class TodoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = TodoListFragment()
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

// TODO MAKE SEPARATE FILE
private const val ARG_TODO_TYPE = "completed" // is this completed or incomplete

// Instances of this class are fragments representing a single
// object in our collection.
class TodoListFragment : Fragment() {

    private val vinoUserModel: UserViewModel by activityViewModels()
    private var _binding: TodoFragmentCollectionObjectBinding? = null
    private var completed = false
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            completed = it.getBoolean(ARG_TODO_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TodoFragmentCollectionObjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpConnectionImageAndText()

        binding.todoRecyclerView.visibility = View.INVISIBLE // to show progress circle

        // TODO: where to put this?
        vinoUserModel.getTodos()
        
        vinoUserModel.todos.observe(viewLifecycleOwner, { todoList ->

            binding.progressCircular.hide() // hide progress once user is grabbed
            binding.todoRecyclerView.visibility = View.VISIBLE

            val adapter = TodoListAdapter(completed, requireContext())
            binding.todoRecyclerView.adapter = adapter

            lifecycleScope.launch(Dispatchers.Default) {
                val sortedTodoList = todoList
                    .filter { it.completed == completed }
                    .sortedBy { todo ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.DAY_OF_MONTH, todo.dueDate.substringAfter("/").toInt())
                        calendar.set(Calendar.MONTH, todo.dueDate.substringBefore("/").toInt())
                        return@sortedBy calendar.time
                    }

                activity?.runOnUiThread {
                    adapter.submitList(sortedTodoList)
                }
            }
        })

        binding.todoRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpConnectionImageAndText() {
        // on api status, either way DONE or ERROR, hide loading
        vinoUserModel.status.observe(viewLifecycleOwner, {
            if (it != VinoApiStatus.LOADING)
                binding.progressCircular.hide()
//            if (it == VinoApiStatus.ERROR)
//                binding.connectionStatusText.visibility = View.VISIBLE
            // if DONE hide text
        })
    }
}