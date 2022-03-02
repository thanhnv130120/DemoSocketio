package com.example.demosocketio.ui.screen.home

import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demosocketio.R
import com.example.demosocketio.data.base.BaseFragment
import com.example.demosocketio.data.model.LoadDataStatus
import com.example.demosocketio.data.response.DataResponse
import com.example.demosocketio.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {

        binding!!.edtName.doAfterTextChanged {
            viewModel.updateUserName(it.toString())
        }

        binding!!.edtRoom.doAfterTextChanged {
            viewModel.updateRoomName(it.toString())
        }

        binding!!.btnDeeplink.setOnClickListener {

        }

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel

    }

    override fun initViewModel() {
        val factory = HomeViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.saveDoneLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                when ((it as DataResponse.DataSuccessResponse).body) {
                    HomeViewModel.ValidateType.ValidateDone -> {
                        val action = HomeFragmentDirections.actionGlobalChatFragment(
                            binding!!.edtName.text.toString(),
                            binding!!.edtRoom.text.toString()
                        )
                        findNavController().navigate(action)
                    }
                    else -> {
                        Toast.makeText(requireContext(), getString(R.string.empty_input), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}