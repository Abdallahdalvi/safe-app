/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.repositories.unifiedsearch

import com.dalvi.safe.models.domain.SearchMessageEntry
import io.reactivex.Observable

interface UnifiedSearchRepository {
    data class UnifiedSearchResults<T>(val cursor: Int, val hasMore: Boolean, val entries: List<T>)

    fun searchMessages(
        credentials: String?,
        url: String,
        searchTerm: String,
        cursor: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE
    ): Observable<UnifiedSearchResults<SearchMessageEntry>>

    fun searchInRoom(
        credentials: String?,
        url: String,
        roomToken: String,
        searchTerm: String,
        cursor: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE
    ): Observable<UnifiedSearchResults<SearchMessageEntry>>

    companion object {
        private const val DEFAULT_PAGE_SIZE = 5
    }
}
