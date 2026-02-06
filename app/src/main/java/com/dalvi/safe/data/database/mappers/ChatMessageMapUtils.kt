/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Julius Linus <juliuslinus1@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.data.database.mappers

import com.dalvi.safe.chat.data.model.ChatMessage
import com.dalvi.safe.data.database.model.ChatMessageEntity
import com.dalvi.safe.models.json.chat.ChatMessageJson
import com.dalvi.safe.models.json.chat.ReadStatus

fun ChatMessageJson.asEntity(accountId: Long) =
    ChatMessageEntity(
        // accountId@token@messageId
        internalId = "$accountId@$token@$id",
        accountId = accountId,
        id = id,
        internalConversationId = "$accountId@$token",
        threadId = threadId,
        isThread = hasThread,
        message = message!!,
        token = token!!,
        actorType = actorType!!,
        actorId = actorId!!,
        actorDisplayName = actorDisplayName!!,
        timestamp = timestamp,
        messageParameters = messageParameters,
        systemMessageType = systemMessageType!!,
        replyable = replyable,
        parentMessageId = parentMessage?.id,
        messageType = messageType!!,
        reactions = reactions,
        reactionsSelf = reactionsSelf,
        expirationTimestamp = expirationTimestamp,
        renderMarkdown = renderMarkdown,
        lastEditActorDisplayName = lastEditActorDisplayName,
        lastEditActorId = lastEditActorId,
        lastEditActorType = lastEditActorType,
        lastEditTimestamp = lastEditTimestamp,
        deleted = deleted,
        referenceId = referenceId,
        silent = silent,
        threadTitle = threadTitle,
        threadReplies = threadReplies,
        pinnedActorType = metaData?.pinnedActorType,
        pinnedActorId = metaData?.pinnedActorId,
        pinnedActorDisplayName = metaData?.pinnedActorDisplayName,
        pinnedAt = metaData?.pinnedAt,
        pinnedUntil = metaData?.pinnedUntil,
        sendAt = sendAt
    )

fun ChatMessageEntity.asModel() =
    ChatMessage(
        jsonMessageId = id.toInt(),
        message = message,
        token = token,
        threadId = threadId,
        isThread = isThread,
        actorType = actorType,
        actorId = actorId,
        actorDisplayName = actorDisplayName,
        timestamp = timestamp,
        messageParameters = messageParameters,
        systemMessageType = systemMessageType,
        replyable = replyable,
        parentMessageId = parentMessageId,
        messageType = messageType,
        reactions = reactions,
        reactionsSelf = reactionsSelf,
        expirationTimestamp = expirationTimestamp,
        renderMarkdown = renderMarkdown,
        lastEditActorDisplayName = lastEditActorDisplayName,
        lastEditActorId = lastEditActorId,
        lastEditActorType = lastEditActorType,
        lastEditTimestamp = lastEditTimestamp,
        isDeleted = deleted,
        referenceId = referenceId,
        isTemporary = isTemporary,
        sendStatus = sendStatus,
        readStatus = ReadStatus.NONE,
        silent = silent,
        threadTitle = threadTitle,
        threadReplies = threadReplies,
        pinnedActorType = pinnedActorType,
        pinnedActorId = pinnedActorId,
        pinnedActorDisplayName = pinnedActorDisplayName,
        pinnedAt = pinnedAt,
        pinnedUntil = pinnedUntil,
        sendAt = sendAt
    )

fun ChatMessageJson.asModel() =
    ChatMessage(
        jsonMessageId = id.toInt(),
        message = message,
        token = token,
        threadId = threadId,
        isThread = hasThread,
        actorType = actorType,
        actorId = actorId,
        actorDisplayName = actorDisplayName,
        timestamp = timestamp,
        messageParameters = messageParameters,
        systemMessageType = systemMessageType,
        replyable = replyable,
        parentMessageId = parentMessage?.id,
        messageType = messageType,
        reactions = reactions,
        reactionsSelf = reactionsSelf,
        expirationTimestamp = expirationTimestamp,
        renderMarkdown = renderMarkdown,
        lastEditActorDisplayName = lastEditActorDisplayName,
        lastEditActorId = lastEditActorId,
        lastEditActorType = lastEditActorType,
        lastEditTimestamp = lastEditTimestamp,
        isDeleted = deleted,
        referenceId = referenceId,
        silent = silent,
        threadTitle = threadTitle,
        threadReplies = threadReplies,
        pinnedActorType = metaData?.pinnedActorType,
        pinnedActorId = metaData?.pinnedActorId,
        pinnedActorDisplayName = metaData?.pinnedActorDisplayName,
        pinnedAt = metaData?.pinnedAt,
        pinnedUntil = metaData?.pinnedUntil,
        sendAt = sendAt
    )
