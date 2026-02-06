/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.polls.adapters

import com.dalvi.safe.adapters.items.FlexibleItemViewType
import com.dalvi.safe.polls.model.PollDetails

data class PollResultVotersOverviewItem(val detailsList: List<PollDetails>) : PollResultItem {

    override fun getViewType(): Int = VIEW_TYPE

    companion object {
        // layout is used as view type for uniqueness
        const val VIEW_TYPE = FlexibleItemViewType.POLL_RESULT_VOTERS_OVERVIEW_ITEM
    }
}
