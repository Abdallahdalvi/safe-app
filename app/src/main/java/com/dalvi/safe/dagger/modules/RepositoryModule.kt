/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022-2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.dagger.modules

import android.content.Context
import com.dalvi.safe.account.data.LoginRepository
import com.dalvi.safe.account.data.io.LocalLoginDataSource
import com.dalvi.safe.account.data.network.NetworkLoginDataSource
import com.dalvi.safe.api.NcApi
import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.chat.data.ChatMessageRepository
import com.dalvi.safe.chat.data.network.ChatNetworkDataSource
import com.dalvi.safe.chat.data.network.OfflineFirstChatRepository
import com.dalvi.safe.chat.data.network.RetrofitChatNetwork
import com.dalvi.safe.chooseaccount.StatusRepository
import com.dalvi.safe.chooseaccount.StatusRepositoryImplementation
import com.dalvi.safe.contacts.ContactsRepository
import com.dalvi.safe.contacts.ContactsRepositoryImpl
import com.dalvi.safe.conversationcreation.ConversationCreationRepository
import com.dalvi.safe.conversationcreation.ConversationCreationRepositoryImpl
import com.dalvi.safe.conversationinfoedit.data.ConversationInfoEditRepository
import com.dalvi.safe.conversationinfoedit.data.ConversationInfoEditRepositoryImpl
import com.dalvi.safe.conversationlist.data.OfflineConversationsRepository
import com.dalvi.safe.conversationlist.data.network.ConversationsNetworkDataSource
import com.dalvi.safe.conversationlist.data.network.OfflineFirstConversationsRepository
import com.dalvi.safe.conversationlist.data.network.RetrofitConversationsNetwork
import com.dalvi.safe.data.database.dao.ChatBlocksDao
import com.dalvi.safe.data.database.dao.ChatMessagesDao
import com.dalvi.safe.data.database.dao.ConversationsDao
import com.dalvi.safe.data.network.NetworkMonitor
import com.dalvi.safe.data.source.local.TalkDatabase
import com.dalvi.safe.data.storage.ArbitraryStoragesRepository
import com.dalvi.safe.data.storage.ArbitraryStoragesRepositoryImpl
import com.dalvi.safe.data.user.UsersRepository
import com.dalvi.safe.data.user.UsersRepositoryImpl
import com.dalvi.safe.invitation.data.InvitationsRepository
import com.dalvi.safe.invitation.data.InvitationsRepositoryImpl
import com.dalvi.safe.openconversations.data.OpenConversationsRepository
import com.dalvi.safe.openconversations.data.OpenConversationsRepositoryImpl
import com.dalvi.safe.polls.repositories.PollRepository
import com.dalvi.safe.polls.repositories.PollRepositoryImpl
import com.dalvi.safe.raisehand.RequestAssistanceRepository
import com.dalvi.safe.raisehand.RequestAssistanceRepositoryImpl
import com.dalvi.safe.remotefilebrowser.repositories.RemoteFileBrowserItemsRepository
import com.dalvi.safe.remotefilebrowser.repositories.RemoteFileBrowserItemsRepositoryImpl
import com.dalvi.safe.repositories.callrecording.CallRecordingRepository
import com.dalvi.safe.repositories.callrecording.CallRecordingRepositoryImpl
import com.dalvi.safe.repositories.conversations.ConversationsRepository
import com.dalvi.safe.repositories.conversations.ConversationsRepositoryImpl
import com.dalvi.safe.repositories.reactions.ReactionsRepository
import com.dalvi.safe.repositories.reactions.ReactionsRepositoryImpl
import com.dalvi.safe.repositories.unifiedsearch.UnifiedSearchRepository
import com.dalvi.safe.repositories.unifiedsearch.UnifiedSearchRepositoryImpl
import com.dalvi.safe.shareditems.repositories.SharedItemsRepository
import com.dalvi.safe.shareditems.repositories.SharedItemsRepositoryImpl
import com.dalvi.safe.threadsoverview.data.ThreadsRepository
import com.dalvi.safe.threadsoverview.data.ThreadsRepositoryImpl
import com.dalvi.safe.translate.repositories.TranslateRepository
import com.dalvi.safe.translate.repositories.TranslateRepositoryImpl
import com.dalvi.safe.users.UserManager
import com.dalvi.safe.utils.DateUtils
import com.dalvi.safe.utils.preferences.AppPreferences
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Suppress("TooManyFunctions")
@Module
class RepositoryModule {

    @Provides
    fun provideConversationsRepository(ncApi: NcApi, ncApiCoroutines: NcApiCoroutines): ConversationsRepository =
        ConversationsRepositoryImpl(ncApi, ncApiCoroutines)

    @Provides
    fun provideSharedItemsRepository(ncApi: NcApi, dateUtils: DateUtils): SharedItemsRepository =
        SharedItemsRepositoryImpl(ncApi, dateUtils)

