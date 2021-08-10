package com.example.vino.ui.dashboard

import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnResume
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentDashboardBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.ui.adapter.DashboardSprayAdapter
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private val dashboardFragmentViewModel: DashboardFragmentViewModel by viewModels {
        DashboardFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var sprayClosed = true
    private var density = 0F

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        density = resources.displayMetrics.density
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
        binding.sprayRecyclerView.setHasFixedSize(true)
        dashboardFragmentViewModel.sprayedVineyards.observe(viewLifecycleOwner, { sprayedVineyardList ->
            binding.sprayRecyclerView.adapter = DashboardSprayAdapter(requireContext(), sprayedVineyardList)
        })

        dashboardFragmentViewModel.getSprayCount()

        dashboardFragmentViewModel.sprayCount.observe(viewLifecycleOwner, { sprayCount ->
            if (sprayCount == 1)
                binding.spraysThisMorning.text = "spray this morning"
            binding.sprayNumber.text = "$sprayCount"
        })

        dashboardFragmentViewModel.sprayedVineyardNames.observe(viewLifecycleOwner, { sprayText ->
            binding.vineyardPreviewText.text = sprayText
        })

        binding.sprayCardViewDashboard.setOnClickListener {
            sprayClosed = if (sprayClosed) {
                openSprayContent()
                false
            } else {
                // spray is open, need to close
                closeSprayContent()
                true
            }
        }
    }

    private fun closeSprayContent() {
        // setting isClickable causes the card view to flicker as if it was clicked so I set ripple to transparent
        binding.sprayCardViewDashboard.isClickable = false
        val arrowAnimation = getAnimationForArrow(binding.sprayArrow, 270F, 90F)

        // animate
        val contentAnimation = getAnimationForContent(binding.sprayContent, (300*density).toInt(), 0, DecelerateInterpolator())
        val animationSet = AnimatorSet()
        animationSet.playTogether(
            arrowAnimation,
            contentAnimation
        )
        animationSet.doOnEnd {
            binding.sprayContent.visibility = View.GONE
            binding.sprayCardViewDashboard.isClickable = true
        }
        changeConstraint(binding.todoCardViewDashboard.id, ConstraintSet.TOP, binding.sprayContent.id, ConstraintSet.BOTTOM, 8)
        animationSet.start()

    }

    private fun openSprayContent() {
        binding.sprayCardViewDashboard.isClickable = false
        val arrowAnimation = getAnimationForArrow(binding.sprayArrow, 90F, 270F)

        binding.sprayContent.visibility = View.VISIBLE

        // animate
        val animatorSet = AnimatorSet()
        val contentAnimation = getAnimationForContent(binding.sprayContent, 0, (300*density).toInt(), OvershootInterpolator())
        animatorSet.playTogether(
            arrowAnimation,
            contentAnimation
        )
        animatorSet.doOnEnd {
            changeConstraint(binding.todoCardViewDashboard.id, ConstraintSet.TOP, binding.sprayContent.id, ConstraintSet.BOTTOM, 5)
            binding.sprayCardViewDashboard.isClickable = true
        }
        animatorSet.start()
    }

    private fun getAnimationForContent(view: View, fromHeight: Int, toHeightInt: Int, interpolator: TimeInterpolator): ValueAnimator {
        val anim = ValueAnimator.ofInt(fromHeight, toHeightInt)
        anim.addUpdateListener { valueAnimator ->
            val newHeight = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = newHeight
            view.layoutParams = layoutParams
        }
        anim.duration = 300
        anim.interpolator = interpolator
        return anim
    }

    private fun getAnimationForArrow(icon: ImageView, startRotation: Float, endRotation: Float): ValueAnimator {
        val anim = ValueAnimator.ofFloat(startRotation, endRotation)
        anim.addUpdateListener { valueAnimator ->
            val newRotationValue = valueAnimator.animatedValue as Float
            icon.rotation = newRotationValue
        }
        anim.duration = 300
        return anim
    }

    private fun changeConstraint(fromViewId: Int, constraintSetFrom: Int, toViewId: Int, constraintSetTo: Int, margin: Int) {
        val mainConstraints = binding.mainConstraintLayout
        val newConstraint = ConstraintSet()
        newConstraint.clone(mainConstraints)
        newConstraint.connect(fromViewId, constraintSetFrom, toViewId, constraintSetTo, (margin*density).toInt())
        newConstraint.applyTo(mainConstraints)
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