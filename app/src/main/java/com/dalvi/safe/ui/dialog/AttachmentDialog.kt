/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2021 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-FileCopyrightText: 2021 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.ui.dialog

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import autodagger.AutoInjector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.dalvi.safe.R
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.chat.ChatActivity
import com.dalvi.safe.databinding.DialogAttachmentBinding
import com.dalvi.safe.ui.theme.ViewThemeUtils
import com.dalvi.safe.utils.CapabilitiesUtil
import com.dalvi.safe.utils.SpreedFeatures
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class AttachmentDialog(val activity: Activity, var chatActivity: ChatActivity) : BottomSheetDialog(activity) {

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    private lateinit var dialogAttachmentBinding: DialogAttachmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SafeAppApplication.sharedApplication?.componentApplication?.inject(this)

        dialogAttachmentBinding = DialogAttachmentBinding.inflate(layoutInflater)
        setContentView(dialogAttachmentBinding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        viewThemeUtils.material.colorBottomSheetBackground(dialogAttachmentBinding.root)
        viewThemeUtils.material.colorBottomSheetDragHandle(dialogAttachmentBinding.bottomSheetDragHandle)
        initItemsStrings()
        initItemsVisibility()
        initItemsClickListeners()
    }

    private fun initItemsStrings() {
        var serverName = CapabilitiesUtil.getServerName(chatActivity.conversationUser)
        dialogAttachmentBinding.txtAttachFileFromCloud.text = chatActivity.resources?.let {
            if (serverName.isNullOrEmpty()) {
                serverName = it.getString(R.string.nc_server_product_name)
            }
            String.format(it.getString(R.string.nc_upload_from_cloud), serverName)
        }
    }

    private fun initItemsVisibility() {
        if (!chatActivity.currentConversation!!.remoteServer.isNullOrEmpty()) {
            dialogAttachmentBinding.menuAttachContact.visibility = View.GONE
            dialogAttachmentBinding.menuShareLocation.visibility = View.GONE
            dialogAttachmentBinding.menuAttachPictureFromCam.visibility = View.GONE
            dialogAttachmentBinding.menuAttachVideoFromCam.visibility = View.GONE
            dialogAttachmentBinding.menuAttachFileFromLocal.visibility = View.GONE
            dialogAttachmentBinding.menuAttachFileFromCloud.visibility = View.GONE
        }

        if (!CapabilitiesUtil.hasSpreedFeatureCapability(
                chatActivity.spreedCapabilities,
                SpreedFeatures.GEO_LOCATION_SHARING
            )
        ) {
            dialogAttachmentBinding.menuShareLocation.visibility = View.GONE
        }

        if (!CapabilitiesUtil.hasSpreedFeatureCapability(chatActivity.spreedCapabilities, SpreedFeatures.TALK_POLLS) ||
            chatActivity.isOneToOneConversation()
        ) {
            dialogAttachmentBinding.menuAttachPoll.visibility = View.GONE
        }

        if (!CapabilitiesUtil.hasSpreedFeatureCapability(
                chatActivity.spreedCapabilities,
                SpreedFeatures.THREADS
            ) ||
            chatActivity.conversationThreadId != null
        ) {
            dialogAttachmentBinding.menuCreateThread.visibility = View.GONE
        }

        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            dialogAttachmentBinding.menuAttachVideoFromCam.visibility = View.GONE
        }
    }

    private fun initItemsClickListeners() {
        dialogAttachmentBinding.menuShareLocation.setOnClickListener {
            chatActivity.showShareLocationScreen()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachFileFromGallery.setOnClickListener {
            chatActivity.showGalleryPicker()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachFileFromLocal.setOnClickListener {
            chatActivity.sendSelectLocalFileIntent()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachPictureFromCam.setOnClickListener {
            chatActivity.sendPictureFromCamIntent()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachVideoFromCam.setOnClickListener {
            chatActivity.sendVideoFromCamIntent()
            dismiss()
        }

        dialogAttachmentBinding.menuCreateThread.setOnClickListener {
            chatActivity.createThread()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachPoll.setOnClickListener {
            chatActivity.createPoll()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachFileFromCloud.setOnClickListener {
            chatActivity.showBrowserScreen()
            dismiss()
        }

        dialogAttachmentBinding.menuAttachContact.setOnClickListener {
            chatActivity.sendChooseContactIntent()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet as View)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}
