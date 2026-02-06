/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.conversationcreation

import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.RetrofitBucket
import com.dalvi.safe.models.domain.ConversationModel
import com.dalvi.safe.models.json.conversations.RoomOverall
import com.dalvi.safe.models.json.generic.GenericOverall
import com.dalvi.safe.models.json.participants.AddParticipantOverall
import com.dalvi.safe.utils.Mimetype
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ConversationCreationRepositoryImpl @Inject constructor(private val ncApiCoroutines: NcApiCoroutines) :
    ConversationCreationRepository {

    override suspend fun setConversationDescription(
        credentials: String?,
        url: String,
        roomToken: String,
        description: String?
    ): GenericOverall =
        ncApiCoroutines.setConversationDescription(
            credentials,
            url,
            description
        )

    override suspend fun openConversation(
        credentials: String?,
        url: String,
        roomToken: String,
        scope: Int
    ): GenericOverall =
        ncApiCoroutines.openConversation(
            credentials,
            url,
            scope
        )

    override suspend fun addParticipants(credentials: String?, retrofitBucket: RetrofitBucket): AddParticipantOverall {
        val participants = ncApiCoroutines.addParticipant(
            credentials,
            retrofitBucket.url,
            retrofitBucket.queryMap
        )
        return participants
    }

    override suspend fun createRoom(credentials: String?, retrofitBucket: RetrofitBucket): RoomOverall {
        val response = ncApiCoroutines.createRoom(
            credentials,
            retrofitBucket.url,
            retrofitBucket.queryMap
        )
        return response
    }

    override suspend fun setPassword(
        credentials: String?,
        url: String,
        roomToken: String,
        password: String
    ): GenericOverall {
        val result = ncApiCoroutines.setPassword(
            credentials,
            url,
            password
        )
        return result
    }

    override suspend fun uploadConversationAvatar(
        credentials: String?,
        user: User,
        url: String,
        file: File,
        roomToken: String
    ): ConversationModel {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart(
            "file",
            file.name,
            file.asRequestBody(Mimetype.IMAGE_PREFIX_GENERIC.toMediaTypeOrNull())
        )
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody(Mimetype.IMAGE_JPG.toMediaTypeOrNull())
        )
        val response = ncApiCoroutines.uploadConversationAvatar(
            credentials!!,
            url,
            filePart
        )
        return ConversationModel.mapToConversationModel(response.ocs?.data!!, user)
    }

    override suspend fun allowGuests(credentials: String?, url: String, token: String, allow: Boolean): GenericOverall {
        val result: GenericOverall = if (allow) {
            ncApiCoroutines.makeRoomPublic(
                credentials!!,
                url
            )
        } else {
            ncApiCoroutines.makeRoomPrivate(
                credentials!!,
                url
            )
        }
        return result
    }
}
