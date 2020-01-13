package com.android4dev.CityTourApp

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.android4dev.CityTourApp.models.TouristicPlace

/* Extension to be able to notify de adapter when we make any change in the touristicPlace items */
fun RecyclerView.Adapter<*>.autoNotify(oldList: List<TouristicPlace>, newList: List<TouristicPlace>, compare: (TouristicPlace, TouristicPlace) -> Boolean) {

    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

        /*
        *  We make sure the items haven't swipe and also that the distance betweeen each touristicPlace is relevant.
        *  If distance is higher than 50, and the title is diferent then we update the location
        */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            var distanceBetweenEachOther = oldList[oldItemPosition].distance!!-newList[newItemPosition].distance!!

            return distanceBetweenEachOther<50 && distanceBetweenEachOther>-50 && oldList[oldItemPosition].title == newList[newItemPosition].title
        }

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
    })

    diff.dispatchUpdatesTo(this)


}