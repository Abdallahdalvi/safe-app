/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Julius Linus <juliuslinus1@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.dalvi.safe.dagger.modules

import android.content.Context
import com.dalvi.safe.chat.data.io.AudioFocusRequestManager
import com.dalvi.safe.chat.data.io.AudioRecorderManager
import com.dalvi.safe.chat.data.io.MediaPlayerManager
import com.dalvi.safe.chat.data.io.MediaRecorderManager
import com.dalvi.safe.utils.preferences.AppPreferences
import dagger.Module
import dagger.Provides

@Module
class ManagerModule {

    @Provides
    fun provideMediaRecorderManager(): MediaRecorderManager = MediaRecorderManager()

    @Provides
    fun provideAudioRecorderManager(): AudioRecorderManager = AudioRecorderManager()

    @Provides
    fun provideMediaPlayerManager(preferences: AppPreferences): MediaPlayerManager =
        MediaPlayerManager().apply {
            appPreferences = preferences
        }

    @Provides
    fun provideAudioFocusManager(context: Context): AudioFocusRequestManager = AudioFocusRequestManager(context)
}
