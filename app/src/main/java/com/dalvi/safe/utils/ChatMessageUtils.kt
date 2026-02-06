/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils

import android.view.View
import android.widget.ImageView
import com.dalvi.safe.chat.data.model.ChatMessage
import com.dalvi.safe.extensions.loadBotsAvatar
import com.dalvi.safe.extensions.loadChangelogBotAvatar
import com.dalvi.safe.extensions.loadDefaultAvatar
import com.dalvi.safe.extensions.loadFederatedUserAvatar
import com.dalvi.safe.extensions.loadFirstLetterAvatar
import com.dalvi.safe.ui.theme.ViewThemeUtils

class ChatMessageUtils {

    fun setAvatarOnMessage(view: ImageView, message: ChatMessage, viewThemeUtils: ViewThemeUtils) {
        view.visibility = View.VISIBLE
        if (message.actorType == "guests" || message.actorType == "emails") {
            val actorName = message.actorDisplayName
            if (!actorName.isNullOrBlank()) {
                view.loadFirstLetterAvatar(actorName)
            } else {
                view.loadDefaultAvatar(viewThemeUtils)
            }
        } else if (message.actorType == "bots" && (message.actorId == "changelog" || message.actorId == "sample")) {
            view.loadChangelogBotAvatar()
        } else if (message.actorType == "bots") {
            view.loadBotsAvatar()
        } else if (message.actorType == "federated_users") {
            view.loadFederatedUserAvatar(message)
        }
    }
}
