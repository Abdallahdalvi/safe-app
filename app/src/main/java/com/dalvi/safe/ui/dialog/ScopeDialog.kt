/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2021 Tobias Kaminsky <tobias.kaminsky@nextcloud.com>
 * SPDX-FileCopyrightText: 2021 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import autodagger.AutoInjector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.dalvi.safe.R
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.databinding.DialogScopeBinding
import com.dalvi.safe.models.json.userprofile.Scope
import com.dalvi.safe.profile.ProfileActivity
import com.dalvi.safe.ui.theme.ViewThemeUtils
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class ScopeDialog(
    con: Context,
    private val userInfoAdapter: ProfileActivity.UserInfoAdapter,
    private val field: ProfileActivity.Field,
    private val position: Int
) : BottomSheetDialog(con) {

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    private lateinit var dialogScopeBinding: DialogScopeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SafeAppApplication.sharedApplication?.componentApplication?.inject(this)

        dialogScopeBinding = DialogScopeBinding.inflate(layoutInflater)
        setContentView(dialogScopeBinding.root)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        viewThemeUtils.platform.themeDialog(dialogScopeBinding.root)

        if (field == ProfileActivity.Field.DISPLAYNAME || field == ProfileActivity.Field.EMAIL) {
            dialogScopeBinding.scopePrivate.visibility = View.GONE
        }

        dialogScopeBinding.scopePrivate.setOnClickListener {
            userInfoAdapter.updateScope(position, Scope.PRIVATE)
            dismiss()
        }

        dialogScopeBinding.scopeLocal.setOnClickListener {
            userInfoAdapter.updateScope(position, Scope.LOCAL)
            dismiss()
        }

        dialogScopeBinding.scopeFederated.setOnClickListener {
            userInfoAdapter.updateScope(position, Scope.FEDERATED)
            dismiss()
        }

        dialogScopeBinding.scopePublished.setOnClickListener {
            userInfoAdapter.updateScope(position, Scope.PUBLISHED)
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
