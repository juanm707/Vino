package com.example.vino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.vino.databinding.FragmentHomeBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.VinoApiStatus
import com.example.vino.network.Vineyard
import com.example.vino.network.VineyardManagerUser
import com.example.vino.ui.adapter.VineyardGridAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), VineyardGridAdapter.OnVineyardListener {
    //TODO: Use list adapter with diff  util

    private var user: VineyardManagerUser? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val vinoUserModel: UserViewModel by activityViewModels()
    private lateinit var userVineyards: List<Vineyard>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.userViewModel = vinoUserModel

        return binding.root
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

    override fun onVineyardClick(position: Int) {
        Toast.makeText(requireContext(), userVineyards[position].name, Toast.LENGTH_SHORT).show()
    }

    private fun setUpRecyclerView() {
        binding.vineyardRecyclerView.visibility = View.INVISIBLE // to show progress circle

        // will update after get user request
        vinoUserModel.vinoUser.observe(viewLifecycleOwner, {
            setAccountName(it.head.name, it.userName)

            binding.progressCircular.hide() // hide progress once user is grabbed
            binding.vineyardRecyclerView.visibility = View.VISIBLE

            // on cpu thread, but not really needed since not a lot of vineyards
            lifecycleScope.launch {
                userVineyards = it.vineyards.sortedBy { vineyard ->
                    vineyard.name
                }
                binding.vineyardRecyclerView.adapter = VineyardGridAdapter(userVineyards, requireContext(), this@HomeFragment)
                binding.vineyardRecyclerView.setHasFixedSize(true)
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