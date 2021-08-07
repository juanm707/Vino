package com.example.vino

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.vino.databinding.FragmentSettingsBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory

class SettingsDialogFragment : DialogFragment() {

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpSettings()
        binding.mainConstraintLayout.alpha = 0f
        binding.mainConstraintLayout.visibility = View.VISIBLE
        getFadeViewAnimation(0f, 1f, binding.mainConstraintLayout)
            .start()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG = "settings_dialog"
        fun display(fragmentManager: FragmentManager): SettingsDialogFragment {
            val settingsDialogFragment = SettingsDialogFragment()
            settingsDialogFragment.show(fragmentManager, TAG)
            return settingsDialogFragment
        }
    }

    private fun setUpToolbar() {
        binding.toolbarSettings.setNavigationOnClickListener {
            dismiss()
        }
        binding.toolbarSettings.title = "Settings"
        binding.toolbarSettings.menu.clear()
        binding.toolbarSettings.inflateMenu(R.menu.settings_menu)
        binding.toolbarSettings.setOnMenuItemClickListener { item: MenuItem? ->
            if (item != null) {
                if (item.itemId == R.id.action_save)
                    Toast.makeText(requireContext(), "Saved settings", Toast.LENGTH_SHORT).show()
            }
            dismiss()
            true
        }
    }

    private fun setUpSettings() {
        vinoUserModel.vinoUser.observe(viewLifecycleOwner, { user ->
            binding.name.text = "${user.firstName} ${user.lastName}"
            binding.company.text = "${user.company}"
        })
    }

    private fun getFadeViewAnimation(startAlpha: Float, endAlpha: Float, view: View): ValueAnimator {
        val anim = ValueAnimator.ofFloat(startAlpha, endAlpha)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            view.alpha = value
        }
        anim.duration = 500
        return anim
    }
}