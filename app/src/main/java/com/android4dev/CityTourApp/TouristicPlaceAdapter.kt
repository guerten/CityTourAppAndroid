package com.android4dev.CityTourApp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    val tpDescription = view.textView_tpDescription
    val tpImage = view.imageView_tpImage

    fun bind(touristicPlace:TouristicPlace, context: Context){
        tpTitle.text = touristicPlace.title
        tpDescription.text = touristicPlace.description

        tpImage.setImageResource(context.resources.getIdentifier(touristicPlace.imageFileName, "drawable", context.packageName))

        itemView.setOnClickListener { Toast.makeText(context, touristicPlace.title, Toast.LENGTH_SHORT).show() }
    }

}

