package com.example.vino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import coil.memory.MemoryCache
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentHomeBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.VinoApiStatus
import com.example.vino.model.Vineyard
import com.example.vino.ui.adapter.VineyardGridAdapter
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), VineyardGridAdapter.OnVineyardListener {
    //TODO: Use list adapter with diff  util

    private var _binding: FragmentHomeBinding? = null
    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }
    private lateinit var userVineyards: List<Vineyard>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.userViewModel = vinoUserModel

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // text invisible until actual user (if) available
        binding.companyName.visibility = View.INVISIBLE
        binding.accountName.visibility = View.INVISIBLE

        setUpRecyclerView()
        setUpConnectionImageAndText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onVineyardClick(
        position: Int,
        vineyard: String,
        vineyardCardView: MaterialCardView,
        vineyardLinearLayout: LinearLayout,
        vineyardName: TextView,
        vineyardImage: ImageView,
        temperature: TextView,
        humidity: TextView,
        imageCacheKey: MemoryCache.Key?
    ) {
        //Toast.makeText(requireContext(), vineyard, Toast.LENGTH_SHORT).show()

        val extras = FragmentNavigatorExtras(
            vineyardCardView to vineyardCardView.transitionName,
            vineyardLinearLayout to vineyardLinearLayout.transitionName,
            vineyardName to vineyardName.transitionName,
            vineyardImage to vineyardImage.transitionName,
            temperature to temperature.transitionName,
            humidity to humidity.transitionName,
        )
        val action = HomeFragmentDirections.actionNavigationHomeToBlankFragment(vineyard, imageCacheKey)
        findNavController().navigate(action, extras)
    }

    private fun setUpRecyclerView() {
        binding.vineyardRecyclerView.visibility = View.INVISIBLE // to show progress circle

        // will update after get user request
        vinoUserModel.vinoUser.observe(viewLifecycleOwner, {
            setAccountName(it.head.name, it.userName)

            binding.progressCircular.hide() // hide progress once user is grabbed
            binding.vineyardRecyclerView.visibility = View.VISIBLE

            // on cpu thread, but not really needed since not a lot of vineyards
            lifecycleScope.launch(Dispatchers.Default) {
                userVineyards = it.vineyards.sortedBy { vineyard ->
                    vineyard.name
                }
                activity?.runOnUiThread {
                    binding.vineyardRecyclerView.adapter = VineyardGridAdapter(userVineyards, requireContext(), this@HomeFragment) // Todo change with list adapter, submit list
                    binding.vineyardRecyclerView.setHasFixedSize(true)
                    binding.vineyardRecyclerView.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
            }
        })
    }

    private fun setAccountName(userName: String, companyName: String) {
        binding.accountName.apply {
            visibility = View.VISIBLE
            text = userName
        }
        binding.companyName.apply {
            visibility = View.VISIBLE
            text = companyName
        }
    }

    private fun setUpConnectionImageAndText() {
        // on api status, either way DONE or ERROR, hide loading
        vinoUserModel.status.observe(viewLifecycleOwner, {
            if (it != VinoApiStatus.LOADING)
                binding.progressCircular.hide()
            if (it == VinoApiStatus.ERROR)
                binding.connectionStatusText.visibility = View.VISIBLE
            // if DONE hide text
        })
    }
}