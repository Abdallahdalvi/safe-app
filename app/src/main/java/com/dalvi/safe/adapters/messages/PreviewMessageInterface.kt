/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters.messages

import com.dalvi.safe.chat.data.model.ChatMessage

interface PreviewMessageInterface {
    fun onPreviewMessageLongClick(chatMessage: ChatMessage)
}
