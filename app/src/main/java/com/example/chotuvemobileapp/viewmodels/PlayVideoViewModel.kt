package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class PlayVideoViewModel : ViewModel() {
    private lateinit var prefs: SharedPreferences
    private val comments by lazy{

    }
    var liked = false
    var disliked = false

    var likes = 32525;
    var dislikes = 2;

    var descriptionExpanded = false

    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }
}