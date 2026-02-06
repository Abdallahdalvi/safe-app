/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Julius Linus <julius.linus@nextcloud.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.translate.repositories.model

import android.os.Parcelable
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.dalvi.safe.models.json.generic.GenericMeta
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonObject
data class TranslateOCS(
    @JsonField(name = ["meta"])
    var meta: GenericMeta?,
    @JsonField(name = ["data"])
    var data: TranslateData?
) : Parcelable {
    // This constructor is added to work with the 'com.bluelinelabs.logansquare.annotation.JsonObject'
    constructor() : this(null, TranslateData())
}
