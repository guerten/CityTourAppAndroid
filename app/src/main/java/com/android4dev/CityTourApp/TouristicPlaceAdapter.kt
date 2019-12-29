package com.android4dev.CityTourApp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
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
    val tpSubtitle = view.textView_tpSubtitle
    val tpImage = view.imageView_tpImage

    fun bind(touristicPlace:TouristicPlace, context: Context){
        tpTitle.text = touristicPlace.title
        tpSubtitle.text = touristicPlace.subtitle

        tpImage.setImageResource(context.resources.getIdentifier(touristicPlace.imageFileName, "drawable", context.packageName))

        itemView.setOnClickListener {
            val intent = Intent(context,TouristicPlaceDetail::class.java)
            intent.putExtra("tpItem",touristicPlace)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Apply activity transition
                context.startActivity(intent)



            } else {
                context.startActivity(intent)
                // Swap without transition
            }
            Toast.makeText(context, touristicPlace.title, Toast.LENGTH_SHORT).show()
        }
    }

}

