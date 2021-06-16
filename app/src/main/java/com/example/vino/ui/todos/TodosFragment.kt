package com.example.vino.ui.todos

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentTodosBinding
import com.example.vino.databinding.TodoFragmentCollectionObjectBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.VinoApiStatus
import com.example.vino.network.Todo
import com.example.vino.ui.adapter.TodoCollectionAdapter
import com.example.vino.ui.adapter.TodoListAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class TodosFragment : Fragment() {

    //private val vinoUserModel: UserViewModel by activityViewModels()
    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }
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

        vinoUserModel.getTodos()

//        binding.addTodoButton.setOnClickListener {
//            Toast.makeText(requireContext(), "Add todo", Toast.LENGTH_SHORT).show()
//        }
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

        // TODO update tab layout appropriately
        //tabLayout.getTabAt(0)?.orCreateBadge?.number = 10
    }
}
