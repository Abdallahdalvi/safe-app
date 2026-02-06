/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models.json.converters

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter
import com.dalvi.safe.models.json.reactions.ReactionVoter.ReactionActorType.DUMMY
import com.dalvi.safe.models.json.reactions.ReactionVoter.ReactionActorType.GUESTS
import com.dalvi.safe.models.json.reactions.ReactionVoter.ReactionActorType.USERS
import com.dalvi.safe.models.json.reactions.ReactionVoter

class EnumReactionActorTypeConverter : StringBasedTypeConverter<ReactionVoter.ReactionActorType>() {
    override fun getFromString(string: String): ReactionVoter.ReactionActorType =
        when (string) {
            "guests" -> GUESTS
            "users" -> USERS
            else -> DUMMY
        }

    override fun convertToString(`object`: ReactionVoter.ReactionActorType?): String {
        if (`object` == null) {
            return ""
        }

        return when (`object`) {
            GUESTS -> "guests"
            USERS -> "users"
            else -> ""
        }
    }
}
