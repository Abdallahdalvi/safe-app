/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Tim Krüger <t@timkrueger.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.shareditems.model

class SharedItems(
    val items: List<SharedItem>,
    val type: SharedItemType,
    var lastSeenId: Int?,
    var moreItemsExisting: Boolean
)
