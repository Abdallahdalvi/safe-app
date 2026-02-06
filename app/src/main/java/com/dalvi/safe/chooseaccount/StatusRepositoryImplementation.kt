/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.chooseaccount

import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.models.json.status.StatusOverall
import javax.inject.Inject

class StatusRepositoryImplementation @Inject constructor(private val ncApiCoroutines: NcApiCoroutines) :
    StatusRepository {

    override suspend fun setStatus(credentials: String, url: String): StatusOverall {
        val statusOverall = ncApiCoroutines.status(credentials, url)
        return statusOverall
    }
}
