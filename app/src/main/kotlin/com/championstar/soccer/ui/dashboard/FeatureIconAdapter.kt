package com.championstar.soccer.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.FeatureIcon
import com.championstar.soccer.databinding.ItemFeatureIconBinding

class FeatureIconAdapter(
    private val icons: List<FeatureIcon>,
    private val onClick: (FeatureIcon) -> Unit
) : RecyclerView.Adapter<FeatureIconAdapter.VH>() {

    inner class VH(val b: ItemFeatureIconBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemFeatureIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val icon = icons[position]
        with(holder.b) {
            imgFeatureIcon.setImageResource(icon.iconResId)
            tvFeatureLabel.text = icon.label
            root.setOnClickListener { onClick(icon) }
        }
    }

    override fun getItemCount(): Int = icons.size
}