/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Christian Reiner <foss@christian-reiner.info>
 * SPDX-FileCopyrightText: 2021 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters.messages

import com.dalvi.safe.chat.data.model.ChatMessage
import com.dalvi.safe.ui.PlaybackSpeed

interface VoiceMessageInterface {
    fun updateMediaPlayerProgressBySlider(message: ChatMessage, progress: Int)
    fun registerMessageToObservePlaybackSpeedPreferences(userId: String, listener: (speed: PlaybackSpeed) -> Unit)
}
