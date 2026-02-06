/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.models.json.userAbsence

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.dalvi.safe.models.json.generic.GenericMeta
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class UserAbsenceOCS(
    @JsonField(name = ["meta"])
    var meta: GenericMeta?,
    @JsonField(name = ["data"])
    var data: UserAbsenceData?
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, null)
}
