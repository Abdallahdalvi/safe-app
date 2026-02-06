/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.threadsoverview.data

import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.models.json.threads.ThreadOverall
import com.dalvi.safe.models.json.threads.ThreadsOverall
import javax.inject.Inject

class ThreadsRepositoryImpl @Inject constructor(private val ncApiCoroutines: NcApiCoroutines) : ThreadsRepository {

    override suspend fun getThreads(credentials: String, url: String, limit: Int?): ThreadsOverall =
        ncApiCoroutines.getThreads(credentials, url, limit)

    override suspend fun getThread(credentials: String, url: String): ThreadOverall =
        ncApiCoroutines.getThread(credentials, url)

    override suspend fun setThreadNotificationLevel(credentials: String, url: String, level: Int): ThreadOverall =
        ncApiCoroutines.setThreadNotificationLevel(credentials, url, level)

    companion object {
        val TAG = ThreadsRepositoryImpl::class.simpleName
    }
}
