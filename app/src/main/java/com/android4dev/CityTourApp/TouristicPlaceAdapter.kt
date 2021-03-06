package com.android4dev.CityTourApp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android4dev.CityTourApp.models.TouristicPlace
import kotlinx.android.synthetic.main.touristic_place_item.view.*
import kotlin.properties.Delegates


class TouristicPlaceAdapter(val context: Context) : RecyclerView.Adapter<TouristicPlaceAdapter.ViewHolder>() {

    var items: List<TouristicPlace> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.distance == n.distance }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.touristic_place_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val touristicPlaceItem = items[position]
        holder.bind(touristicPlaceItem, context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val tpTitle = view.textView_tpTitle
        private val tpSubtitle = view.textView_tpSubtitle
        private val tpImage = view.imageView_tpImage
        private val tpDistance = view.textView_tpDistance

        fun bind(touristicPlace:TouristicPlace, context: Context){
            tpTitle.text = touristicPlace.title
            tpSubtitle.text = touristicPlace.subtitle
            var meters = touristicPlace.distance!!
            tpDistance.text = "${"%.1f".format(touristicPlace.distance!!.div(1000))} km"

            var imageId = context.resources.getIdentifier(touristicPlace.imageFileName+"_rounded", "drawable", context.packageName)
            tpImage.setImageResource(imageId)

            itemView.setOnClickListener {
                val intent = Intent(context,TouristicPlaceDetail::class.java)
                intent.putExtra("tpItem",touristicPlace)
                context.startActivity(intent)
            }
        }

    }

}


