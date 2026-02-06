/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.polls.model

import com.dalvi.safe.models.json.participants.Participant

data class PollDetails(
    val actorType: Participant.ActorType?,
    val actorId: String?,
    val actorDisplayName: String?,
    val optionId: Int
)
