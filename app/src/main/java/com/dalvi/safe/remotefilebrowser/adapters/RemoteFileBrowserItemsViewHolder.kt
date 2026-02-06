/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.remotefilebrowser.adapters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.remotefilebrowser.SelectionInterface
import com.dalvi.safe.remotefilebrowser.model.RemoteFileBrowserItem
import com.dalvi.safe.utils.DrawableUtils

abstract class RemoteFileBrowserItemsViewHolder(
    open val binding: ViewBinding,
    val mimeTypeSelectionFilter: String? = null,
    val currentUser: User,
    val selectionInterface: SelectionInterface
) : RecyclerView.ViewHolder(binding.root) {

    abstract val fileIcon: ImageView

    open fun onBind(item: RemoteFileBrowserItem) {
        fileIcon.setImageResource(DrawableUtils.getDrawableResourceIdForMimeType(item.mimeType))
    }
}
