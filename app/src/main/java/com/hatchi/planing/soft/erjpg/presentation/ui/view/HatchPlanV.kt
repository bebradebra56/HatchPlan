package com.hatchi.planing.soft.erjpg.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hatchi.planing.soft.erjpg.presentation.app.HatchPlanApplication
import com.hatchi.planing.soft.erjpg.presentation.ui.load.HatchPlanLoadFragment
import org.koin.android.ext.android.inject

class HatchPlanV : Fragment(){

    private lateinit var hatchPlanPhoto: Uri
    private var hatchPlanFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val hatchPlanTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        hatchPlanFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        hatchPlanFilePathFromChrome = null
    }

    private val hatchPlanTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            hatchPlanFilePathFromChrome?.onReceiveValue(arrayOf(hatchPlanPhoto))
            hatchPlanFilePathFromChrome = null
        } else {
            hatchPlanFilePathFromChrome?.onReceiveValue(null)
            hatchPlanFilePathFromChrome = null
        }
    }

    private val hatchPlanDataStore by activityViewModels<HatchPlanDataStore>()


    private val hatchPlanViFun by inject<HatchPlanViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (hatchPlanDataStore.hatchPlanView.canGoBack()) {
                        hatchPlanDataStore.hatchPlanView.goBack()
                        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "WebView can go back")
                    } else if (hatchPlanDataStore.hatchPlanViList.size > 1) {
                        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "WebView can`t go back")
                        hatchPlanDataStore.hatchPlanViList.removeAt(hatchPlanDataStore.hatchPlanViList.lastIndex)
                        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "WebView list size ${hatchPlanDataStore.hatchPlanViList.size}")
                        hatchPlanDataStore.hatchPlanView.destroy()
                        val previousWebView = hatchPlanDataStore.hatchPlanViList.last()
                        hatchPlanAttachWebViewToContainer(previousWebView)
                        hatchPlanDataStore.hatchPlanView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (hatchPlanDataStore.hatchPlanIsFirstCreate) {
            hatchPlanDataStore.hatchPlanIsFirstCreate = false
            hatchPlanDataStore.hatchPlanContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return hatchPlanDataStore.hatchPlanContainerView
        } else {
            return hatchPlanDataStore.hatchPlanContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "onViewCreated")
        if (hatchPlanDataStore.hatchPlanViList.isEmpty()) {
            hatchPlanDataStore.hatchPlanView = HatchPlanVi(requireContext(), object :
                HatchPlanCallBack {
                override fun hatchPlanHandleCreateWebWindowRequest(hatchPlanVi: HatchPlanVi) {
                    hatchPlanDataStore.hatchPlanViList.add(hatchPlanVi)
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "WebView list size = ${hatchPlanDataStore.hatchPlanViList.size}")
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "CreateWebWindowRequest")
                    hatchPlanDataStore.hatchPlanView = hatchPlanVi
                    hatchPlanVi.hatchPlanSetFileChooserHandler { callback ->
                        hatchPlanHandleFileChooser(callback)
                    }
                    hatchPlanAttachWebViewToContainer(hatchPlanVi)
                }

            }, hatchPlanWindow = requireActivity().window).apply {
                hatchPlanSetFileChooserHandler { callback ->
                    hatchPlanHandleFileChooser(callback)
                }
            }
            hatchPlanDataStore.hatchPlanView.hatchPlanFLoad(arguments?.getString(HatchPlanLoadFragment.HATCH_PLAN_D) ?: "")
//            ejvview.fLoad("www.google.com")
            hatchPlanDataStore.hatchPlanViList.add(hatchPlanDataStore.hatchPlanView)
            hatchPlanAttachWebViewToContainer(hatchPlanDataStore.hatchPlanView)
        } else {
            hatchPlanDataStore.hatchPlanViList.forEach { webView ->
                webView.hatchPlanSetFileChooserHandler { callback ->
                    hatchPlanHandleFileChooser(callback)
                }
            }
            hatchPlanDataStore.hatchPlanView = hatchPlanDataStore.hatchPlanViList.last()

            hatchPlanAttachWebViewToContainer(hatchPlanDataStore.hatchPlanView)
        }
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "WebView list size = ${hatchPlanDataStore.hatchPlanViList.size}")
    }

    private fun hatchPlanHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        hatchPlanFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Launching file picker")
                    hatchPlanTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "Launching camera")
                    hatchPlanPhoto = hatchPlanViFun.hatchPlanSavePhoto()
                    hatchPlanTakePhoto.launch(hatchPlanPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(HatchPlanApplication.HATCH_PLAN_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                hatchPlanFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun hatchPlanAttachWebViewToContainer(w: HatchPlanVi) {
        hatchPlanDataStore.hatchPlanContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            hatchPlanDataStore.hatchPlanContainerView.removeAllViews()
            hatchPlanDataStore.hatchPlanContainerView.addView(w)
        }
    }


}