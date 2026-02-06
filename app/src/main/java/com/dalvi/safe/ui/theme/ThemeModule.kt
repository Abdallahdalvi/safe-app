/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.ui.theme

import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.dalvi.safe.dagger.modules.ContextModule
import com.dalvi.safe.utils.database.user.UserModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module(includes = [ContextModule::class, UserModule::class])
internal abstract class ThemeModule {

    @Binds
    @Reusable
    abstract fun bindMaterialSchemesProvider(provider: MaterialSchemesProviderImpl): MaterialSchemesProvider

    companion object {
        @Provides
        fun provideCurrentMaterialSchemes(schemesProvider: MaterialSchemesProvider): MaterialSchemes =
            schemesProvider.getMaterialSchemesForCurrentUser()
    }
}
