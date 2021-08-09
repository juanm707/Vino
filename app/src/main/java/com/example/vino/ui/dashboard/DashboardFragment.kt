package com.example.vino.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentDashboardBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private val dashboardFragmentViewModel: DashboardFragmentViewModel by viewModels {
        DashboardFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAccountAndCompanyName()
        setTodaysDate()
        setSprayInfo()
        setTodoInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpAccountAndCompanyName() {
        vinoUserModel.vinoUser.observe(viewLifecycleOwner, { user ->
            binding.accountName.text = "${user.firstName} ${user.lastName}"
            binding.companyName.text = user.company
        })
    }

    private fun setTodaysDate() {
        val sdf = SimpleDateFormat("EEE MMMM d, yyyy", Locale.US)
        val date = sdf.format(Calendar.getInstance().time)
        binding.dateText.text = "$date"

    }

    private fun setSprayInfo() {
        dashboardFragmentViewModel.getSprayCount()

        dashboardFragmentViewModel.sprayCount.observe(viewLifecycleOwner, { sprayCount ->
            if (sprayCount == 1)
                binding.spraysThisMorning.text = "spray this morning"
            binding.sprayNumber.text = "$sprayCount"
        })

        dashboardFragmentViewModel.sprayedVineyards.observe(viewLifecycleOwner, { sprayText ->
            binding.vineyardPreviewText.text = sprayText
        })
    }

    private fun setTodoInfo() {
        binding.todoCardViewDashboard.setOnClickListener {
            val action = DashboardFragmentDirections.actionNavigationDashboardToNavigationTodos()
            findNavController().navigate(action)
        }
        vinoUserModel.inCompleteTodos.observe(viewLifecycleOwner, { todoList ->
            val todosDueToday = dashboardFragmentViewModel.getTodosDueTodayCount(todoList)
            if (todosDueToday == 1)
                binding.todosSubtitle.text = "todo due today"
            binding.todosDueToday.text = "$todosDueToday"
        })
    }
}