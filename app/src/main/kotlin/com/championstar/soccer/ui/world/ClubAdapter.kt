// app/src/main/kotlin/com/championstar/soccer/ui/world/ClubAdapter.kt
package com.championstar.soccer.ui.world

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.Club // <-- IMPORT DIPERBARUI DARI ClubData MENJADI Club
import com.championstar.soccer.databinding.ItemClubBinding

class ClubAdapter(
    private val clubs: List<Club> // <-- TIPE DATA DIPERBARUI
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    inner class ClubViewHolder(private val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(club: Club) { // <-- TIPE PARAMETER DIPERBARUI
            binding.tvClubName.text = club.name
            // Di masa depan, kita bisa tambahkan click listener di sini
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        holder.bind(clubs[position])
    }

    override fun getItemCount(): Int = clubs.size
}