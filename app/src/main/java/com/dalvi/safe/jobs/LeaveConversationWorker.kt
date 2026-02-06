/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-FileCopyrightText: 2017-2018 Mario Danic <mario@lovelyhq.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.jobs

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import autodagger.AutoInjector
import com.google.common.util.concurrent.ListenableFuture
import com.dalvi.safe.api.NcApi
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.models.json.generic.GenericOverall
import com.dalvi.safe.users.UserManager
import com.dalvi.safe.utils.ApiUtils
import com.dalvi.safe.utils.ApiUtils.getConversationApiVersion
import com.dalvi.safe.utils.ApiUtils.getCredentials
import com.dalvi.safe.utils.ApiUtils.getUrlForParticipantsSelf
import com.dalvi.safe.utils.bundle.BundleKeys
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

@SuppressLint("RestrictedApi")
@AutoInjector(SafeAppApplication::class)
class LeaveConversationWorker(context: Context, workerParams: WorkerParameters) :
    ListenableWorker(context, workerParams) {

    @Inject
    lateinit var ncApi: NcApi

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var currentUserProvider: CurrentUserProviderOld

    private val result = SettableFuture.create<Result>()

    override fun startWork(): ListenableFuture<Result> {
        SafeAppApplication.sharedApplication!!.componentApplication.inject(this)
        val conversationToken = inputData.getString(BundleKeys.KEY_ROOM_TOKEN)
        val currentUser = currentUserProvider.currentUser.blockingGet()

        if (currentUser != null && conversationToken != null) {
            val credentials = getCredentials(currentUser.username, currentUser.token)
            val apiVersion = getConversationApiVersion(currentUser, intArrayOf(ApiUtils.API_V4, 1))

            ncApi.removeSelfFromRoom(
                credentials,
                getUrlForParticipantsSelf(apiVersion, currentUser.baseUrl, conversationToken)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<GenericOverall?> {
                    override fun onSubscribe(d: Disposable) {
                        // unused atm
                    }

                    override fun onNext(p0: GenericOverall) {
                        // unused atm
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "Failed to remove self from room", e)
                        val httpException = e as? HttpException
                        val errorData = if (httpException?.code() == HTTP_ERROR_CODE_400) {
                            Data.Builder()
                                .putString("error_type", ERROR_NO_OTHER_MODERATORS_OR_OWNERS_LEFT)
                                .build()
                        } else {
                            Data.Builder()
                                .putString("error_type", ERROR_OTHER)
                                .build()
                        }
                        result.set(Result.failure(errorData))
                    }

                    override fun onComplete() {
                        result.set(Result.success())
                    }
                })
        } else {
            result.set(Result.failure())
        }

        return result
    }

    companion object {
        private const val TAG = "LeaveConversationWorker"
        const val ERROR_NO_OTHER_MODERATORS_OR_OWNERS_LEFT = "NO_OTHER_MODERATORS_OR_OWNERS_LEFT"
        const val ERROR_OTHER = "ERROR_OTHER"
        const val HTTP_ERROR_CODE_400 = 400
    }
}
