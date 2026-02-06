/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.repositories.callrecording

import com.dalvi.safe.api.NcApi
import com.dalvi.safe.models.domain.StartCallRecordingModel
import com.dalvi.safe.models.domain.StopCallRecordingModel
import com.dalvi.safe.models.json.generic.GenericMeta
import io.reactivex.Observable

class CallRecordingRepositoryImpl(private val ncApi: NcApi) : CallRecordingRepository {

    override fun startRecording(
        credentials: String?,
        url: String,
        roomToken: String
    ): Observable<StartCallRecordingModel> =
        ncApi.startRecording(
            credentials,
            url,
            1
        ).map { mapToStartCallRecordingModel(it.ocs?.meta!!) }

    override fun stopRecording(
        credentials: String?,
        url: String,
        roomToken: String
    ): Observable<StopCallRecordingModel> =
        ncApi.stopRecording(
            credentials,
            url
        ).map { mapToStopCallRecordingModel(it.ocs?.meta!!) }

    private fun mapToStartCallRecordingModel(response: GenericMeta): StartCallRecordingModel {
        val success = response.statusCode == HTTP_OK
        return StartCallRecordingModel(
            success
        )
    }

    private fun mapToStopCallRecordingModel(response: GenericMeta): StopCallRecordingModel {
        val success = response.statusCode == HTTP_OK
        return StopCallRecordingModel(
            success
        )
    }

    companion object {
        private const val HTTP_OK: Int = 200
    }
}
