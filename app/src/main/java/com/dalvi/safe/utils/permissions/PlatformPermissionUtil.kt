/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils.permissions

interface PlatformPermissionUtil {
    val privateBroadcastPermission: String
    fun isCameraPermissionGranted(): Boolean
    fun isMicrophonePermissionGranted(): Boolean
    fun isBluetoothPermissionGranted(): Boolean
    fun isFilesPermissionGranted(): Boolean
    fun isPostNotificationsPermissionGranted(): Boolean
    fun isLocationPermissionGranted(): Boolean
}
