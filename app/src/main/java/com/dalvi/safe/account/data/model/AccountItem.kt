/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.account.data.model

import com.dalvi.safe.data.user.model.User

data class AccountItem(val user: User, val pendingInvitation: Int)
