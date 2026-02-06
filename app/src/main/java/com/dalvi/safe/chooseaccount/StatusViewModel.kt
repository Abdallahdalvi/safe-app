/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Sowjanya Kota <sowjanya.kch@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.chooseaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalvi.safe.models.json.status.StatusOverall
import com.dalvi.safe.utils.ApiUtils
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatusViewModel @Inject constructor(
    private val repository: StatusRepository,
    private val currentUserProvider: CurrentUserProviderOld
) : ViewModel() {

    private val currentUser = currentUserProvider.currentUser.blockingGet()
    private val credentials = ApiUtils.getCredentials(currentUser.username, currentUser.token)

    private val _statusViewState = MutableStateFlow<StatusUiState>(StatusUiState.None)
    val statusViewState: StateFlow<StatusUiState> = _statusViewState

    @Suppress("Detekt.TooGenericExceptionCaught")
    fun getStatus() {
        viewModelScope.launch {
            try {
                val url = ApiUtils.getUrlForStatus(currentUser.baseUrl!!)
                val status = repository.setStatus(
                    credentials!!,
                    url
                )
                _statusViewState.value = StatusUiState.Success(status)
            } catch (exception: Exception) {
                _statusViewState.value = StatusUiState.Error(exception.message ?: "")
            }
        }
    }
}

sealed class StatusUiState {
    data object None : StatusUiState()
    open class Success(val status: StatusOverall) : StatusUiState()
    open class Error(val message: String) : StatusUiState()
}
