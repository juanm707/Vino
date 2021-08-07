package com.example.vino.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import coil.memory.MemoryCache
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentHomeBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.ui.adapter.VineyardGridAdapter
import com.google.android.material.card.MaterialCardView
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), VineyardGridAdapter.OnVineyardListener {
    //TODO: Use list adapter with diff  util
    private val homeFragmentViewModel: HomeFragmentViewModel by viewModels {
        HomeFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private lateinit var userVineyards: List<Vineyard>
    private lateinit var adapter: VineyardGridAdapter

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //postponeEnterTransition()

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
        vineyardId: Int,
        vineyardCardView: MaterialCardView,
        vineyardLinearLayout: LinearLayout,
        vineyardName: TextView,
        vineyardJob: TextView,
        vineyardImage: ImageView,
        imageCacheKey: MemoryCache.Key?
    ) {

        val extras = FragmentNavigatorExtras(
            vineyardCardView to vineyardCardView.transitionName,
            vineyardLinearLayout to vineyardLinearLayout.transitionName,
            vineyardName to vineyardName.transitionName,
            vineyardImage to vineyardImage.transitionName,
            vineyardJob to vineyardJob.transitionName
        )

        vinoUserModel.selectedVineyard = homeFragmentViewModel.getVineyardById(vineyardId)
        vinoUserModel.imageCacheKey = imageCacheKey

        val action = HomeFragmentDirections.actionNavigationHomeToVineyardDetailFragment(vineyardId)
        findNavController().navigate(action, extras)
    }

    private fun setUpRecyclerView() {
        (view?.parent as? ViewGroup)?.doOnPreDraw {
            //startPostponedEnterTransition()
        }

        binding.vineyardRecyclerView.visibility = View.INVISIBLE // to show progress circle
        binding.vineyardRecyclerView.itemAnimator = SlideInRightAnimator()
        adapter = VineyardGridAdapter(requireContext(), this@HomeFragment)
        binding.vineyardRecyclerView.adapter = adapter
        binding.vineyardRecyclerView.setHasFixedSize(true)
        // will update after get user request
        vinoUserModel.vinoUser.observe(viewLifecycleOwner, { user ->
            setAccountName("${user.firstName} ${user.lastName}", user.company)

            // Now that we have a user, we can get the vineyards
            homeFragmentViewModel.refreshVineyards(user.userId)
        })

        homeFragmentViewModel.vineyards.observe(viewLifecycleOwner, { vineyards ->
            binding.vineyardRecyclerView.visibility = View.VISIBLE

            // on cpu thread, but not really needed since not a lot of vineyards
            lifecycleScope.launch(Dispatchers.Default) {
                userVineyards = homeFragmentViewModel.sortVineyardsByName(vineyards)

                activity?.runOnUiThread {
                    adapter.submitList(userVineyards)
                }
            }
        })


    }

    private fun setAccountName(userName: String, companyName: String) {
        setTextAndVisibility(binding.accountName, userName)
        setTextAndVisibility(binding.companyName, companyName)
    }

    private fun setTextAndVisibility(textView: TextView, newText: String) {
        textView.apply {
            text = newText
            visibility = View.VISIBLE
        }
    }

    private fun setUpConnectionImageAndText() {
        // on api status, either way DONE or ERROR, hide loading
        vinoUserModel.apiStatus.observe(viewLifecycleOwner, {
            if (it != VinoApiStatus.LOADING)
                binding.progressCircular.hide()
            if (it == VinoApiStatus.ERROR) {
                binding.connectionStatusImage.visibility = View.VISIBLE
                binding.connectionStatusText.visibility = View.VISIBLE
            }
            // if DONE hide text
        })
    }
}
