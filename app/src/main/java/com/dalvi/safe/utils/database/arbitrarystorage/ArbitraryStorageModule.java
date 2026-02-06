/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017-2018 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils.database.arbitrarystorage;

import com.dalvi.safe.application.SafeAppApplication;
import com.dalvi.safe.arbitrarystorage.ArbitraryStorageManager;
import com.dalvi.safe.dagger.modules.DatabaseModule;
import com.dalvi.safe.data.storage.ArbitraryStoragesRepository;

import javax.inject.Inject;

import autodagger.AutoInjector;
import dagger.Module;
import dagger.Provides;

@Module(includes = DatabaseModule.class)
@AutoInjector(SafeAppApplication.class)
public class ArbitraryStorageModule {

    @Inject
    public ArbitraryStorageModule() {
    }

    @Provides
    public ArbitraryStorageManager provideArbitraryStorageManager(ArbitraryStoragesRepository repository) {
        return new ArbitraryStorageManager(repository);
    }
}
