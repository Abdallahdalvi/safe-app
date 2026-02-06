/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.adapters.messages

import android.view.View
import com.dalvi.safe.R
import com.dalvi.safe.chat.ChatActivity
import com.dalvi.safe.databinding.ReactionsInsideMessageBinding
import com.dalvi.safe.chat.data.model.ChatMessage
import com.dalvi.safe.databinding.ItemThreadTitleBinding

class Thread {

    fun showThreadPreview(
        chatActivity: ChatActivity,
        message: ChatMessage,
        threadBinding: ItemThreadTitleBinding,
        reactionsBinding: ReactionsInsideMessageBinding,
        openThread: (message: ChatMessage) -> Unit
    ) {
        val isFirstMessageOfThreadInNormalChat = chatActivity.conversationThreadId == null && message.isThread
        if (isFirstMessageOfThreadInNormalChat) {
            threadBinding.threadTitleLayout.visibility = View.VISIBLE

            threadBinding.threadTitleLayout.findViewById<androidx.emoji2.widget.EmojiTextView>(R.id.threadTitle).text =
                message.threadTitle

            reactionsBinding.threadButton.visibility = View.VISIBLE

            reactionsBinding.threadButton.setContent {
                ThreadButtonComposable(
                    message.threadReplies ?: 0,
                    onButtonClick = { openThread(message) }
                )
            }
        } else {
            threadBinding.threadTitleLayout.visibility = View.GONE
            reactionsBinding.threadButton.visibility = View.GONE
        }
    }
}
