/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models.domain

data class SearchMessageEntry(
    val searchTerm: String,
    val thumbnailURL: String?,
    val title: String,
    val messageExcerpt: String,
    val conversationToken: String,
    val threadId: String?,
    val messageId: String?
)
