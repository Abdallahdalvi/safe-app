/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.polls.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.widget.ImageView
import com.dalvi.safe.R
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.databinding.PollResultVoterItemBinding
import com.dalvi.safe.extensions.loadFederatedUserAvatar
import com.dalvi.safe.extensions.loadGuestAvatar
import com.dalvi.safe.extensions.loadUserAvatar
import com.dalvi.safe.models.json.participants.Participant
import com.dalvi.safe.polls.model.PollDetails
import com.dalvi.safe.ui.theme.ViewThemeUtils
import com.dalvi.safe.utils.DisplayUtils

class PollResultVoterViewHolder(
    private val user: User,
    private val roomToken: String,
    override val binding: PollResultVoterItemBinding,
    private val viewThemeUtils: ViewThemeUtils
) : PollResultViewHolder(binding) {

    @SuppressLint("SetTextI18n")
    override fun bind(pollResultItem: PollResultItem, clickListener: PollResultItemClickListener) {
        val item = pollResultItem as PollResultVoterItem

        binding.root.setOnClickListener { clickListener.onClick() }

        binding.pollVoterName.text = item.details.actorDisplayName
        loadAvatar(item.details, binding.pollVoterAvatar)
        viewThemeUtils.dialog.colorDialogSupportingText(binding.pollVoterName)
    }

    private fun loadAvatar(pollDetail: PollDetails, avatar: ImageView) {
        when (pollDetail.actorType) {
            Participant.ActorType.GUESTS -> {
                var displayName = SafeAppApplication.sharedApplication?.resources?.getString(R.string.nc_guest)
                if (!TextUtils.isEmpty(pollDetail.actorDisplayName)) {
                    displayName = pollDetail.actorDisplayName!!
                }
                avatar.loadGuestAvatar(user, displayName!!, false)
            }

            Participant.ActorType.FEDERATED -> {
                val darkTheme = if (DisplayUtils.isDarkModeOn(binding.root.context)) 1 else 0
                avatar.loadFederatedUserAvatar(
                    user,
                    user.baseUrl!!,
                    roomToken,
                    pollDetail.actorId!!,
                    darkTheme,
                    false,
                    false
                )
            }

            else -> {
                avatar.loadUserAvatar(user, pollDetail.actorId!!, false, false)
            }
        }
    }
}
