package com.example.vino.ui.dashboard

import android.Manifest
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnResume
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentDashboardBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.ui.adapter.AlertRecyclerViewAdapter
import com.example.vino.ui.adapter.DashboardSprayAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private val dashboardFragmentViewModel: DashboardFragmentViewModel by viewModels {
        DashboardFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var sprayContentClosed = true
    private var weatherContentClosed = true
    private var density = 0F
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAccountAndCompanyName()
        setTodaysDate()
        setSprayInfo()
        setTodoInfo()
        setWeatherInfo()
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
            sprayContentClosed = if (sprayContentClosed) {
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
        val animationSet = getCloseContentAnimatorSet(binding.sprayCardViewDashboard, binding.sprayContent, binding.sprayArrow)
        changeConstraint(binding.todoCardViewDashboard.id, ConstraintSet.TOP, binding.sprayContent.id, ConstraintSet.BOTTOM, 8)
        animationSet.start()

    }

    private fun openSprayContent() {
        val animatorSet = getOpenContentAnimatorSet(binding.sprayCardViewDashboard, binding.sprayContent, binding.sprayArrow)
        animatorSet.doOnEnd {
            changeConstraint(binding.todoCardViewDashboard.id, ConstraintSet.TOP, binding.sprayContent.id, ConstraintSet.BOTTOM, 5)
            binding.sprayCardViewDashboard.isClickable = true
        }
        animatorSet.start()
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

    @SuppressLint("MissingPermission")
    private fun setWeatherInfo() {
        binding.weatherAlertCardView.setOnClickListener {
            weatherContentClosed = if (weatherContentClosed) {
                openWeatherContent()
                false
            } else {
                // spray is open, need to close
                closeWeatherContent()
                true
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                dashboardFragmentViewModel.getCurrentLocationAlerts(location.latitude, location.longitude)
            }
        } else {
            binding.weatherAlerts.text = ""
            binding.weatherAlertsText.text = "Location error"
        }

        binding.weatherRecyclerView.setHasFixedSize(true)

        dashboardFragmentViewModel.alerts.observe(viewLifecycleOwner, { alerts ->
           if (!(alerts.alerts.isNullOrEmpty())) {
               val numAlerts = alerts.alerts.size
               if (numAlerts == 1)
                   binding.weatherAlertsText.text = "weather alert"

               binding.weatherAlerts.text = "$numAlerts"
               binding.weatherPreview.text = alerts.alerts[0].event
               binding.weatherRecyclerView.adapter = AlertRecyclerViewAdapter(alerts.alerts)
           }
        })
    }

    private fun closeWeatherContent() {
        val animationSet = getCloseContentAnimatorSet(binding.weatherAlertCardView, binding.weatherContent, binding.weatherArrow)
        animationSet.start()
    }

    private fun openWeatherContent() {
        val animatorSet = getOpenContentAnimatorSet(binding.weatherAlertCardView, binding.weatherContent, binding.weatherArrow)
        animatorSet.doOnEnd {
            binding.weatherAlertCardView.isClickable = true
        }
        animatorSet.start()
    }

    private fun getAnimationForArrow(icon: ImageView, startRotation: Float, endRotation: Float, duration: Long): ValueAnimator {
        val anim = ValueAnimator.ofFloat(startRotation, endRotation)
        anim.addUpdateListener { valueAnimator ->
            val newRotationValue = valueAnimator.animatedValue as Float
            icon.rotation = newRotationValue
        }
        anim.duration = duration
        return anim
    }

    private fun getAnimationForContent(view: View, fromHeight: Int, toHeightInt: Int, interpolator: TimeInterpolator): ValueAnimator {
        val anim = ValueAnimator.ofInt(fromHeight, toHeightInt)
        anim.addUpdateListener { valueAnimator ->
            val newHeight = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = newHeight
            view.layoutParams = layoutParams
        }
        anim.duration = 400
        anim.interpolator = interpolator
        return anim
    }

    private fun changeConstraint(fromViewId: Int, constraintSetFrom: Int, toViewId: Int, constraintSetTo: Int, margin: Int) {
        val mainConstraints = binding.mainConstraintLayout
        val newConstraint = ConstraintSet()
        newConstraint.clone(mainConstraints)
        newConstraint.connect(fromViewId, constraintSetFrom, toViewId, constraintSetTo, (margin*density).toInt())
        newConstraint.applyTo(mainConstraints)
    }

    private fun getCloseContentAnimatorSet(mainCardView: MaterialCardView, content: MaterialCardView, arrow: ImageView): AnimatorSet {
        // setting isClickable causes the card view to flicker as if it was clicked so I set ripple to transparent
        mainCardView.isClickable = false
        val arrowAnimation = getAnimationForArrow(arrow, 270F, 90F, 300)

        // animate
        val animationSet = AnimatorSet()
        val contentAnimation = getAnimationForContent(content, (300*density).toInt(), 0, DecelerateInterpolator())
        animationSet.playTogether(
            arrowAnimation,
            contentAnimation
        )
        animationSet.doOnEnd {
            content.visibility = View.GONE
            mainCardView.isClickable = true
        }
        return animationSet
    }

    private fun getOpenContentAnimatorSet(mainCardView: MaterialCardView, content: MaterialCardView, arrow: ImageView): AnimatorSet {
        mainCardView.isClickable = false
        val arrowAnimation = getAnimationForArrow(arrow, 90F, 270F, 200)

        content.visibility = View.VISIBLE

        // animate
        val animatorSet = AnimatorSet()
        val contentAnimation = getAnimationForContent(content, 0, (300*density).toInt(), OvershootInterpolator())
        animatorSet.playTogether(
            arrowAnimation,
            contentAnimation
        )
        return animatorSet
    }
}
