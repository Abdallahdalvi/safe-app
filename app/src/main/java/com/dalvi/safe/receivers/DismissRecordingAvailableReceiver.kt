/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022-2023 Marcel Hibbe <dev@mhibbe.de>
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
import com.dalvi.safe.utils.bundle.BundleKeys
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_INTERNAL_USER_ID
import com.dalvi.safe.utils.bundle.BundleKeys.KEY_SYSTEM_NOTIFICATION_ID
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class DismissRecordingAvailableReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var currentUserProvider: CurrentUserProviderOld

    @Inject
    lateinit var ncApi: NcApi

    lateinit var context: Context
    lateinit var currentUser: User
    private var systemNotificationId: Int? = null
    private var link: String? = null

    init {
        SafeAppApplication.sharedApplication!!.componentApplication.inject(this)
    }

    override fun onReceive(receiveContext: Context, intent: Intent?) {
        context = receiveContext

        // NOTE - systemNotificationId is an internal ID used on the device only.
        // It is NOT the same as the notification ID used in communication with the server.
        systemNotificationId = intent!!.getIntExtra(KEY_SYSTEM_NOTIFICATION_ID, 0)
        link = intent.getStringExtra(BundleKeys.KEY_DISMISS_RECORDING_URL)

        val id = intent.getLongExtra(KEY_INTERNAL_USER_ID, currentUserProvider.currentUser.blockingGet().id!!)
        currentUser = userManager.getUserWithId(id).blockingGet()

        dismissNcRecordingAvailableNotification()
    }

    private fun dismissNcRecordingAvailableNotification() {
        val credentials = ApiUtils.getCredentials(currentUser.username, currentUser.token)

        ncApi.sendCommonDeleteRequest(credentials, link)
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
                    Log.e(TAG, "Failed to send dismiss for recording available", e)
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
        private val TAG = DismissRecordingAvailableReceiver::class.java.simpleName
    }
}