    @Provides
    fun provideUnifiedSearchRepository(ncApi: NcApi): UnifiedSearchRepository = UnifiedSearchRepositoryImpl(ncApi)

    @Provides
    fun provideDialogPollRepository(ncApi: NcApi): PollRepository = PollRepositoryImpl(ncApi)

    @Provides
    fun provideRemoteFileBrowserItemsRepository(okHttpClient: OkHttpClient): RemoteFileBrowserItemsRepository =
        RemoteFileBrowserItemsRepositoryImpl(okHttpClient)

    @Provides
    fun provideUsersRepository(database: TalkDatabase): UsersRepository = UsersRepositoryImpl(database.usersDao())

    @Provides
    fun provideArbitraryStoragesRepository(database: TalkDatabase): ArbitraryStoragesRepository =
        ArbitraryStoragesRepositoryImpl(database.arbitraryStoragesDao())

    @Provides
    fun provideReactionsRepository(ncApi: NcApi, dao: ChatMessagesDao): ReactionsRepository =
        ReactionsRepositoryImpl(ncApi, dao)

    @Provides
    fun provideCallRecordingRepository(ncApi: NcApi): CallRecordingRepository = CallRecordingRepositoryImpl(ncApi)

    @Provides
    fun provideRequestAssistanceRepository(ncApi: NcApi): RequestAssistanceRepository =
        RequestAssistanceRepositoryImpl(ncApi)

    @Provides
    fun provideOpenConversationsRepository(ncApiCoroutines: NcApiCoroutines): OpenConversationsRepository =
        OpenConversationsRepositoryImpl(ncApiCoroutines)

    @Provides
    fun translateRepository(ncApi: NcApi): TranslateRepository = TranslateRepositoryImpl(ncApi)

    @Provides
    fun provideChatNetworkDataSource(ncApi: NcApi, ncApiCoroutines: NcApiCoroutines): ChatNetworkDataSource =
        RetrofitChatNetwork(ncApi, ncApiCoroutines)

    @Provides
    fun provideConversationsNetworkDataSource(ncApi: NcApi): ConversationsNetworkDataSource =
        RetrofitConversationsNetwork(ncApi)

    @Provides
    fun provideConversationInfoEditRepository(
        ncApi: NcApi,
        ncApiCoroutines: NcApiCoroutines
    ): ConversationInfoEditRepository = ConversationInfoEditRepositoryImpl(ncApi, ncApiCoroutines)

    @Provides
    fun provideInvitationsRepository(ncApi: NcApi, ncApiCoroutines: NcApiCoroutines): InvitationsRepository =
        InvitationsRepositoryImpl(ncApi, ncApiCoroutines)

    @Provides
    fun provideOfflineFirstChatRepository(
        chatMessagesDao: ChatMessagesDao,
        chatBlocksDao: ChatBlocksDao,
        dataSource: ChatNetworkDataSource,
        networkMonitor: NetworkMonitor
    ): ChatMessageRepository =
        OfflineFirstChatRepository(
            chatMessagesDao,
            chatBlocksDao,
            dataSource,
            networkMonitor
        )

    @Provides
    fun provideOfflineFirstConversationsRepository(
        dao: ConversationsDao,
        dataSource: ConversationsNetworkDataSource,
        chatNetworkDataSource: ChatNetworkDataSource,
        networkMonitor: NetworkMonitor
    ): OfflineConversationsRepository =
        OfflineFirstConversationsRepository(
            dao,
            dataSource,
            chatNetworkDataSource,
            networkMonitor
        )

    @Provides
    fun provideContactsRepository(ncApiCoroutines: NcApiCoroutines): ContactsRepository =
        ContactsRepositoryImpl(ncApiCoroutines)

    @Provides
    fun provideConversationCreationRepository(ncApiCoroutines: NcApiCoroutines): ConversationCreationRepository =
        ConversationCreationRepositoryImpl(ncApiCoroutines)

    @Provides
    fun provideThreadsRepository(ncApiCoroutines: NcApiCoroutines): ThreadsRepository =
        ThreadsRepositoryImpl(ncApiCoroutines)

    @Provides
    fun provideNetworkDataSource(okHttpClient: OkHttpClient): NetworkLoginDataSource =
        NetworkLoginDataSource(okHttpClient)

    @Provides
    fun providesLocalDataSource(
        userManager: UserManager,
        appPreferences: AppPreferences,
        context: Context
    ): LocalLoginDataSource = LocalLoginDataSource(userManager, appPreferences, context)

    @Provides
    fun provideLoginRepository(
        networkLoginDataSource: NetworkLoginDataSource,
        localLoginDataSource: LocalLoginDataSource
    ): LoginRepository = LoginRepository(networkLoginDataSource, localLoginDataSource)

    @Provides
    fun provideStatusRepository(ncApiCoroutines: NcApiCoroutines): StatusRepository =
        StatusRepositoryImplementation(ncApiCoroutines)
}
