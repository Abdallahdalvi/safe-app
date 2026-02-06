/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017-2020 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.data.source.local.converters

import androidx.room.TypeConverter
import com.bluelinelabs.logansquare.LoganSquare
import com.dalvi.safe.models.json.push.PushConfigurationState

class PushConfigurationConverter {

    @TypeConverter
    fun fromPushConfigurationToString(pushConfiguration: PushConfigurationState?): String =
        if (pushConfiguration == null) {
            ""
        } else {
            LoganSquare.serialize(pushConfiguration)
        }

    @TypeConverter
    fun fromStringToPushConfiguration(value: String?): PushConfigurationState? {
        return if (value.isNullOrBlank()) {
            null
        } else {
            return LoganSquare.parse(value, PushConfigurationState::class.java)
        }
    }
}
