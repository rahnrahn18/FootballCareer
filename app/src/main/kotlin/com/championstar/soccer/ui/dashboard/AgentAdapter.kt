package com.championstar.soccer.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.Agent
import com.championstar.soccer.databinding.ItemAgentChoiceBinding

class AgentAdapter(private val onAgentSelected: (Agent) -> Unit) :
    ListAdapter<Agent, AgentAdapter.AgentViewHolder>(AgentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentViewHolder {
        val binding = ItemAgentChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AgentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgentViewHolder, position: Int) {
        val agent = getItem(position)
        holder.bind(agent, onAgentSelected)
    }

    class AgentViewHolder(private val binding: ItemAgentChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(agent: Agent, onAgentSelected: (Agent) -> Unit) {
            binding.tvAgentName.text = agent.name
            binding.tvAgentSpecialty.text = "Specialty: ${agent.specialty.name.replace('_', ' ')}"
            binding.tvAgentDescription.text = agent.description
            binding.tvAgentCommission.text = "Commission: ${ (agent.commissionRate * 100).toInt() }%"

            val portraitResId = itemView.context.resources.getIdentifier(
                agent.portraitResourceName, "drawable", itemView.context.packageName
            )
            if (portraitResId != 0) {
                binding.imgAgentPortrait.setImageResource(portraitResId)
            }

            // *** PERBAIKAN: Jadikan seluruh kartu dan tombol dapat diklik ***
            val clickListener = View.OnClickListener { onAgentSelected(agent) }
            itemView.setOnClickListener(clickListener) // itemView adalah root dari layout item (MaterialCardView)
            binding.btnSelectAgent.setOnClickListener(clickListener)
        }
    }
}

class AgentDiffCallback : DiffUtil.ItemCallback<Agent>() {
    override fun areItemsTheSame(oldItem: Agent, newItem: Agent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Agent, newItem: Agent): Boolean {
        return oldItem == newItem
    }
}