/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models.domain

import com.dalvi.safe.chat.data.model.ChatMessage

data class ReactionAddedModel(var chatMessage: ChatMessage, var emoji: String, var success: Boolean)
