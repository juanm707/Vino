package com.example.vino.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vino.databinding.FragmentHomeBinding
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApi
import com.example.vino.ui.adapter.VineyardGridAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    //TODO: Use list adapter with diff  util
    //TODO: Use view model and move request to view model

    private var user: VineyardManagerUser? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            user = getVineyardManagerUser()
            if (user == null) {
                //updateUIWithError()
            } else if (user != null){
                binding.accountName.text = user!!.head.name
                binding.companyName.text = user!!.userName // change userName to other variable -> company?
                binding.vineyardRecyclerView.adapter = VineyardGridAdapter(user!!.vineyards)
                binding.vineyardRecyclerView.setHasFixedSize(true)
            }
        }

    }

    private suspend fun getVineyardManagerUser(): VineyardManagerUser? {
        return withContext(Dispatchers.IO) {
            try {
                VinoApi.retrofitService.getUser()
            } catch (exception: Exception) {
                Log.d("NetworkError", "$exception handled!")
                return@withContext null
            }
        }
    }
}