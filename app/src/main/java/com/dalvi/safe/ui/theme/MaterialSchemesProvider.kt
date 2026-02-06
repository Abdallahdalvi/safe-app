/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.ui.theme

import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.capabilities.Capabilities

interface MaterialSchemesProvider {
    fun getMaterialSchemesForUser(user: User?): MaterialSchemes
    fun getMaterialSchemesForCapabilities(capabilities: Capabilities?): MaterialSchemes
    fun getMaterialSchemesForCurrentUser(): MaterialSchemes
}
