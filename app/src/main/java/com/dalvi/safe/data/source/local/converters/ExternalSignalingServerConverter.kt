/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.data.source.local.converters

import androidx.room.TypeConverter
import com.bluelinelabs.logansquare.LoganSquare
import com.dalvi.safe.models.ExternalSignalingServer

class ExternalSignalingServerConverter {

    @TypeConverter
    fun fromExternalSignalingServerToString(externalSignalingServer: ExternalSignalingServer?): String =
        if (externalSignalingServer == null) {
            ""
        } else {
            LoganSquare.serialize(externalSignalingServer)
        }

    @TypeConverter
    fun fromStringToExternalSignalingServer(value: String): ExternalSignalingServer? {
        return if (value.isBlank()) {
            null
        } else {
            return LoganSquare.parse(value, ExternalSignalingServer::class.java)
        }
    }
}
