/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2024 Daniel Calviño Sánchez <danxuliu@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.call;

import com.dalvi.safe.models.json.signaling.DataChannelMessage;
import com.dalvi.safe.signaling.SignalingMessageSender;
import com.dalvi.safe.webrtc.PeerConnectionWrapper;

import java.util.List;
import java.util.Set;

/**
 * Helper class to send messages to participants in a call when an MCU is not used.
 */
public class MessageSenderNoMcu extends MessageSender {

    public MessageSenderNoMcu(SignalingMessageSender signalingMessageSender,
                              Set<String> callParticipantSessionIds,
                              List<PeerConnectionWrapper> peerConnectionWrappers) {
        super(signalingMessageSender, callParticipantSessionIds, peerConnectionWrappers);
    }

    /**
     * Sends the given data channel message to the given signaling session ID.
     *
     * @param dataChannelMessage the message to send
     * @param sessionId the signaling session ID of the participant to send the message to
     */
    public void send(DataChannelMessage dataChannelMessage, String sessionId) {
        PeerConnectionWrapper peerConnectionWrapper = getPeerConnectionWrapper(sessionId);
        if (peerConnectionWrapper != null) {
            peerConnectionWrapper.send(dataChannelMessage);
        }
    }

    public void sendToAll(DataChannelMessage dataChannelMessage) {
        for (PeerConnectionWrapper peerConnectionWrapper: peerConnectionWrappers) {
            if ("video".equals(peerConnectionWrapper.getVideoStreamType())){
                peerConnectionWrapper.send(dataChannelMessage);
            }
        }
    }
}
