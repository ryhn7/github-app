package com.example.githubapp

import android.view.View
import android.widget.TextView

class Utils {

    companion object{
        const val TOKEN = BuildConfig.API_KEY
        fun TextView.setVisibleOrInvisible(text: String?) {
            if (!text.isNullOrBlank()) {
                this.text = text
                this.visibility = View.VISIBLE
            }
        }
    }
}