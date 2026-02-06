/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters.items

import android.content.Context
import com.dalvi.safe.R
import com.dalvi.safe.ui.theme.ViewThemeUtils

class MessagesTextHeaderItem(context: Context, viewThemeUtils: ViewThemeUtils) :
    GenericTextHeaderItem(context.getString(R.string.messages), viewThemeUtils) {
    companion object {
        const val VIEW_TYPE = FlexibleItemViewType.MESSAGES_TEXT_HEADER_ITEM
    }

    override fun getItemViewType(): Int = VIEW_TYPE
}
