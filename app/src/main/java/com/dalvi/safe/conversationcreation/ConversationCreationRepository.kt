/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.conversationcreation

import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.RetrofitBucket
import com.dalvi.safe.models.domain.ConversationModel
import com.dalvi.safe.models.json.conversations.RoomOverall
import com.dalvi.safe.models.json.generic.GenericOverall
import com.dalvi.safe.models.json.participants.AddParticipantOverall
import java.io.File

interface ConversationCreationRepository {

    suspend fun setConversationDescription(
        credentials: String?,
        url: String,
        roomToken: String,
        description: String?
    ): GenericOverall
    suspend fun openConversation(credentials: String?, url: String, roomToken: String, scope: Int): GenericOverall
    suspend fun addParticipants(credentials: String?, retrofitBucket: RetrofitBucket): AddParticipantOverall
    suspend fun createRoom(credentials: String?, retrofitBucket: RetrofitBucket): RoomOverall
    suspend fun setPassword(credentials: String?, url: String, roomToken: String, password: String): GenericOverall
    suspend fun uploadConversationAvatar(
        credentials: String?,
        user: User,
        url: String,
        file: File,
        roomToken: String
    ): ConversationModel
    suspend fun allowGuests(credentials: String?, url: String, token: String, allow: Boolean): GenericOverall
}
