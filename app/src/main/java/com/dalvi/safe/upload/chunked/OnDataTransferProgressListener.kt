/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.upload.chunked

interface OnDataTransferProgressListener {
    fun onTransferProgress(percentage: Int)
}
