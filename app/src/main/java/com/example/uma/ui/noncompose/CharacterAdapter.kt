package com.example.uma.ui.noncompose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.uma.R
import com.example.uma.data.models.CharacterBasic

class CharacterAdapter : ListAdapter<CharacterBasic, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.character_name)
        private val imageView: ImageView = itemView.findViewById(R.id.character_image)

        fun bind(character: CharacterBasic) {
            nameTextView.text = character.name
            // Using Coil to load the image
            imageView.load(character.image) {
                crossfade(true)
                placeholder(R.drawable.specialweek_icon) // Optional: a placeholder
            }
        }
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<CharacterBasic>() {
    override fun areItemsTheSame(oldItem: CharacterBasic, newItem: CharacterBasic): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CharacterBasic, newItem: CharacterBasic): Boolean {
        return oldItem == newItem
    }
}
