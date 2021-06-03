package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vino.R
import com.example.vino.network.Vineyard
import org.w3c.dom.Text

class VineyardGridAdapter(private val vineyards: List<Vineyard>) : RecyclerView.Adapter<VineyardGridAdapter.VineyardCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VineyardCardViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.vineyard_recycler_view_item, parent, false)
        return VineyardCardViewHolder(layout)
    }

    override fun onBindViewHolder(holder: VineyardCardViewHolder, position: Int) {
        holder.vineyardName.text = vineyards[position].name
        val imgUri = vineyards[position].imageUrl.toUri().buildUpon().scheme("https").build()
        holder.vineyardImage.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_baseline_broken_image_24)
        }
    }

    override fun getItemCount(): Int {
        return vineyards.size
    }

    class VineyardCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vineyardName: TextView = itemView.findViewById(R.id.name)
        val vineyardImage: ImageView = itemView.findViewById(R.id.vineyardImage)
        val temperature: TextView = itemView.findViewById(R.id.temp)
        val humidity: TextView = itemView.findViewById(R.id.humidity)
    }

}