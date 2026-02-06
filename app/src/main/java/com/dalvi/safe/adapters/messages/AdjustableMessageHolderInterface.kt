/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Christian Reiner <foss@christian-reiner.info>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.adapters.messages

import android.widget.RelativeLayout
import androidx.viewbinding.ViewBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingDeckCardMessageBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingLinkPreviewMessageBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingLocationMessageBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingPollMessageBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingTextMessageBinding
import com.dalvi.safe.databinding.ItemCustomOutcomingVoiceMessageBinding
import com.dalvi.safe.models.domain.ConversationModel
import com.dalvi.safe.models.json.conversations.ConversationEnums.ConversationType

interface AdjustableMessageHolderInterface {

    val binding: ViewBinding

    fun adjustIfNoteToSelf(currentConversation: ConversationModel?) {
        if (currentConversation?.type == ConversationType.NOTE_TO_SELF) {
            when (this.binding.javaClass) {
                ItemCustomOutcomingTextMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingTextMessageBinding).bubble
                ItemCustomOutcomingDeckCardMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingDeckCardMessageBinding).bubble
                ItemCustomOutcomingLinkPreviewMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingLinkPreviewMessageBinding).bubble
                ItemCustomOutcomingPollMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingPollMessageBinding).bubble
                ItemCustomOutcomingVoiceMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingVoiceMessageBinding).bubble
                ItemCustomOutcomingLocationMessageBinding::class.java ->
                    (this.binding as ItemCustomOutcomingLocationMessageBinding).bubble
                else -> null
            }?.let {
                RelativeLayout.LayoutParams(binding.root.layoutParams).apply {
                    marginStart = 0
                    marginEnd = 0
                }.run {
                    it.layoutParams = this
                }
            }
        }
    }
}
