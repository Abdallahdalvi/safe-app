/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

    fun vibratePersistent(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 500, 1000) // 0 delay, 500ms vibrate, 1000ms sleep
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0)) // 0 means repeat
    }

    fun stopVibration(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}
