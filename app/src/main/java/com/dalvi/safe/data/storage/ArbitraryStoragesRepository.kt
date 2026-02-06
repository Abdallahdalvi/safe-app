/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.data.storage

import com.dalvi.safe.data.storage.model.ArbitraryStorage
import com.dalvi.safe.data.storage.model.ArbitraryStorageEntity
import io.reactivex.Maybe

interface ArbitraryStoragesRepository {
    fun getStorageSetting(accountIdentifier: Long, key: String, objectString: String): Maybe<ArbitraryStorage>
    fun deleteArbitraryStorage(accountIdentifier: Long): Int
    fun saveArbitraryStorage(arbitraryStorage: ArbitraryStorage): Long
    fun getAll(): Maybe<List<ArbitraryStorageEntity>>
}
