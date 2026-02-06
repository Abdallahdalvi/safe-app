/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro.brey@nextcloud.com>
 * SPDX-FileCopyrightText: 2017 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils.database.user

import com.dalvi.safe.dagger.modules.DatabaseModule
import com.dalvi.safe.data.user.UsersRepository
import com.dalvi.safe.users.UserManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [DatabaseModule::class])
abstract class UserModule {

    @Binds
    abstract fun bindCurrentUserProviderOld(
        currentUserProviderOldImpl: CurrentUserProviderOldImpl
    ): CurrentUserProviderOld

    @Binds
    abstract fun bindCurrentUserProvider(currentUserProviderImpl: CurrentUserProviderImpl): CurrentUserProvider

    companion object {
        @Provides
        fun provideUserManager(userRepository: UsersRepository): UserManager = UserManager(userRepository)
    }
}
