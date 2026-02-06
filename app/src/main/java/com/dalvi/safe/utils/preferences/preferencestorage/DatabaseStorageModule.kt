/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2021 Tim Krüger <t@timkrueger.me>
 * SPDX-FileCopyrightText: 2017-2018 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.utils.preferences.preferencestorage

import android.text.TextUtils
import android.util.Log
import autodagger.AutoInjector
import com.dalvi.safe.api.NcApi
import com.dalvi.safe.api.NcApiCoroutines
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.application.SafeAppApplication.Companion.sharedApplication
import com.dalvi.safe.arbitrarystorage.ArbitraryStorageManager
import com.dalvi.safe.data.storage.model.ArbitraryStorage
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.utils.ApiUtils
import com.dalvi.safe.utils.ApiUtils.getConversationApiVersion
import com.dalvi.safe.utils.ApiUtils.getCredentials
import com.dalvi.safe.utils.ApiUtils.getUrlForMessageExpiration
import com.dalvi.safe.utils.ApiUtils.getUrlForRoomNotificationCalls
import com.dalvi.safe.utils.ApiUtils.getUrlForRoomNotificationLevel
import com.dalvi.safe.utils.CapabilitiesUtil.hasSpreedFeatureCapability
import com.dalvi.safe.utils.SpreedFeatures
import com.dalvi.safe.utils.UserIdUtils.getIdForUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class DatabaseStorageModule(conversationUser: User, conversationToken: String) {

    @JvmField
    @Inject
    var arbitraryStorageManager: ArbitraryStorageManager? = null

    @JvmField
    @Inject
    var ncApi: NcApi? = null

    @JvmField
    @Inject
    var ncApiCoroutines: NcApiCoroutines? = null

    private var messageExpiration = 0
    private val conversationUser: User
    private val conversationToken: String
    private val accountIdentifier: Long

    private var lobbyValue = false

    private var messageNotificationLevel: String? = null

    init {
        sharedApplication!!.componentApplication.inject(this)

        this.conversationUser = conversationUser
        this.accountIdentifier = getIdForUser(conversationUser)
        this.conversationToken = conversationToken
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    suspend fun saveBoolean(key: String, value: Boolean) {
        if ("call_notifications_switch" == key) {
            val apiVersion = getConversationApiVersion(conversationUser, intArrayOf(ApiUtils.API_V4))
            val url = getUrlForRoomNotificationCalls(apiVersion, conversationUser.baseUrl, conversationToken)
            val credentials = getCredentials(conversationUser.username, conversationUser.token)
            val notificationLevel = if (value) 1 else 0
            withContext(Dispatchers.IO) {
                try {
                    ncApiCoroutines!!.notificationCalls(credentials!!, url, notificationLevel)
                    Log.d(TAG, "Toggled notification calls")
                } catch (e: Exception) {
                    Log.e(TAG, "Error when trying to toggle notification calls", e)
                }
            }
        }
        if ("lobby_switch" != key) {
            arbitraryStorageManager!!.storeStorageSetting(
                accountIdentifier,
                key,
                value.toString(),
                conversationToken
            )
        } else {
            lobbyValue = value
        }
    }

    @Suppress("Detekt.TooGenericExceptionCaught")
    suspend fun saveString(key: String, value: String) {
        when (key) {
            "conversation_settings_dropdown" -> {
                try {
                    val apiVersion = getConversationApiVersion(conversationUser, intArrayOf(API_VERSION_4))
                    val trimmedValue = value.replace("expire_", "")
                    val valueInt = trimmedValue.toInt()
                    withContext(Dispatchers.IO) {
                        ncApiCoroutines!!.setMessageExpiration(
                            getCredentials(conversationUser.username, conversationUser.token)!!,
                            getUrlForMessageExpiration(apiVersion, conversationUser.baseUrl, conversationToken),
                            valueInt
                        )
                        messageExpiration = valueInt
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "Error when trying to set message expiration", exception)
                }
            }
            "conversation_info_message_notifications_dropdown" -> {
                try {
                    if (hasSpreedFeatureCapability(
                            conversationUser.capabilities!!.spreedCapability!!,
                            SpreedFeatures.NOTIFICATION_LEVELS
                        )
                    ) {
                        if (TextUtils.isEmpty(messageNotificationLevel) || messageNotificationLevel != value) {
                            val intValue = when (value) {
                                "never" -> NOTIFICATION_NEVER
                                "mention" -> NOTIFICATION_MENTION
                                "always" -> NOTIFICATION_ALWAYS
                                else -> 0
                            }
                            val apiVersion = getConversationApiVersion(conversationUser, intArrayOf(ApiUtils.API_V4, 1))
                            withContext(Dispatchers.IO) {
                                ncApiCoroutines!!.setNotificationLevel(
                                    getCredentials(conversationUser.username, conversationUser.token)!!,
                                    getUrlForRoomNotificationLevel(
                                        apiVersion,
                                        conversationUser.baseUrl,
                                        conversationToken
                                    ),
                                    intValue
                                )
                                messageNotificationLevel = value
                            }
                        } else {
                            messageNotificationLevel = value
                        }
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "Error trying to set notification level", exception)
                }
            }
            else -> {
                arbitraryStorageManager!!.storeStorageSetting(accountIdentifier, key, value, conversationToken)
            }
        }
    }

    fun getBoolean(key: String, defaultVal: Boolean): Boolean =
        if ("lobby_switch" == key) {
            lobbyValue
        } else {
            arbitraryStorageManager!!
                .getStorageSetting(accountIdentifier, key, conversationToken)
                .map { arbitraryStorage: ArbitraryStorage -> arbitraryStorage.value.toBoolean() }
                .blockingGet(defaultVal)
        }

    fun getString(key: String, defaultVal: String): String? =
        if ("conversation_settings_dropdown" == key) {
            when (messageExpiration) {
                EXPIRE_4_WEEKS -> "expire_2419200"
                EXPIRE_7_DAYS -> "expire_604800"
                EXPIRE_1_DAY -> "expire_86400"
                EXPIRE_8_HOURS -> "expire_28800"
                EXPIRE_1_HOUR -> "expire_3600"
                else -> "expire_0"
            }
        } else if ("conversation_info_message_notifications_dropdown" == key) {
            messageNotificationLevel
        } else {
            arbitraryStorageManager!!
                .getStorageSetting(accountIdentifier, key, conversationToken)
                .map(ArbitraryStorage::value)
                .blockingGet(defaultVal)
        }

    fun setMessageExpiration(messageExpiration: Int) {
        this.messageExpiration = messageExpiration
    }

    companion object {
        private const val TAG = "DatabaseStorageModule"
        private const val EXPIRE_1_HOUR = 3600
        private const val EXPIRE_8_HOURS = 28800
        private const val EXPIRE_1_DAY = 86400
        private const val EXPIRE_7_DAYS = 604800
        private const val EXPIRE_4_WEEKS = 2419200
        private const val NOTIFICATION_NEVER = 3
        private const val NOTIFICATION_MENTION = 2
        private const val NOTIFICATION_ALWAYS = 1
        private const val API_VERSION_4 = 4
    }
}
