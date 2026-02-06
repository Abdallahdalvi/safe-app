/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.dalvi.safe.R
import com.dalvi.safe.application.SafeAppApplication.Companion.sharedApplication
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.databinding.ReactionItemBinding
import com.dalvi.safe.extensions.loadGuestAvatar
import com.dalvi.safe.extensions.loadUserAvatar
import com.dalvi.safe.models.json.reactions.ReactionVoter

class ReactionsViewHolder(private val binding: ReactionItemBinding, private val user: User?) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(reactionItem: ReactionItem, clickListener: ReactionItemClickListener) {
        binding.root.setOnClickListener { clickListener.onClick(reactionItem) }
        binding.reaction.text = reactionItem.reaction
        binding.name.text = reactionItem.reactionVoter.actorDisplayName

        if (user != null && user.baseUrl?.isNotEmpty() == true) {
            loadAvatar(reactionItem)
        }
    }

    private fun loadAvatar(reactionItem: ReactionItem) {
        if (reactionItem.reactionVoter.actorType == ReactionVoter.ReactionActorType.GUESTS) {
            var displayName = sharedApplication?.resources?.getString(R.string.nc_guest)
            if (!TextUtils.isEmpty(reactionItem.reactionVoter.actorDisplayName)) {
                displayName = reactionItem.reactionVoter.actorDisplayName!!
            }
            binding.avatar.loadGuestAvatar(user!!.baseUrl!!, displayName!!, false)
        } else if (reactionItem.reactionVoter.actorType == ReactionVoter.ReactionActorType.USERS) {
            binding.avatar.loadUserAvatar(
                user!!,
                reactionItem.reactionVoter.actorId!!,
                false,
                false
            )
        }
    }
}
