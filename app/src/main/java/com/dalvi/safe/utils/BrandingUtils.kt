/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils

import android.content.Context

object BrandingUtils {
    private const val ORIGINAL_NEXTCLOUD_TALK_APPLICATION_ID = "com.dalvi.safe2"

    fun isOriginalSafe AppClient(context: Context): Boolean =
        context.packageName.equals(ORIGINAL_NEXTCLOUD_TALK_APPLICATION_ID)
}
