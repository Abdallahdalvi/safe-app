/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils.database.user

import com.dalvi.safe.data.user.model.User
import io.reactivex.Maybe

@Deprecated("Use CurrentUserProvider instead")
interface CurrentUserProviderOld {
    val currentUser: Maybe<User>
}
