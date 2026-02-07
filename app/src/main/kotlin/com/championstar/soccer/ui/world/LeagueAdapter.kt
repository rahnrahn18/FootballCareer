package com.championstar.soccer.ui.world

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.League
import com.championstar.soccer.databinding.ItemLeagueBinding

class LeagueAdapter(
    private val leagues: List<League>,
    private val onLeagueClicked: (League) -> Unit
) : RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder>() {

    inner class LeagueViewHolder(private val binding: ItemLeagueBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(league: League) {
            binding.tvLeagueName.text = league.name
            binding.tvLeagueCountry.text = league.country
            binding.root.setOnClickListener { onLeagueClicked(league) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val binding = ItemLeagueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeagueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bind(leagues[position])
    }

    override fun getItemCount(): Int = leagues.size
}