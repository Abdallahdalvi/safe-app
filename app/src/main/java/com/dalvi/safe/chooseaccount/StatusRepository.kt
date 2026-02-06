/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.chooseaccount

import com.dalvi.safe.models.json.status.StatusOverall

interface StatusRepository {
    suspend fun setStatus(credentials: String, url: String): StatusOverall
}
