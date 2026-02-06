/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.conversationlist.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalvi.safe.R
import com.dalvi.safe.adapters.items.ContactItem
import com.dalvi.safe.adapters.items.ConversationItem
import com.dalvi.safe.adapters.items.GenericTextHeaderItem
import com.dalvi.safe.adapters.items.LoadMoreResultsItem
import com.dalvi.safe.adapters.items.MessageResultItem
import com.dalvi.safe.arbitrarystorage.ArbitraryStorageManager
import com.dalvi.safe.contacts.ContactsRepository
import com.dalvi.safe.conversationlist.data.OfflineConversationsRepository
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.invitation.data.InvitationsModel
import com.dalvi.safe.invitation.data.InvitationsRepository
import com.dalvi.safe.messagesearch.MessageSearchHelper
import com.dalvi.safe.messagesearch.MessageSearchHelper.MessageSearchResults
import com.dalvi.safe.models.domain.ConversationModel
import com.dalvi.safe.models.json.conversations.Conversation
import com.dalvi.safe.models.json.converters.EnumActorTypeConverter
import com.dalvi.safe.models.json.participants.Participant
import com.dalvi.safe.openconversations.data.OpenConversationsRepository
import com.dalvi.safe.repositories.unifiedsearch.UnifiedSearchRepository
import com.dalvi.safe.threadsoverview.data.ThreadsRepository
import com.dalvi.safe.ui.theme.ViewThemeUtils
import com.dalvi.safe.users.UserManager
import com.dalvi.safe.utils.ApiUtils
import com.dalvi.safe.utils.CapabilitiesUtil.hasSpreedFeatureCapability
import com.dalvi.safe.utils.SpreedFeatures
import com.dalvi.safe.utils.UserIdUtils
import com.dalvi.safe.utils.database.user.CurrentUserProviderOld
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ConversationsListViewModel @Inject constructor(
    private val repository: OfflineConversationsRepository,
    private val threadsRepository: ThreadsRepository,
    private val currentUserProvider: CurrentUserProviderOld,
    private val openConversationsRepository: OpenConversationsRepository,
    private val contactsRepository: ContactsRepository,
    private val viewThemeUtils: ViewThemeUtils,
    private val unifiedSearchRepository: UnifiedSearchRepository,
    private val invitationsRepository: InvitationsRepository,
    private val arbitraryStorageManager: ArbitraryStorageManager,
    var userManager: UserManager
) : ViewModel() {

    private val _currentUser = currentUserProvider.currentUser.blockingGet()
    val currentUser: User = _currentUser
    val credentials = ApiUtils.getCredentials(_currentUser.username, _currentUser.token) ?: ""

    private val searchHelper = MessageSearchHelper(unifiedSearchRepository, currentUser)

    sealed interface ViewState

    sealed class ThreadsExistUiState {
        data object None : ThreadsExistUiState()
        data class Success(val threadsExistence: Boolean?) : ThreadsExistUiState()
        data class Error(val exception: Exception) : ThreadsExistUiState()
    }

    private val _threadsExistState = MutableStateFlow<ThreadsExistUiState>(ThreadsExistUiState.None)
    val threadsExistState: StateFlow<ThreadsExistUiState> = _threadsExistState

    sealed class OpenConversationsUiState {
        data object None : OpenConversationsUiState()
        data class Success(val conversations: List<Conversation>) : OpenConversationsUiState()
        data class Error(val exception: Throwable) : OpenConversationsUiState()
    }

    private val _openConversationsState = MutableStateFlow<OpenConversationsUiState>(OpenConversationsUiState.None)
    val openConversationsState: StateFlow<OpenConversationsUiState> = _openConversationsState

    object GetRoomsStartState : ViewState
    object GetRoomsErrorState : ViewState
    open class GetRoomsSuccessState(val listIsNotEmpty: Boolean) : ViewState

    private val _getRoomsViewState: MutableLiveData<ViewState> = MutableLiveData(GetRoomsStartState)
    val getRoomsViewState: LiveData<ViewState>
        get() = _getRoomsViewState

    val getRoomsFlow = repository.roomListFlow
        .onEach { list ->
            _getRoomsViewState.value = GetRoomsSuccessState(list.isNotEmpty())
        }.catch {
            _getRoomsViewState.value = GetRoomsErrorState
        }

    val getRoomsStateFlow = repository
        .roomListFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    object GetFederationInvitationsStartState : ViewState
    object GetFederationInvitationsErrorState : ViewState

    open class GetFederationInvitationsSuccessState(val showInvitationsHint: Boolean) : ViewState

    private val _getFederationInvitationsViewState: MutableLiveData<ViewState> =
        MutableLiveData(GetFederationInvitationsStartState)
    val getFederationInvitationsViewState: LiveData<ViewState>
        get() = _getFederationInvitationsViewState

    object ShowBadgeStartState : ViewState
    object ShowBadgeErrorState : ViewState
    open class ShowBadgeSuccessState(val showBadge: Boolean) : ViewState

    private val _showBadgeViewState: MutableLiveData<ViewState> = MutableLiveData(ShowBadgeStartState)
    val showBadgeViewState: LiveData<ViewState>
        get() = _showBadgeViewState

    fun getFederationInvitations() {
        _getFederationInvitationsViewState.value = GetFederationInvitationsStartState
        _showBadgeViewState.value = ShowBadgeStartState

        userManager.users.blockingGet()?.forEach {
            invitationsRepository.fetchInvitations(it)
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(FederatedInvitationsObserver())
        }
    }

    private val _searchResultFlow: MutableStateFlow<List<AbstractFlexibleItem<*>>> = MutableStateFlow(listOf())
    val searchResultFlow = _searchResultFlow.asStateFlow()

    @Suppress("LongMethod")
    fun getSearchQuery(context: Context, filter: String) {
        val conversationsTitle: String = context.resources!!.getString(R.string.conversations)
        val conversationsHeader = GenericTextHeaderItem(conversationsTitle, viewThemeUtils)
        val openConversationsTitle = context.resources!!.getString(R.string.openConversations)
        val openConversationsHeader = GenericTextHeaderItem(openConversationsTitle, viewThemeUtils)
        val usersTitle = context.resources!!.getString(R.string.nc_user)
        val usersHeader = GenericTextHeaderItem(usersTitle, viewThemeUtils)
        val actorTypeConverter = EnumActorTypeConverter()

        viewModelScope.launch {
            combine(
                getRoomsStateFlow.map { list ->
                    list.map { conversation ->
                        ConversationItem(
                            conversation,
                            currentUser,
                            context,
                            conversationsHeader,
                            viewThemeUtils
                        )
                    }.filter { it.model.displayName.contains(filter, true) }
                },
                openConversationsRepository.fetchOpenConversationsFlow(currentUser, filter)
                    .map { list ->
                        list.map { conversation ->
                            ConversationItem(
                                ConversationModel.mapToConversationModel(conversation, currentUser),
                                currentUser,
                                context,
                                openConversationsHeader,
                                viewThemeUtils
                            )
                        }
                    },
                contactsRepository.getContactsFlow(currentUser, filter)
                    .map { list ->
                        list.map { autocompleteUser ->
                            val participant = Participant()
                            participant.actorId = autocompleteUser.id
                            participant.actorType = actorTypeConverter.getFromString(autocompleteUser.source)
                            participant.displayName = autocompleteUser.label

                            ContactItem(
                                participant,
                                currentUser,
                                usersHeader,
                                viewThemeUtils
                            )
                        }
                    },
                getMessagesFlow(filter)
                    .map { (messages, hasMore) ->
                        messages.mapIndexed { index, entry ->
                            MessageResultItem(
                                context,
                                currentUser,
                                entry,
                                index == 0,
                                viewThemeUtils = viewThemeUtils
                            )
                        }.let {
                            if (hasMore) {
                                it + LoadMoreResultsItem
                            } else {
                                it
                            }
                        }
                    }
            ) { conversations, openConversations, users, messages ->
                conversations + openConversations + users + messages
            }.collect { searchResults ->
                _searchResultFlow.emit(searchResults)
            }
        }
    }

    private fun getMessagesFlow(search: String): Flow<MessageSearchResults> =
        searchHelper.startMessageSearch(search).subscribeOn(Schedulers.io()).asFlow()

    fun loadMoreMessages(context: Context) {
        viewModelScope.launch {
            searchHelper.loadMore()
                ?.asFlow()
                ?.map { (messages, hasMore) ->
                    messages.map { entry ->
                        MessageResultItem(
                            context,
                            currentUser,
                            entry,
                            false,
                            viewThemeUtils = viewThemeUtils
                        )
                    }.let {
                        if (hasMore) {
                            it + LoadMoreResultsItem
                        } else {
                            it
                        }
                    }
                }?.collect { messages ->
                    _searchResultFlow.update {
                        it.filter { item ->
                            item !is LoadMoreResultsItem && item !is MessageResultItem
                        } + messages
                    }
                }
        }
    }

    fun getRooms(user: User) {
        val startNanoTime = System.nanoTime()
        Log.d(TAG, "fetchData - getRooms - calling: $startNanoTime")
        repository.getRooms(user)
    }

    fun checkIfThreadsExist() {
        val limitForFollowedThreadsExistenceCheck = 1
        val accountId = UserIdUtils.getIdForUser(currentUserProvider.currentUser.blockingGet())

        fun isLastCheckTooOld(lastCheckDate: Long): Boolean {
            val currentTimeMillis = System.currentTimeMillis()
            val differenceMillis = currentTimeMillis - lastCheckDate
            val checkIntervalInMillies = TimeUnit.HOURS.toMillis(2)
            return differenceMillis > checkIntervalInMillies
        }

        fun checkIfFollowedThreadsExist() {
            val threadsUrl = ApiUtils.getUrlForSubscribedThreads(
                version = 1,
                baseUrl = currentUser.baseUrl
            )

            viewModelScope.launch {
                try {
                    val threads =
                        threadsRepository.getThreads(credentials, threadsUrl, limitForFollowedThreadsExistenceCheck)
                    val followedThreadsExistNew = threads.ocs?.data?.isNotEmpty()
                    _threadsExistState.value = ThreadsExistUiState.Success(followedThreadsExistNew)
                    val followedThreadsExistLastCheckNew = System.currentTimeMillis()
                    arbitraryStorageManager.storeStorageSetting(
                        accountId,
                        FOLLOWED_THREADS_EXIST_LAST_CHECK,
                        followedThreadsExistLastCheckNew.toString(),
                        ""
                    )
                    arbitraryStorageManager.storeStorageSetting(
                        accountId,
                        FOLLOWED_THREADS_EXIST,
                        followedThreadsExistNew.toString(),
                        ""
                    )
                } catch (exception: Exception) {
                    _threadsExistState.value = ThreadsExistUiState.Error(exception)
                }
            }
        }

        if (!hasSpreedFeatureCapability(currentUser.capabilities?.spreedCapability, SpreedFeatures.THREADS)) {
            _threadsExistState.value = ThreadsExistUiState.Success(false)
            return
        }

        val followedThreadsExistOld = arbitraryStorageManager.getStorageSetting(
            accountId,
            FOLLOWED_THREADS_EXIST,
            ""
        ).blockingGet()?.value?.toBoolean() ?: false

        val followedThreadsExistLastCheckOld = arbitraryStorageManager.getStorageSetting(
            accountId,
            FOLLOWED_THREADS_EXIST_LAST_CHECK,
            ""
        ).blockingGet()?.value?.toLong()

        if (followedThreadsExistOld) {
            Log.d(TAG, "followed threads exist for this user. No need to check again.")
            _threadsExistState.value = ThreadsExistUiState.Success(true)
        } else {
            if (followedThreadsExistLastCheckOld == null || isLastCheckTooOld(followedThreadsExistLastCheckOld)) {
                Log.d(TAG, "check if followed threads exist never happened or is too old. Checking now...")
                checkIfFollowedThreadsExist()
            } else {
                _threadsExistState.value = ThreadsExistUiState.Success(false)
                Log.d(TAG, "already checked in the last 2 hours if followed threads exist. Skip check.")
            }
        }
    }

    suspend fun fetchOpenConversations(searchTerm: String) =
        withContext(Dispatchers.IO) {
            _openConversationsState.value = OpenConversationsUiState.None

            if (!hasSpreedFeatureCapability(
                    currentUser.capabilities?.spreedCapability,
                    SpreedFeatures.LISTABLE_ROOMS
                )
            ) {
                return@withContext
            }

            val apiVersion = ApiUtils.getConversationApiVersion(
                currentUser,
                intArrayOf(
                    ApiUtils.API_V4,
                    ApiUtils
                        .API_V3,
                    1
                )
            )
            val url = ApiUtils.getUrlForOpenConversations(apiVersion, currentUser.baseUrl!!)

            openConversationsRepository.fetchConversations(currentUser, url, searchTerm)
                .onSuccess { conversations ->
                    if (conversations.isEmpty()) {
                        _openConversationsState.value = OpenConversationsUiState.None
                    } else {
                        _openConversationsState.value = OpenConversationsUiState.Success(conversations)
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch conversations", exception)
                    _openConversationsState.value = OpenConversationsUiState.Error(exception)
                }
        }

    inner class FederatedInvitationsObserver : Observer<InvitationsModel> {
        override fun onSubscribe(d: Disposable) {
            // unused atm
        }

        override fun onNext(invitationsModel: InvitationsModel) {
            val currentUser = currentUserProvider.currentUser.blockingGet()

            if (invitationsModel.user.userId?.equals(currentUser.userId) == true &&
                invitationsModel.user.baseUrl?.equals(currentUser.baseUrl) == true
            ) {
                if (invitationsModel.invitations.isNotEmpty()) {
                    _getFederationInvitationsViewState.value = GetFederationInvitationsSuccessState(true)
                } else {
                    _getFederationInvitationsViewState.value = GetFederationInvitationsSuccessState(false)
                }
            } else {
                if (invitationsModel.invitations.isNotEmpty()) {
                    _showBadgeViewState.value = ShowBadgeSuccessState(true)
                }
            }
        }

        override fun onError(e: Throwable) {
            _getFederationInvitationsViewState.value = GetFederationInvitationsErrorState
            Log.e(TAG, "Failed to fetch pending invitations", e)
        }

        override fun onComplete() {
            // unused atm
        }
    }

    companion object {
        private val TAG = ConversationsListViewModel::class.simpleName
        const val FOLLOWED_THREADS_EXIST_LAST_CHECK = "FOLLOWED_THREADS_EXIST_LAST_CHECK"
        const val FOLLOWED_THREADS_EXIST = "FOLLOWED_THREADS_EXIST"
    }
}
