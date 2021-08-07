package com.example.vino.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import com.example.vino.R
import com.example.vino.model.Todo
import com.example.vino.model.Vineyard
import com.example.vino.ui.ImageShimmer
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.card.MaterialCardView
import java.util.*

class VineyardGridAdapter(private val context: Context, private val onVineyardListener: OnVineyardListener) : ListAdapter<Vineyard, VineyardGridAdapter.VineyardCardViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Vineyard>() {
        override fun areItemsTheSame(oldItem: Vineyard, newItem: Vineyard): Boolean {
            return oldItem.vineyardId == newItem.vineyardId
        }

        override fun areContentsTheSame(oldItem: Vineyard, newItem: Vineyard): Boolean {
            return ((oldItem.name == newItem.name) && (oldItem.job == newItem.job) && (oldItem.sprayed == newItem.sprayed))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VineyardCardViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.vineyard_recycler_view_item, parent, false)
        return VineyardCardViewHolder(layout, onVineyardListener)
    }

    override fun onBindViewHolder(holder: VineyardCardViewHolder, position: Int) {
        val vineyard = getItem(position)
        holder.bind(vineyard, context)
    }

    class VineyardCardViewHolder(itemView: View, private val onVineyardListener: OnVineyardListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val vineyardItemCardView: MaterialCardView = itemView.findViewById(R.id.vineyard_item_card_view)
        private val vineyardLinearLayout: LinearLayout = itemView.findViewById(R.id.vineyard_item_linear_layout)
        private val vineyardName: TextView = itemView.findViewById(R.id.vineyard_name)
        private val vineyardImage: ImageView = itemView.findViewById(R.id.vineyard_image)
        private val vineyardJob: TextView = itemView.findViewById(R.id.current_job)
        private val sprayIcon: ImageView = itemView.findViewById(R.id.spray_icon)
        private val sprayText: TextView = itemView.findViewById(R.id.spray_text)

        private lateinit var vineyardObject: Vineyard
        private var imageCacheKey: MemoryCache.Key? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(vineyard: Vineyard, context: Context) {

            if (vineyard.sprayed) {
                sprayIcon.visibility = View.VISIBLE
                sprayText.apply {
                    text = "${vineyard.type} - ${vineyard.material}"
                    visibility = View.VISIBLE
                }
                setGradient(vineyardLinearLayout, intArrayOf(Color.RED, ContextCompat.getColor(context, R.color.white)))
            } else {
                sprayIcon.visibility = View.GONE
                sprayText.visibility = View.GONE

                setGradient(vineyardLinearLayout, intArrayOf(ContextCompat.getColor(context, R.color.purple_600), ContextCompat.getColor(context, R.color.white)))
            }

            vineyardName.text = vineyard.name
            vineyardJob.text = context.getString(R.string.current_job, vineyard.job)

            // This is the placeholder for the imageView
            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(ImageShimmer().shimmer)
            }

            val imgUri = vineyard.imageUrl.toUri().buildUpon().scheme("https").build()

            // For caching
            // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
            // The singleton ImageLoader can be accessed using an extension function:
            // val imageLoader = context.imageLoader USED IN VINEYARD DETAIL FRAGMENT

            vineyardImage.load(imgUri) {
                allowHardware(false)
                placeholder(shimmerDrawable)
                error(R.drawable.generic_vineyard)
                listener { _, metadata ->
                    imageCacheKey = metadata.memoryCacheKey
                }
            }

            setItemsTransitionName(vineyard.name)

            vineyardObject = vineyard
        }

        private fun setGradient(view: View, colors: IntArray) {
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                colors
            )
            view.background = gradientDrawable
        }

        private fun setItemsTransitionName(name: String) {
            vineyardItemCardView.transitionName = "vineyardCardView$name"
            vineyardLinearLayout.transitionName = "vineyardLinearLayout$name"
            vineyardName.transitionName = "vineyardName$name"
            vineyardImage.transitionName = "vineyardImage$name"
            vineyardJob.transitionName = "vineyardJob$name"
        }

        override fun onClick(v: View?) {
            onVineyardListener.onVineyardClick(vineyardObject.vineyardId, vineyardItemCardView, vineyardLinearLayout, vineyardName, vineyardJob, vineyardImage, imageCacheKey)
        }
    }

    interface OnVineyardListener {
        fun onVineyardClick(
            vineyardId: Int,
            vineyardCardView: MaterialCardView,
            vineyardLinearLayout: LinearLayout,
            vineyardName: TextView,
            vineyardJob: TextView,
            vineyardImage: ImageView,
            imageCacheKey: MemoryCache.Key?
        ) {}
    }
}
