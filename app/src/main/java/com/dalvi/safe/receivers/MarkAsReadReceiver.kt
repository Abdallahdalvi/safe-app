/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2022 Dariusz Olszewski <starypatyk@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import autodagger.AutoInjector
import com.dalvi.safe.api.NcApi
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.models.json.generic.GenericOverall
import com.dalvi.safe.users.UserManager
import com.dalvi.safe.utils.ApiUtils
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_INTERNAL_USER_ID
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_MESSAGE_ID
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_ROOM_TOKEN
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_SYSTEM_NOTIFICATION_ID
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class MarkAsReadReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var currentUserProvider: CurrentUserProviderOld

    @Inject
    lateinit var ncApi: NcApi

    lateinit var context: Context
    lateinit var currentUser: User
    private var systemNotificationId: Int? = null
    private var roomToken: String? = null
    private var messageId: Int = 0

    init {
        SafeAppApplication.sharedApplication!!.componentApplication.inject(this)
    }

    override fun onReceive(receiveContext: Context, intent: Intent?) {
        context = receiveContext

        // NOTE - systemNotificationId is an internal ID used on the device only.
        // It is NOT the same as the notification ID used in communication with the server.
        systemNotificationId = intent!!.getIntExtra(KEY_SYSTEM_NOTIFICATION_ID, 0)
        roomToken = intent.getStringExtra(KEY_ROOM_TOKEN)
        messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)

        val id = intent.getLongExtra(KEY_INTERNAL_USER_ID, currentUserProvider.currentUser.blockingGet().id!!)
        currentUser = userManager.getUserWithId(id).blockingGet()

        markAsRead()
    }

    private fun markAsRead() {
        val credentials = ApiUtils.getCredentials(currentUser.username, currentUser.token)
        val apiVersion = ApiUtils.getChatApiVersion(currentUser.capabilities!!.spreedCapability!!, intArrayOf(1))
        val url = ApiUtils.getUrlForChatReadMarker(
            apiVersion,
            currentUser.baseUrl!!,
            roomToken!!
        )

        ncApi.setChatReadMarker(credentials, url, messageId)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<GenericOverall> {
                override fun onSubscribe(d: Disposable) {
                    // unused atm
                }

                override fun onNext(genericOverall: GenericOverall) {
                    cancelNotification(systemNotificationId!!)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "Failed to set chat read marker", e)
                }

                override fun onComplete() {
                    // unused atm
                }
            })
    }

    private fun cancelNotification(notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val TAG = "MarkAsReadReceiver"
    }
}
