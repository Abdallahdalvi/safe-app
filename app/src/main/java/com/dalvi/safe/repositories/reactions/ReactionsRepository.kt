/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.repositories.reactions

import com.dalvi.safe.models.domain.ReactionAddedModel
import com.dalvi.safe.models.domain.ReactionDeletedModel
import com.dalvi.safe.chat.data.model.ChatMessage
import io.reactivex.Observable

interface ReactionsRepository {

    @Suppress("LongParameterList")
    fun addReaction(
        credentials: String?,
        userId: Long,
        url: String,
        roomToken: String,
        message: ChatMessage,
        emoji: String
    ): Observable<ReactionAddedModel>

    @Suppress("LongParameterList")
    fun deleteReaction(
        credentials: String?,
        userId: Long,
        url: String,
        roomToken: String,
        message: ChatMessage,
        emoji: String
    ): Observable<ReactionDeletedModel>
}
