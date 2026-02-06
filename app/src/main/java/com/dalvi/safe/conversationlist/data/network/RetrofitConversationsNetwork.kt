/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.conversationlist.data.network

import com.dalvi.safe.api.NcApi
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.conversations.Conversation
import com.dalvi.safe.utils.ApiUtils
import io.reactivex.Observable

class RetrofitConversationsNetwork(private val ncApi: NcApi) : ConversationsNetworkDataSource {
    override fun getRooms(user: User, url: String, includeStatus: Boolean): Observable<List<Conversation>> {
        val credentials: String = ApiUtils.getCredentials(user.username, user.token)!!
        val apiVersion = ApiUtils.getConversationApiVersion(user, intArrayOf(ApiUtils.API_V4, ApiUtils.API_V3, 1))

        return ncApi.getRooms(
            credentials,
            ApiUtils.getUrlForRooms(apiVersion, user.baseUrl!!),
            includeStatus
        ).map { it ->
            it.ocs?.data?.map { it } ?: listOf()
        }
    }
}
