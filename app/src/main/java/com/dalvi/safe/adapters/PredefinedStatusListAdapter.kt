/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2020 Tobias Kaminsky <tobias@kaminsky.me>
 * SPDX-FileCopyrightText: 2020 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalvi.safe.databinding.PredefinedStatusBinding
import com.dalvi.safe.models.json.status.predefined.PredefinedStatus

class PredefinedStatusListAdapter(
    private val clickListener: PredefinedStatusClickListener,
    val context: Context,
    var isBackupStatusAvailable: Boolean
) : RecyclerView.Adapter<PredefinedStatusViewHolder>() {
    internal var list: List<PredefinedStatus> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredefinedStatusViewHolder {
        val itemBinding = PredefinedStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredefinedStatusViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PredefinedStatusViewHolder, position: Int) {
        holder.bind(list[position], clickListener, context, isBackupStatusAvailable)
    }

    override fun getItemCount(): Int = list.size
}
