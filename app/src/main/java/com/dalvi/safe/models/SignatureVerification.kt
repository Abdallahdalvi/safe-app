/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.models

import android.os.Parcelable
import com.dalvi.safe.data.user.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignatureVerification(var signatureValid: Boolean = false, var user: User? = null) : Parcelable
