package com.example.vino.ui.todos

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class TodosFragment : Fragment() {

    //private val vinoUserModel: UserViewModel by activityViewModels()
    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }
    private var _binding: FragmentTodosBinding? = null
    private lateinit var todoCollectionAdapter: TodoCollectionAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
        setUpBottomSheet()

        vinoUserModel.getTodos()
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
//                    val badge = tab.orCreateBadge
////                    if (vinoUserModel.vinoUser.value != null) {
////                        val badge = tab.orCreateBadge
////                        badge.number = vinoUserModel.vinoUser.value?.todoAmount!!
////                    }
//                    vinoUserModel.todoAmount.observe(viewLifecycleOwner, { newTodoInCompleteAmount ->
//                        badge.number = newTodoInCompleteAmount
//                    })
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
        vinoUserModel.todoAmount.observe(viewLifecycleOwner, { newTodoInCompleteAmount ->
            tabLayout.getTabAt(0)?.orCreateBadge?.number = newTodoInCompleteAmount
        })
    }

    private fun setUpBottomSheet() {
        var dueByDate: String? = null
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.addTodoBottomSheetContent)
        binding.addTodoButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.bottomSheetLayout.closeBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            clearTodoInput()
        }
        binding.bottomSheetLayout.addTodoBottomSheetButton.setOnClickListener {
            val newTodo = Todo(
                99,
                binding.bottomSheetLayout.titleEditText.text.toString(),
                binding.bottomSheetLayout.todoEditText.text.toString(),
                dueByDate?: "1/1",
                false)

            vinoUserModel.insertTodo(newTodo)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.bottomSheetLayout.newTodoDueBy.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds() + 86400000) // set tomorrow as default
                .build()
            datePicker.show(childFragmentManager, "Date Picker")

            datePicker.addOnPositiveButtonClickListener {
                if (datePicker.selection != null) {
                    val sdf = SimpleDateFormat("M/dd", Locale.US)
                    dueByDate = sdf.format(Date(datePicker.selection!! + 86400000))
                    binding.bottomSheetLayout.newTodoDueBy.text = "Due by: $dueByDate"
                }
            }
        }
    }

    private fun clearTodoInput() {

    }
}
