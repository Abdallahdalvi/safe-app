/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Andy Scherzinger <info@andy-scherzinger.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.remotefilebrowser.repositories

import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.remotefilebrowser.model.RemoteFileBrowserItem
import io.reactivex.Observable

interface RemoteFileBrowserItemsRepository {

    fun listFolder(user: User, path: String): Observable<List<RemoteFileBrowserItem>>
}
