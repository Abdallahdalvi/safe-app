/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2017-2018 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dalvi.safe.utils.NotificationUtils

class PackageReplacedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null &&
            intent.action != null &&
            intent.action == "android.intent.action.MY_PACKAGE_REPLACED"
        ) {
            NotificationUtils.removeOldNotificationChannels(context)
        }
    }
}
