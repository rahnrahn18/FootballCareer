package com.championstar.soccer.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.data.model.MatchCommentary
import com.championstar.soccer.databinding.ItemMatchCommentaryBinding

class MatchCommentaryAdapter : ListAdapter<MatchCommentary, MatchCommentaryAdapter.CommentaryViewHolder>(CommentaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentaryViewHolder {
        val binding = ItemMatchCommentaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentaryViewHolder(private val binding: ItemMatchCommentaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(commentary: MatchCommentary) {
            binding.tvCommentaryLine.text = "${commentary.minute}' - ${commentary.text}"
        }
    }
}

class CommentaryDiffCallback : DiffUtil.ItemCallback<MatchCommentary>() {
    override fun areItemsTheSame(oldItem: MatchCommentary, newItem: MatchCommentary): Boolean = oldItem === newItem
    override fun areContentsTheSame(oldItem: MatchCommentary, newItem: MatchCommentary): Boolean = oldItem == newItem
}