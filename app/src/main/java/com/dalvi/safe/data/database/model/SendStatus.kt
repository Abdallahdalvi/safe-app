/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.data.database.model

enum class SendStatus {
    PENDING,
    SENT_PENDING_ACK,
    FAILED
}
