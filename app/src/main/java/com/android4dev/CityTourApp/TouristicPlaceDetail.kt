package com.android4dev.CityTourApp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.android4dev.CityTourApp.models.TouristicPlace
import android.view.View
import kotlinx.android.synthetic.main.activity_touristic_place_detail.*


class TouristicPlaceDetail : AppCompatActivity() {

    lateinit var touristicPlace: TouristicPlace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touristic_place_detail)

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)


        touristicPlace = intent.extras.get("tpItem") as TouristicPlace

        tpdTitle.text = touristicPlace.title

        tpdScore.text = touristicPlace.score.toString()
        tpdDescription.text = touristicPlace.description



        /*   tpSubtitle.text = touristicPlace.subtitle
   */

        tpdImage.setImageResource(resources.getIdentifier(touristicPlace.imageFileName, "drawable", packageName))

        goBackButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })

        tpdFullLayout.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                Log.d("ViewSwipe", "Left")
            }

            override fun onSwipeRight() {
                finish()
                Log.d("ViewSwipe", "Right")
            }
        })
    }
}

open class OnSwipeTouchListener (context: Context) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(context, GestureListener())

    fun onTouch(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onTouch(e)
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    // onTouch(e);
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}
}