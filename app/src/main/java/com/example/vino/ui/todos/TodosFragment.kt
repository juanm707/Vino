package com.example.vino.ui.todos

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentTodosBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.Todo
import com.example.vino.ui.adapter.TodoCollectionAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        vinoUserModel.refreshTodos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewPagerAdapter() {
        todoCollectionAdapter = TodoCollectionAdapter(this)
        viewPager = binding.pager
        viewPager.adapter = todoCollectionAdapter
        viewPager.isUserInputEnabled = false // no scroll
    }

    private fun setUpTabLayout() {
        val tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Incomplete"
                    tab.contentDescription = "Incomplete tasks"
                }
                1 -> {
                    tab.text = "Completed"
                    tab.contentDescription = "Completed tasks"
                    tab.customView = getCustomTabView(R.color.light_green_dark)
                }
                else -> {
                    tab.text = "Oops" // should never see this
                }
            }
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if (tab.position == 0) {
                        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple_600))
                    }
                    else {
                        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.light_green_600))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        // updates whenever the database is updated
        vinoUserModel.inCompleteTodos.observe(viewLifecycleOwner, { newTodoInCompleteAmount ->
            val tab = tabLayout.getTabAt(0)
            if (newTodoInCompleteAmount.isEmpty())
                tab?.removeBadge()
            else
               tab?.orCreateBadge?.number = newTodoInCompleteAmount.size
        })

    }

    private fun setUpBottomSheet() {
        var dueByDate: Long? = null

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.addTodoBottomSheetContent)

        binding.addTodoButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.bottomSheetLayout.closeBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            clearTodoInput()
        }
        binding.bottomSheetLayout.addTodoBottomSheetButton.setOnClickListener {
            dueByDate = dueByDate?.plus(86400000)
            val newTodo = Todo(
                99,
                binding.bottomSheetLayout.titleEditText.text.toString(),
                binding.bottomSheetLayout.todoEditText.text.toString(),
                dueByDate?: 0,
                false)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            vinoUserModel.insertTodo(newTodo)
        }

        binding.bottomSheetLayout.newTodoDueBy.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select due date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds() + 86400000) // set tomorrow as default
                .build()
            datePicker.show(childFragmentManager, "Date Picker")

            datePicker.addOnPositiveButtonClickListener {
                if (datePicker.selection != null) {
                    val sdf = SimpleDateFormat("dd MMM", Locale.US)
                    dueByDate = datePicker.selection
                    var textDueByDate: String? = null
                    if (dueByDate != null) {
                        textDueByDate = sdf.format(Date(dueByDate!! + 86400000))
                    }
                    binding.bottomSheetLayout.newTodoDueBy.text = "Due by: $textDueByDate"
                }
            }
        }
    }

    private fun getCustomTabView(colorResource: Int): TextView {
        val textView: TextView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab,null) as TextView
        textView.setTextColor(ContextCompat.getColor(requireContext(), colorResource))
        return textView
    }

    private fun clearTodoInput() {
        // TODO Clear todo input on bottom sheet hide?
    }
}
