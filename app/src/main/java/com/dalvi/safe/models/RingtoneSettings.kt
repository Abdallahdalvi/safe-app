/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017-2018 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models

import android.net.Uri
import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.dalvi.safe.models.json.converters.UriTypeConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class RingtoneSettings(
    @JsonField(name = ["ringtoneUri"], typeConverter = UriTypeConverter::class)
    var ringtoneUri: Uri? = null,
    @JsonField(name = ["ringtoneName"])
    var ringtoneName: String? = null
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, null)
}
