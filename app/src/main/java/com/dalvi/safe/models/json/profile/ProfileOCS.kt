/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Julius Linus <juliuslinus1@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.models.json.profile

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.dalvi.safe.models.json.generic.GenericMeta
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class ProfileOCS(
    @JsonField(name = ["meta"])
    var meta: GenericMeta? = null,
    @JsonField(name = ["data"])
    var data: Profile? = null
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, null)
}
