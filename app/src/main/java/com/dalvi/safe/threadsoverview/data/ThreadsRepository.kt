/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.threadsoverview.data

import com.dalvi.safe.models.json.threads.ThreadOverall
import com.dalvi.safe.models.json.threads.ThreadsOverall

interface ThreadsRepository {

    suspend fun getThreads(credentials: String, url: String, limit: Int?): ThreadsOverall

    suspend fun getThread(credentials: String, url: String): ThreadOverall

    suspend fun setThreadNotificationLevel(credentials: String, url: String, level: Int): ThreadOverall
}
