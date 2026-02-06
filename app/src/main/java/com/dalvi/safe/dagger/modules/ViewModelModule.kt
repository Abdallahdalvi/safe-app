/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Safe App GmbH
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.dagger.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dalvi.safe.account.viewmodels.BrowserLoginActivityViewModel
import com.dalvi.safe.activities.CallViewModel
import com.dalvi.safe.chat.viewmodels.ChatViewModel
import com.dalvi.safe.chat.viewmodels.ScheduledMessagesViewModel
import com.dalvi.safe.chooseaccount.StatusViewModel
import com.dalvi.safe.contacts.ContactsViewModel
import com.dalvi.safe.contextchat.ContextChatViewModel
import com.dalvi.safe.conversationcreation.ConversationCreationViewModel
import com.dalvi.safe.conversationinfo.viewmodel.ConversationInfoViewModel
import com.dalvi.safe.conversationinfoedit.viewmodel.ConversationInfoEditViewModel
import com.dalvi.safe.conversationlist.viewmodels.ConversationsListViewModel
import com.dalvi.safe.diagnose.DiagnoseViewModel
import com.dalvi.safe.invitation.viewmodels.InvitationsViewModel
import com.dalvi.safe.messagesearch.MessageSearchViewModel
import com.dalvi.safe.openconversations.viewmodels.OpenConversationsViewModel
import com.dalvi.safe.polls.viewmodels.PollCreateViewModel
import com.dalvi.safe.polls.viewmodels.PollMainViewModel
import com.dalvi.safe.polls.viewmodels.PollResultsViewModel
import com.dalvi.safe.polls.viewmodels.PollVoteViewModel
import com.dalvi.safe.raisehand.viewmodel.RaiseHandViewModel
import com.dalvi.safe.remotefilebrowser.viewmodels.RemoteFileBrowserItemsViewModel
import com.dalvi.safe.shareditems.viewmodels.SharedItemsViewModel
import com.dalvi.safe.threadsoverview.viewmodels.ThreadsOverviewViewModel
import com.dalvi.safe.translate.viewmodels.TranslateViewModel
import com.dalvi.safe.viewmodels.CallRecordingViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class ViewModelFactory @Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
@Suppress("TooManyFunctions")
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedItemsViewModel::class)
    abstract fun sharedItemsViewModel(viewModel: SharedItemsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageSearchViewModel::class)
    abstract fun messageSearchViewModel(viewModel: MessageSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PollMainViewModel::class)
    abstract fun pollViewModel(viewModel: PollMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PollVoteViewModel::class)
    abstract fun pollVoteViewModel(viewModel: PollVoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PollResultsViewModel::class)
    abstract fun pollResultsViewModel(viewModel: PollResultsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PollCreateViewModel::class)
    abstract fun pollCreateViewModel(viewModel: PollCreateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RemoteFileBrowserItemsViewModel::class)
    abstract fun remoteFileBrowserItemsViewModel(viewModel: RemoteFileBrowserItemsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CallRecordingViewModel::class)
    abstract fun callRecordingViewModel(viewModel: CallRecordingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RaiseHandViewModel::class)
    abstract fun raiseHandViewModel(viewModel: RaiseHandViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TranslateViewModel::class)
    abstract fun translateViewModel(viewModel: TranslateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OpenConversationsViewModel::class)
    abstract fun openConversationsViewModel(viewModel: OpenConversationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationsListViewModel::class)
    abstract fun conversationsListViewModel(viewModel: ConversationsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun chatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationInfoViewModel::class)
    abstract fun conversationInfoViewModel(viewModel: ConversationInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationInfoEditViewModel::class)
    abstract fun conversationInfoEditViewModel(viewModel: ConversationInfoEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvitationsViewModel::class)
    abstract fun invitationsViewModel(viewModel: InvitationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    abstract fun contactsViewModel(viewModel: ContactsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationCreationViewModel::class)
    abstract fun conversationCreationViewModel(viewModel: ConversationCreationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiagnoseViewModel::class)
    abstract fun diagnoseViewModel(viewModel: DiagnoseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ThreadsOverviewViewModel::class)
    abstract fun threadsOverviewViewModel(viewModel: ThreadsOverviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowserLoginActivityViewModel::class)
    abstract fun browserLoginActivityViewModel(viewModel: BrowserLoginActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContextChatViewModel::class)
    abstract fun contextChatViewModel(viewModel: ContextChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CallViewModel::class)
    abstract fun callViewModel(viewModel: CallViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatusViewModel::class)
    abstract fun statusRepositoryViewModel(viewModel: StatusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScheduledMessagesViewModel::class)
    abstract fun scheduledMessagesViewModel(viewModel: ScheduledMessagesViewModel): ViewModel
}
