/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.invitation.data

import com.dalvi.safe.data.user.model.User
import io.reactivex.Observable

interface InvitationsRepository {
    fun fetchInvitations(user: User): Observable<InvitationsModel>
    fun acceptInvitation(user: User, invitation: Invitation): Observable<InvitationActionModel>
    fun rejectInvitation(user: User, invitation: Invitation): Observable<InvitationActionModel>
    suspend fun getInvitations(user: User): InvitationsModel
}
