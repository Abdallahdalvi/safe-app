/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.polls.repositories

import com.dalvi.safe.polls.model.Poll
import io.reactivex.Observable

interface PollRepository {

    fun createPoll(
        credentials: String?,
        url: String,
        roomToken: String,
        question: String,
        options: List<String>,
        resultMode: Int,
        maxVotes: Int
    ): Observable<Poll>

    fun getPoll(credentials: String?, url: String, roomToken: String, pollId: String): Observable<Poll>

    fun vote(credentials: String?, url: String, roomToken: String, pollId: String, options: List<Int>): Observable<Poll>

    fun closePoll(credentials: String?, url: String, roomToken: String, pollId: String): Observable<Poll>
}
