/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.contacts

import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.autocomplete.AutocompleteOverall
import com.dalvi.safe.models.json.autocomplete.AutocompleteUser
import com.dalvi.safe.models.json.conversations.RoomOverall
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun getContacts(user: User, searchQuery: String?, shareTypes: List<String>): AutocompleteOverall

    suspend fun createRoom(
        user: User,
        roomType: String,
        sourceType: String?,
        userId: String,
        conversationName: String?
    ): RoomOverall

    fun getImageUri(user: User, avatarId: String, requestBigSize: Boolean): String

    fun getContactsFlow(user: User, searchQuery: String?): Flow<List<AutocompleteUser>>
}
