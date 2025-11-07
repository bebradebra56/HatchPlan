package com.hatchi.planing.soft.erjpg.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hatchi.planing.soft.MainActivity
import com.hatchi.planing.soft.R
import com.hatchi.planing.soft.databinding.FragmentLoadHatchPlanBinding
import com.hatchi.planing.soft.erjpg.data.shar.HatchPlanSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HatchPlanLoadFragment : Fragment(R.layout.fragment_load_hatch_plan) {
    private lateinit var hatchPlanLoadBinding: FragmentLoadHatchPlanBinding

    private val hatchPlanLoadViewModel by viewModel<HatchPlanLoadViewModel>()

    private val hatchPlanSharedPreference by inject<HatchPlanSharedPreference>()

    private var hatchPlanUrl = ""

    private val hatchPlanRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            hatchPlanNavigateToSuccess(hatchPlanUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                hatchPlanSharedPreference.hatchPlanNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                hatchPlanNavigateToSuccess(hatchPlanUrl)
            } else {
                hatchPlanNavigateToSuccess(hatchPlanUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hatchPlanLoadBinding = FragmentLoadHatchPlanBinding.bind(view)

        hatchPlanLoadBinding.hatchPlanGrandButton.setOnClickListener {
            val hatchPlanPermission = Manifest.permission.POST_NOTIFICATIONS
            hatchPlanRequestNotificationPermission.launch(hatchPlanPermission)
            hatchPlanSharedPreference.hatchPlanNotificationRequestedBefore = true
        }

        hatchPlanLoadBinding.hatchPlanSkipButton.setOnClickListener {
            hatchPlanSharedPreference.hatchPlanNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            hatchPlanNavigateToSuccess(hatchPlanUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                hatchPlanLoadViewModel.hatchPlanHomeScreenState.collect {
                    when (it) {
                        is HatchPlanLoadViewModel.HatchPlanHomeScreenState.HatchPlanLoading -> {

                        }

                        is HatchPlanLoadViewModel.HatchPlanHomeScreenState.HatchPlanError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is HatchPlanLoadViewModel.HatchPlanHomeScreenState.HatchPlanSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val hatchPlanPermission = Manifest.permission.POST_NOTIFICATIONS
                                val hatchPlanPermissionRequestedBefore = hatchPlanSharedPreference.hatchPlanNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), hatchPlanPermission) == PackageManager.PERMISSION_GRANTED) {
                                    hatchPlanNavigateToSuccess(it.data)
                                } else if (!hatchPlanPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > hatchPlanSharedPreference.hatchPlanNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    hatchPlanLoadBinding.hatchPlanNotiGroup.visibility = View.VISIBLE
                                    hatchPlanLoadBinding.hatchPlanLoadingGroup.visibility = View.GONE
                                    hatchPlanUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(hatchPlanPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > hatchPlanSharedPreference.hatchPlanNotificationRequest) {
                                        hatchPlanLoadBinding.hatchPlanNotiGroup.visibility = View.VISIBLE
                                        hatchPlanLoadBinding.hatchPlanLoadingGroup.visibility = View.GONE
                                        hatchPlanUrl = it.data
                                    } else {
                                        hatchPlanNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    hatchPlanNavigateToSuccess(it.data)
                                }
                            } else {
                                hatchPlanNavigateToSuccess(it.data)
                            }
                        }

                        HatchPlanLoadViewModel.HatchPlanHomeScreenState.HatchPlanNotInternet -> {
                            hatchPlanLoadBinding.hatchPlanStateGroup.visibility = View.VISIBLE
                            hatchPlanLoadBinding.hatchPlanLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun hatchPlanNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_hatchPlanLoadFragment_to_hatchPlanV,
            bundleOf(HATCH_PLAN_D to data)
        )
    }

    companion object {
        const val HATCH_PLAN_D = "hatchPlanData"
    }
}