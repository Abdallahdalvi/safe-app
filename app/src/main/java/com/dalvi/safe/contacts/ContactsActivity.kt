/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-FileCopyrightText: 2025 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodagger.AutoInjector
import com.dalvi.safe.activities.BaseActivity
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.components.ColoredStatusBar
import com.dalvi.safe.contacts.CompanionClass.Companion.KEY_HIDE_ALREADY_EXISTING_PARTICIPANTS
import com.dalvi.safe.extensions.getParcelableArrayListExtraProvider
import com.dalvi.safe.models.json.autocomplete.AutocompleteUser
import com.dalvi.safe.utils.bundle.BundleKeys
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class ContactsActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactsViewModel: ContactsViewModel

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SafeAppApplication.sharedApplication!!.componentApplication.inject(this)
        contactsViewModel = ViewModelProvider(this, viewModelFactory)[ContactsViewModel::class.java]
        setContent {
            val isAddParticipants = intent.getBooleanExtra(BundleKeys.KEY_ADD_PARTICIPANTS, false)
            val hideAlreadyAddedParticipants = intent.getBooleanExtra(KEY_HIDE_ALREADY_EXISTING_PARTICIPANTS, false)
            contactsViewModel.getContactsFromSearchParams()
            contactsViewModel.updateIsAddParticipants(isAddParticipants)
            contactsViewModel.hideAlreadyAddedParticipants(hideAlreadyAddedParticipants)
            if (isAddParticipants) {
                contactsViewModel.updateShareTypes(
                    listOf(
                        ShareType.Group.shareType,
                        ShareType.Email.shareType,
                        ShareType.Circle.shareType
                    )
                )
                contactsViewModel.getContactsFromSearchParams()
            }
            val colorScheme = viewThemeUtils.getColorScheme(this)
            val uiState = contactsViewModel.contactsViewState.collectAsStateWithLifecycle()

            val selectedParticipants = remember {
                intent?.getParcelableArrayListExtraProvider<AutocompleteUser>("selectedParticipants")
                    ?: emptyList()
            }.toSet().toMutableList()
            contactsViewModel.updateSelectedParticipants(selectedParticipants)

            MaterialTheme(
                colorScheme = colorScheme
            ) {
                ColoredStatusBar()
                ContactsScreen(
                    contactsViewModel = contactsViewModel,
                    uiState = uiState.value
                )
            }
        }
    }
}

class CompanionClass {
    companion object {
        internal val TAG = ContactsActivity::class.simpleName
        internal const val ROOM_TYPE_ONE_ONE = "1"
        const val KEY_HIDE_ALREADY_EXISTING_PARTICIPANTS: String = "KEY_HIDE_ALREADY_EXISTING_PARTICIPANTS"
    }
}
