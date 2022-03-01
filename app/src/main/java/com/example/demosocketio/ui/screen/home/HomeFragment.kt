package com.example.demosocketio.ui.screen.home

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demosocketio.R
import com.example.demosocketio.data.base.BaseFragment
import com.example.demosocketio.data.response.DataResponse
import com.example.demosocketio.databinding.FragmentHomeBinding
import androidx.navigation.fragment.navArgs

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {

        binding!!.btnEnter.setOnClickListener {
            if (binding!!.edtName.text.isNullOrEmpty() || binding!!.edtRoom.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "empty room", Toast.LENGTH_SHORT).show()
            } else {
                val action = HomeFragmentDirections.actionGlobalChatFragment(
                    binding!!.edtName.text.toString(),
                    binding!!.edtRoom.text.toString()
                )
                findNavController().navigate(action)
            }
        }

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel

    }

    override fun initViewModel() {
        val factory = HomeViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

    }
}