/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.dagger.modules

import android.content.Context
import com.dalvi.safe.utils.DateUtils
import com.dalvi.safe.utils.message.MessageUtils
import com.dalvi.safe.utils.permissions.PlatformPermissionUtil
import com.dalvi.safe.utils.permissions.PlatformPermissionUtilImpl
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module(includes = [ContextModule::class])
class UtilsModule {
    @Provides
    @Reusable
    fun providePermissionUtil(context: Context): PlatformPermissionUtil = PlatformPermissionUtilImpl(context)

    @Provides
    @Reusable
    fun provideDateUtils(context: Context): DateUtils = DateUtils(context)

    @Provides
    @Reusable
    fun provideMessageUtils(context: Context): MessageUtils = MessageUtils(context)
}
