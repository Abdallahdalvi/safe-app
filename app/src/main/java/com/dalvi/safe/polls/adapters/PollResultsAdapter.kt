/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.polls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.databinding.PollResultHeaderItemBinding
import com.dalvi.safe.databinding.PollResultVoterItemBinding
import com.dalvi.safe.databinding.PollResultVotersOverviewItemBinding
import com.dalvi.safe.ui.theme.ViewThemeUtils

class PollResultsAdapter(
    private val user: User,
    private val roomToken: String,
    private val clickListener: PollResultItemClickListener,
    private val viewThemeUtils: ViewThemeUtils
) : RecyclerView.Adapter<PollResultViewHolder>() {
    internal var list: MutableList<PollResultItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollResultViewHolder {
        var viewHolder: PollResultViewHolder? = null

        when (viewType) {
            PollResultHeaderItem.VIEW_TYPE -> {
                val itemBinding = PollResultHeaderItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                viewHolder = PollResultHeaderViewHolder(itemBinding, viewThemeUtils)
            }
            PollResultVoterItem.VIEW_TYPE -> {
                val itemBinding = PollResultVoterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                viewHolder = PollResultVoterViewHolder(user, roomToken, itemBinding, viewThemeUtils)
            }
            PollResultVotersOverviewItem.VIEW_TYPE -> {
                val itemBinding = PollResultVotersOverviewItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                viewHolder = PollResultVotersOverviewViewHolder(user, roomToken, itemBinding)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: PollResultViewHolder, position: Int) {
        when (holder.itemViewType) {
            PollResultHeaderItem.VIEW_TYPE -> {
                val pollResultItem = list[position]
                holder.bind(pollResultItem as PollResultHeaderItem, clickListener)
            }
            PollResultVoterItem.VIEW_TYPE -> {
                val pollResultItem = list[position]
                holder.bind(pollResultItem as PollResultVoterItem, clickListener)
            }
            PollResultVotersOverviewItem.VIEW_TYPE -> {
                val pollResultItem = list[position]
                holder.bind(pollResultItem as PollResultVotersOverviewItem, clickListener)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = list[position].getViewType()
}
