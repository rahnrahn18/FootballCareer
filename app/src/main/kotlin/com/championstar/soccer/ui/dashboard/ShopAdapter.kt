package com.championstar.soccer.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.Player
import com.championstar.soccer.data.model.ShopItem
import com.championstar.soccer.databinding.ItemShopBinding

// Adapter ini menerima lambda untuk menangani klik tombol
class ShopAdapter(private val onActionClick: (ShopItem, String) -> Unit) :
    ListAdapter<ShopItem, ShopAdapter.ShopItemViewHolder>(ShopItemDiffCallback()) {

    private var player: Player? = null

    // Fungsi untuk mengupdate data pemain (uang, inventaris)
    fun updatePlayerData(newPlayer: Player) {
        player = newPlayer
        notifyDataSetChanged() // Memaksa RecyclerView untuk menggambar ulang semua item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopItemViewHolder(binding, onActionClick)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = getItem(position)
        player?.let { holder.bind(item, it) }
    }

    class ShopItemViewHolder(
        private val binding: ItemShopBinding,
        private val onActionClick: (ShopItem, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShopItem, player: Player) {
            binding.tvItemName.text = item.name
            binding.tvItemDescription.text = item.description
            binding.tvItemBuff.text = "+${item.buffValue} ${item.buffType.name}"

            // Logika untuk menentukan status tombol
            when {
                player.equippedBootId == item.id -> {
                    binding.btnItemAction.text = "Equipped"
                    binding.btnItemAction.isEnabled = false
                }
                player.ownedBootIds.contains(item.id) -> {
                    binding.btnItemAction.text = "Equip"
                    binding.btnItemAction.isEnabled = true
                    binding.btnItemAction.setOnClickListener { onActionClick(item, "EQUIP") }
                }
                else -> {
                    binding.btnItemAction.text = "Buy ($${item.price.toInt()})"
                    binding.btnItemAction.isEnabled = player.cash >= item.price
                    binding.btnItemAction.setOnClickListener { onActionClick(item, "BUY") }
                }
            }
        }
    }
}

class ShopItemDiffCallback : DiffUtil.ItemCallback<ShopItem>() {
    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean = oldItem == newItem
}