/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.openconversations.data

import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.conversations.Conversation
import kotlinx.coroutines.flow.Flow

interface OpenConversationsRepository {

    suspend fun fetchConversations(user: User, url: String, searchTerm: String): Result<List<Conversation>>

    fun fetchOpenConversationsFlow(user: User, searchTerm: String): Flow<List<Conversation>>
}
