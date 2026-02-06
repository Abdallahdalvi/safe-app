/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Julius Linus <juliuslinus1@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.utils.preview

import android.content.Context
import com.github.aurae.retrofit2.LoganSquareConverterFactory
import com.nextcloud.android.common.ui.color.ColorUtil
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils
import com.dalvi.safe.api.NcApi
import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.chat.data.ChatMessageRepository
import com.dalvi.safe.chat.data.io.AudioFocusRequestManager
import com.dalvi.safe.chat.data.io.MediaRecorderManager
import com.dalvi.safe.chat.data.network.ChatNetworkDataSource
import com.dalvi.safe.chat.data.network.OfflineFirstChatRepository
import com.dalvi.safe.chat.data.network.RetrofitChatNetwork
import com.dalvi.safe.chat.viewmodels.ChatViewModel
import com.dalvi.safe.contacts.ContactsRepository
import com.dalvi.safe.contacts.ContactsRepositoryImpl
import com.dalvi.safe.contacts.ContactsViewModel
import com.dalvi.safe.conversationlist.data.OfflineConversationsRepository
import com.dalvi.safe.conversationlist.data.network.ConversationsNetworkDataSource
import com.dalvi.safe.conversationlist.data.network.OfflineFirstConversationsRepository
import com.dalvi.safe.conversationlist.data.network.RetrofitConversationsNetwork
import com.dalvi.safe.data.database.dao.ChatBlocksDao
import com.dalvi.safe.data.database.dao.ChatMessagesDao
import com.dalvi.safe.data.database.dao.ConversationsDao
import com.dalvi.safe.data.network.NetworkMonitor
import com.dalvi.safe.data.network.NetworkMonitorImpl
import com.dalvi.safe.data.user.UsersDao
import com.dalvi.safe.data.user.UsersRepository
import com.dalvi.safe.data.user.UsersRepositoryImpl
import com.dalvi.safe.repositories.reactions.ReactionsRepository
import com.dalvi.safe.repositories.reactions.ReactionsRepositoryImpl
import com.dalvi.safe.threadsoverview.data.ThreadsRepository
import com.dalvi.safe.threadsoverview.data.ThreadsRepositoryImpl
import com.dalvi.safe.ui.theme.MaterialSchemesProviderImpl
import com.dalvi.safe.ui.theme.TalkSpecificViewThemeUtils
import com.dalvi.safe.ui.theme.ViewThemeUtils
import com.dalvi.safe.users.UserManager
import com.dalvi.safe.utils.database.user.CurrentUserProviderOldImpl
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import com.dalvi.safe.utils.message.MessageUtils
import com.dalvi.safe.utils.preferences.AppPreferences
import com.dalvi.safe.utils.preferences.AppPreferencesImpl
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * TODO - basically a reimplementation of common dependencies for use in Previewing Advanced Compose Views
 * It's a hard coded Dependency Injector
 *
 */
class ComposePreviewUtils private constructor(context: Context) {
    private val mContext = context

    companion object {
        fun getInstance(context: Context) = ComposePreviewUtils(context)
        val TAG: String = ComposePreviewUtils::class.java.simpleName
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val appPreferences: AppPreferences
        get() = AppPreferencesImpl(mContext)

    val context: Context = mContext

    val userRepository: UsersRepository
        get() = UsersRepositoryImpl(usersDao)

    val userManager: UserManager
        get() = UserManager(userRepository)

    val userProvider: CurrentUserProviderOld
        get() = CurrentUserProviderOldImpl(userManager)

    val colorUtil: ColorUtil
        get() = ColorUtil(mContext)

    val materialScheme: MaterialSchemes
        get() = MaterialSchemesProviderImpl(userProvider, colorUtil).getMaterialSchemesForCurrentUser()

    val viewThemeUtils: ViewThemeUtils
        get() {
            val android = AndroidViewThemeUtils(materialScheme, colorUtil)
            val material = MaterialViewThemeUtils(materialScheme, colorUtil)
            val androidx = AndroidXViewThemeUtils(materialScheme, android)
            val talk = TalkSpecificViewThemeUtils(materialScheme, androidx)
            val dialog = DialogViewThemeUtils(materialScheme)
            return ViewThemeUtils(materialScheme, android, material, androidx, talk, dialog)
        }

    val messageUtils: MessageUtils
        get() = MessageUtils(mContext)

    val retrofit: Retrofit
        get() {
            val retrofitBuilder = Retrofit.Builder()
                .client(OkHttpClient.Builder().build())
                .baseUrl("https://nextcloud.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(LoganSquareConverterFactory.create())

            return retrofitBuilder.build()
        }

    val ncApi: NcApi
        get() = retrofit.create(NcApi::class.java)

    val ncApiCoroutines: NcApiCoroutines
        get() = retrofit.create(NcApiCoroutines::class.java)

    val chatNetworkDataSource: ChatNetworkDataSource
        get() = RetrofitChatNetwork(ncApi, ncApiCoroutines)

    val usersDao: UsersDao
        get() = DummyUserDaoImpl()

    val chatMessagesDao: ChatMessagesDao
        get() = DummyChatMessagesDaoImpl()

    val chatBlocksDao: ChatBlocksDao
        get() = DummyChatBlocksDaoImpl()

    val conversationsDao: ConversationsDao
        get() = DummyConversationDaoImpl()

    val networkMonitor: NetworkMonitor
        get() = NetworkMonitorImpl(mContext)

    val chatRepository: ChatMessageRepository
        get() = OfflineFirstChatRepository(
            chatMessagesDao,
            chatBlocksDao,
            chatNetworkDataSource,
            networkMonitor
        )

    val threadsRepository: ThreadsRepository
        get() = ThreadsRepositoryImpl(ncApiCoroutines)

    val conversationNetworkDataSource: ConversationsNetworkDataSource
        get() = RetrofitConversationsNetwork(ncApi)

    val conversationRepository: OfflineConversationsRepository
        get() = OfflineFirstConversationsRepository(
            conversationsDao,
            conversationNetworkDataSource,
            chatNetworkDataSource,
            networkMonitor
        )

    val reactionsRepository: ReactionsRepository
        get() = ReactionsRepositoryImpl(ncApi, chatMessagesDao)

    val mediaRecorderManager: MediaRecorderManager
        get() = MediaRecorderManager()

    val audioFocusRequestManager: AudioFocusRequestManager
        get() = AudioFocusRequestManager(mContext)

    val chatViewModel: ChatViewModel
        get() = ChatViewModel(
            appPreferences,
            chatNetworkDataSource,
            chatRepository,
            threadsRepository,
            conversationRepository,
            reactionsRepository,
            mediaRecorderManager,
            audioFocusRequestManager
        )

    val contactsRepository: ContactsRepository
        get() = ContactsRepositoryImpl(ncApiCoroutines)

    val contactsViewModel: ContactsViewModel
        get() = ContactsViewModel(contactsRepository, userProvider)
}
