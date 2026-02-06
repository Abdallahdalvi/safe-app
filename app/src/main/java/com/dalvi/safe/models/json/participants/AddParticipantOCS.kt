/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models.json.participants

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.dalvi.safe.models.json.conversations.Conversation
import com.dalvi.safe.models.json.generic.GenericMeta
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class AddParticipantOCS(
    @JsonField(name = ["meta"])
    var meta: GenericMeta?,
    /* Returned room will have only type set, and sometimes even that will be null */
    @JsonField(name = ["data"])
    var data: Conversation? = null
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, null)
}
