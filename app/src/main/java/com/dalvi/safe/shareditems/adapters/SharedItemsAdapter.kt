/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Tim Krüger <t@timkrueger.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.shareditems.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.databinding.SharedItemGridBinding
import com.dalvi.safe.databinding.SharedItemListBinding
import com.dalvi.safe.polls.ui.PollMainDialogFragment
import com.dalvi.safe.shareditems.activities.SharedItemsActivity
import com.dalvi.safe.shareditems.model.SharedDeckCardItem
import com.dalvi.safe.shareditems.model.SharedFileItem
import com.dalvi.safe.shareditems.model.SharedItem
import com.dalvi.safe.shareditems.model.SharedLocationItem
import com.dalvi.safe.shareditems.model.SharedOtherItem
import com.dalvi.safe.shareditems.model.SharedPinnedItem
import com.dalvi.safe.shareditems.model.SharedPollItem
import com.dalvi.safe.ui.theme.ViewThemeUtils
import com.dalvi.safe.utils.ApiUtils
import java.util.Collections.emptyList

class SharedItemsAdapter(
    private val showGrid: Boolean,
    private val user: User,
    private val roomToken: String,
    private val isUserConversationOwnerOrModerator: Boolean,
    private val isOne2One: Boolean,
    private val viewThemeUtils: ViewThemeUtils
) : RecyclerView.Adapter<SharedItemsViewHolder>() {

    var items: MutableList<SharedItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedItemsViewHolder =
        if (showGrid) {
            SharedItemsGridViewHolder(
                SharedItemGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                user,
                viewThemeUtils
            )
        } else {
            SharedItemsListViewHolder(
                SharedItemListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                user,
                viewThemeUtils
            )
        }

    override fun onBindViewHolder(holder: SharedItemsViewHolder, position: Int) {
        when (val item = items[position]) {
            is SharedPollItem -> holder.onBind(item, ::showPoll)
            is SharedFileItem -> holder.onBind(item)
            is SharedLocationItem -> holder.onBind(item)
            is SharedOtherItem -> holder.onBind(item)
            is SharedDeckCardItem -> holder.onBind(item)
            is SharedPinnedItem -> holder.onBind(item, ::openMessage, ::unpinMessage)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun showPoll(item: SharedItem, context: Context) {
        val pollVoteDialog = PollMainDialogFragment.newInstance(
            user,
            roomToken,
            isUserConversationOwnerOrModerator,
            item.id,
            item.name
        )
        pollVoteDialog.show(
            (context as SharedItemsActivity).supportFragmentManager,
            TAG
        )
    }

    private fun unpinMessage(item: SharedItem, context: Context) {
        val credentials = ApiUtils.getCredentials(user.username, user.token)
        val url = ApiUtils.getUrlForChatMessagePinning(1, user.baseUrl, roomToken, item.id)

        val canPin = isOne2One || isUserConversationOwnerOrModerator
        if (canPin) {
            credentials?.let {
                (context as SharedItemsActivity).chatViewModel.unPinMessage(credentials, url)
                val index = items.indexOf(item)
                items.remove(item)
                this.notifyItemRemoved(index)
            }
        }
    }

    private fun openMessage(item: SharedItem, context: Context) {
        val credentials = ApiUtils.getCredentials(user.username, user.token)
        val baseUrl = user.baseUrl
        (context as SharedItemsActivity).startContextChatWindowForMessage(
            credentials,
            baseUrl,
            roomToken,
            item.id,
            null
        )
    }

    companion object {
        private val TAG = SharedItemsAdapter::class.simpleName
    }
}
