/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017-2020 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.data.user.model

import android.os.Parcelable
import android.util.Log
import com.dalvi.safe.models.ExternalSignalingServer
import com.dalvi.safe.models.json.capabilities.Capabilities
import com.dalvi.safe.models.json.capabilities.ServerVersion
import com.dalvi.safe.models.json.push.PushConfigurationState
import com.dalvi.safe.utils.ApiUtils
import kotlinx.parcelize.Parcelize
import java.lang.Boolean.FALSE

@Parcelize
data class User(
    var id: Long? = null,
    var userId: String? = null,
    var username: String? = null,
    var baseUrl: String? = null,
    var token: String? = null,
    var displayName: String? = null,
    var pushConfigurationState: PushConfigurationState? = null,
    var capabilities: Capabilities? = null,
    var serverVersion: ServerVersion? = null,
    var clientCertificate: String? = null,
    var externalSignalingServer: ExternalSignalingServer? = null,
    var current: Boolean = FALSE,
    var scheduledForDeletion: Boolean = FALSE
) : Parcelable {

    fun getCredentials(): String = ApiUtils.getCredentials(username, token)!!

    fun hasSpreedFeatureCapability(capabilityName: String): Boolean {
        if (capabilities == null) {
            Log.e(TAG, "Capabilities are null in hasSpreedFeatureCapability. false is returned for capability check")
        }
        return capabilities?.spreedCapability?.features?.contains(capabilityName) ?: false
    }

    companion object {
        private val TAG = User::class.simpleName
    }
}
