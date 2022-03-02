package com.example.demosocketio.ui.screen.home

import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demosocketio.R
import com.example.demosocketio.data.base.BaseFragment
import com.example.demosocketio.data.model.LoadDataStatus
import com.example.demosocketio.data.response.DataResponse
import com.example.demosocketio.databinding.FragmentHomeBinding
import com.example.demosocketio.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {

        getDynamicLink()

        binding!!.edtName.doAfterTextChanged {
            viewModel.updateUserName(it.toString())
        }

        binding!!.edtRoom.doAfterTextChanged {
            viewModel.updateRoomName(it.toString())
        }

        binding!!.btnDeeplink.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Constants.URL_DEEPLINK)
            )
            startActivity(browserIntent)
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
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.empty_input),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(requireActivity().intent)
            .addOnSuccessListener(
                requireActivity()
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null)
                    Log.d("TAG", "getDynamicLink: $deepLink")

            }
            .addOnFailureListener(
                requireActivity()
            ) { e -> Log.d("TAG", "getDynamicLink:onFailure", e) }
    }
}