/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.contextchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import autodagger.AutoInjector
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.chat.data.network.ChatNetworkDataSource
import com.dalvi.safe.chat.viewmodels.ChatViewModel
import com.dalvi.safe.models.json.chat.ChatMessageJson
import com.dalvi.safe.users.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class ContextChatViewModel @Inject constructor(private val chatNetworkDataSource: ChatNetworkDataSource) :
    ViewModel() {

    @Inject
    lateinit var chatViewModel: ChatViewModel

    @Inject
    lateinit var userManager: UserManager

    var threadId: String? = null

    private val _getContextChatMessagesState =
        MutableStateFlow<ContextChatRetrieveUiState>(ContextChatRetrieveUiState.None)
    val getContextChatMessagesState: StateFlow<ContextChatRetrieveUiState> = _getContextChatMessagesState

    @Suppress("LongParameterList")
    fun getContextForChatMessages(
        credentials: String,
        baseUrl: String,
        token: String,
        threadId: String?,
        messageId: String,
        title: String
    ) {
        viewModelScope.launch {
            val user = userManager.currentUser.blockingGet()

            if (!user.hasSpreedFeatureCapability("chat-get-context") ||
                !user.hasSpreedFeatureCapability("federation-v1")
            ) {
                _getContextChatMessagesState.value = ContextChatRetrieveUiState.Error
            }

            var messages = chatNetworkDataSource.getContextForChatMessage(
                credentials = credentials,
                baseUrl = baseUrl,
                token = token,
                messageId = messageId,
                limit = LIMIT,
                threadId = threadId?.toIntOrNull()
            )

            if (threadId.isNullOrEmpty()) {
                messages = messages.filter { !isThreadChildMessage(it) }
            }

            val subTitle = if (threadId?.isNotEmpty() == true) {
                messages.firstOrNull()?.threadTitle
            } else {
                ""
            }

            _getContextChatMessagesState.value = ContextChatRetrieveUiState.Success(
                messageId = messageId,
                threadId = threadId,
                messages = messages,
                title = title,
                subTitle = subTitle
            )
        }
    }

    fun isThreadChildMessage(currentMessage: ChatMessageJson): Boolean =
        currentMessage.hasThread &&
            currentMessage.threadId != currentMessage.id

    fun clearContextChatState() {
        _getContextChatMessagesState.value = ContextChatRetrieveUiState.None
    }

    sealed class ContextChatRetrieveUiState {
        data object None : ContextChatRetrieveUiState()
        data class Success(
            val messageId: String,
            val threadId: String?,
            val messages: List<ChatMessageJson>,
            val title: String?,
            val subTitle: String?
        ) : ContextChatRetrieveUiState()
        data object Error : ContextChatRetrieveUiState()
    }

    companion object {
        private const val LIMIT = 50
    }
}
