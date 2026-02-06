/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.invitation.data

import com.dalvi.safe.data.user.model.User

data class InvitationsModel(var user: User, var invitations: List<Invitation>)
