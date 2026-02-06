/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.dagger.modules

import com.dalvi.safe.data.database.dao.ChatBlocksDao
import com.dalvi.safe.data.database.dao.ChatMessagesDao
import com.dalvi.safe.data.database.dao.ConversationsDao
import com.dalvi.safe.data.source.local.TalkDatabase
import dagger.Module
import dagger.Provides

@Module
internal object DaosModule {
    @Provides
    fun providesConversationsDao(database: TalkDatabase): ConversationsDao = database.conversationsDao()

    @Provides
    fun providesChatDao(database: TalkDatabase): ChatMessagesDao = database.chatMessagesDao()

    @Provides
    fun providesChatBlocksDao(database: TalkDatabase): ChatBlocksDao = database.chatBlocksDao()
}
