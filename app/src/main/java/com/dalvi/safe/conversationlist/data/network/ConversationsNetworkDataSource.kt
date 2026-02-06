/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.conversationlist.data.network

import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.conversations.Conversation
import io.reactivex.Observable

interface ConversationsNetworkDataSource {
    fun getRooms(user: User, url: String, includeStatus: Boolean): Observable<List<Conversation>>
}
