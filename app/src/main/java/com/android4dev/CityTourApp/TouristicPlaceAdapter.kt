package com.android4dev.CityTourApp

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android4dev.CityTourApp.models.TouristicPlace
import kotlinx.android.synthetic.main.touristic_place_item.view.*


class TouristicPlaceAdapter(val items : ArrayList<TouristicPlace>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.touristic_place_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val touristicPlaceItem = items.get(position)
        holder.bind(touristicPlaceItem, context)
    }

    // Gets the number of turistic places in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tpTitle = view.textView_tpTitle
    val tpSubtitle = view.textView_tpSubtitle
    val tpImage = view.imageView_tpImage
    val tpDistance = view.textView_tpDistance

    fun bind(touristicPlace:TouristicPlace, context: Context){
        tpTitle.text = touristicPlace.title
        tpSubtitle.text = touristicPlace.subtitle
        tpDistance.text = "${"%.1f".format(touristicPlace.distance!!.div(1000))} km"
        tpImage.setImageResource(context.resources.getIdentifier(touristicPlace.imageFileName, "drawable", context.packageName))

        itemView.setOnClickListener {
            val intent = Intent(context,TouristicPlaceDetail::class.java)
            intent.putExtra("tpItem",touristicPlace)
            context.startActivity(intent)
        }
    }

}

