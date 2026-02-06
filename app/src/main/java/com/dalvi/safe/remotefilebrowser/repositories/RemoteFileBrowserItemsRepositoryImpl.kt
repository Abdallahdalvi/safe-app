/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.remotefilebrowser.repositories

import com.dalvi.safe.filebrowser.webdav.ReadFolderListingOperation
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.remotefilebrowser.model.RemoteFileBrowserItem
import io.reactivex.Observable
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteFileBrowserItemsRepositoryImpl @Inject constructor(private val okHttpClient: OkHttpClient) :
    RemoteFileBrowserItemsRepository {

    override fun listFolder(user: User, path: String): Observable<List<RemoteFileBrowserItem>> {
        return Observable.fromCallable {
            val operation =
                ReadFolderListingOperation(
                    okHttpClient,
                    user,
                    path,
                    1
                )
            val davResponse = operation.readRemotePath()
            if (davResponse.getData() != null) {
                return@fromCallable davResponse.getData() as List<RemoteFileBrowserItem>
            }
            return@fromCallable emptyList()
        }
    }
}
