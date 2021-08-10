package com.example.vino.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.model.Vineyard

class DashboardSprayAdapter(private val context: Context, private val sprayedVineyards: List<Vineyard>) : RecyclerView.Adapter<DashboardSprayAdapter.SprayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SprayViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.spray_dashboard_item, parent, false)
        return SprayViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SprayViewHolder, position: Int) {
        val vineyard = sprayedVineyards[position]
        holder.name.text = vineyard.name
        holder.sprayTypeAndMaterial.text = "${vineyard.type} - ${vineyard.material}"
        val rei = if (vineyard.rei == 0) {
            "None"
        } else {
            "${vineyard.rei.toString()} hrs"
        }
        holder.reiText.text = "REI: $rei"
        holder.viewOrderButton.setOnClickListener {
            val pdfIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(Uri.parse(vineyard.sprayOrderUrl), "application/pdf")
            }

            val chooser: Intent = Intent.createChooser(pdfIntent, null)
            context.startActivity(chooser)
        }
    }

    override fun getItemCount(): Int {
        return sprayedVineyards.size
    }

    class SprayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.sprayInfoText)
        val sprayTypeAndMaterial: TextView = itemView.findViewById(R.id.sprayText)
        val reiText: TextView = itemView.findViewById(R.id.reiText)
        val viewOrderButton: Button = itemView.findViewById(R.id.sprayOrderButton)
    }
}