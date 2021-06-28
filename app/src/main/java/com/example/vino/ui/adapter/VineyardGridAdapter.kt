package com.example.vino.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import com.example.vino.R
import com.example.vino.model.Vineyard
import com.google.android.material.card.MaterialCardView

class VineyardGridAdapter(private val vineyards: List<Vineyard>, private val context: Context, private val onVineyardListener: OnVineyardListener) : RecyclerView.Adapter<VineyardGridAdapter.VineyardCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VineyardCardViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.vineyard_recycler_view_item, parent, false)
        return VineyardCardViewHolder(layout, onVineyardListener)
    }

    override fun onBindViewHolder(holder: VineyardCardViewHolder, position: Int) {
        val vineyard = vineyards[position]
        holder.bind(vineyard, context)
//        holder.vineyardName.text = vineyards[position].name
//        val imgUri = vineyards[position].imageUrl.toUri().buildUpon().scheme("https").build()
//        holder.vineyardImage.load(imgUri) {
//            placeholder(R.drawable.loading_animation)
//            error(R.drawable.ic_baseline_broken_image_24)
//        }
//        holder.temperature.text = context.getString(R.string.temperature_value, 86)
//        holder.humidity.text = context.getString(R.string.humidity_value, 30)
    }

    override fun getItemCount(): Int {
        return vineyards.size
    }

    class VineyardCardViewHolder(itemView: View, private val onVineyardListener: OnVineyardListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val vineyardItemCardView: MaterialCardView = itemView.findViewById(R.id.vineyard_item_card_view)
        private val vineyardLinearLayout: LinearLayout = itemView.findViewById(R.id.vineyard_item_linear_layout)
        private val vineyardName: TextView = itemView.findViewById(R.id.vineyard_name)
        private val vineyardImage: ImageView = itemView.findViewById(R.id.vineyard_image)
        private val temperature: TextView = itemView.findViewById(R.id.vineyard_temp)
        private val humidity: TextView = itemView.findViewById(R.id.vineyard_humidity)

        private lateinit var vineyardObject: Vineyard
        private var imageCacheKey: MemoryCache.Key? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(vineyard: Vineyard, context: Context) {
            vineyardName.text = vineyard.name
            val imgUri = vineyard.imageUrl.toUri().buildUpon().scheme("https").build()

            // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
            // The singleton ImageLoader can be accessed using an extension function:
            // val imageLoader = context.imageLoader

            vineyardImage.load(imgUri) {
                allowHardware(false)
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_baseline_broken_image_24)
                listener { _, metadata ->
                    imageCacheKey = metadata.memoryCacheKey
                }
            }

            temperature.text = context.getString(R.string.temperature_value, 86)
            humidity.text = context.getString(R.string.humidity_value, 30)

            setItemsTransitionName(vineyard.name)

            vineyardObject = vineyard
        }

        private fun setItemsTransitionName(name: String) {
            vineyardItemCardView.transitionName = "vineyardCardView$name"
            vineyardLinearLayout.transitionName = "vineyardLinearLayout$name"
            vineyardName.transitionName = "vineyardName$name"
            vineyardImage.transitionName = "vineyardImage$name"
            temperature.transitionName = "vineyardTemperature$name"
            humidity.transitionName = "vineyardHumidity$name"
        }

        override fun onClick(v: View?) {
            onVineyardListener.onVineyardClick(adapterPosition, vineyardObject.name, vineyardItemCardView, vineyardLinearLayout, vineyardName, vineyardImage, temperature, humidity, imageCacheKey)
        }
    }

    interface OnVineyardListener {
        fun onVineyardClick(
            position: Int,
            vineyard: String,
            vineyardCardView: MaterialCardView,
            vineyardLinearLayout: LinearLayout,
            vineyardName: TextView,
            vineyardImage: ImageView,
            temperature: TextView,
            humidity: TextView,
            imageCacheKey: MemoryCache.Key?
        ) {
        }
    }
}