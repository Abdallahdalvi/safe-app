/*
 * Safe App - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Marcel Hibbe <dev@mhibbe.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.dalvi.safe.invitation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import autodagger.AutoInjector
import com.dalvi.safe.R
import com.dalvi.safe.application.SafeAppApplication
import com.dalvi.safe.data.user.model.User
import com.dalvi.safe.databinding.RvItemInvitationBinding
import com.dalvi.safe.invitation.InvitationsActivity
import com.dalvi.safe.invitation.data.Invitation
import com.dalvi.safe.ui.theme.ViewThemeUtils
import javax.inject.Inject

@AutoInjector(SafeAppApplication::class)
class InvitationsAdapter(
    val user: User,
    private val handleInvitation: (Invitation, InvitationsActivity.InvitationAction) -> Unit
) : ListAdapter<Invitation, InvitationsAdapter.InvitationsViewHolder>(InvitationsCallback) {

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    inner class InvitationsViewHolder(private val itemBinding: RvItemInvitationBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentInvitation: Invitation? = null

        fun bindItem(invitation: Invitation) {
            currentInvitation = invitation

            itemBinding.title.text = invitation.roomName
            itemBinding.subject.text = String.format(
                itemBinding.root.context.resources.getString(R.string.nc_federation_invited_to_room),
                invitation.inviterDisplayName,
                invitation.remoteServerUrl
            )

            itemBinding.acceptInvitation.setOnClickListener {
                currentInvitation?.let {
                    handleInvitation(it, InvitationsActivity.InvitationAction.ACCEPT)
                }
            }

            itemBinding.rejectInvitation.setOnClickListener {
                currentInvitation?.let {
                    handleInvitation(it, InvitationsActivity.InvitationAction.REJECT)
                }
            }

            viewThemeUtils.material.colorMaterialButtonPrimaryBorderless(itemBinding.rejectInvitation)
            viewThemeUtils.material.colorMaterialButtonPrimaryTonal(itemBinding.acceptInvitation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationsViewHolder {
        SafeAppApplication.sharedApplication!!.componentApplication.inject(this)
        return InvitationsViewHolder(
            RvItemInvitationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InvitationsViewHolder, position: Int) {
        val invitation = getItem(position)
        holder.bindItem(invitation)
    }
}

object InvitationsCallback : DiffUtil.ItemCallback<Invitation>() {
    override fun areItemsTheSame(oldItem: Invitation, newItem: Invitation): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Invitation, newItem: Invitation): Boolean = oldItem.id == newItem.id
}
