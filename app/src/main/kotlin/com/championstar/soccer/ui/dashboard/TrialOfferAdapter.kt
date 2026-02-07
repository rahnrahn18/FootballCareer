package com.championstar.soccer.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.Club
import com.championstar.soccer.databinding.ItemTrialOfferBinding

class TrialOfferAdapter(private val onTrialAccepted: (Club) -> Unit) :
    ListAdapter<Club, TrialOfferAdapter.TrialOfferViewHolder>(ClubDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrialOfferViewHolder {
        val binding = ItemTrialOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrialOfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrialOfferViewHolder, position: Int) {
        val club = getItem(position)
        holder.bind(club, onTrialAccepted)
    }

    class TrialOfferViewHolder(private val binding: ItemTrialOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(club: Club, onTrialAccepted: (Club) -> Unit) {
            binding.tvClubName.text = club.name
            // SEKARANG BERFUNGSI KEMBALI
            binding.tvClubTier.text = club.tier.name.replace('_', ' ')
            binding.btnAcceptTrial.setOnClickListener { onTrialAccepted(club) }
        }
    }
}

class ClubDiffCallback : DiffUtil.ItemCallback<Club>() {
    override fun areItemsTheSame(oldItem: Club, newItem: Club): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Club, newItem: Club): Boolean = oldItem == newItem
}