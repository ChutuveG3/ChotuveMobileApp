package com.example.chotuvemobileapp.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chotuvemobileapp.entities.CommentItem
import kotlin.random.Random

class PlayVideoViewModel : ViewModel() {
    private lateinit var prefs: SharedPreferences
    val comments by lazy {
        val liveData = MutableLiveData<ArrayList<CommentItem>>()
        val dummyComments = ArrayList<CommentItem>()
        for (i in 1..5) {
            dummyComments.add(CommentItem("Comentario hardcodeado #$i.\nHardcodear es malo", "User $i", "$i/$i/$i"))
        }
        liveData.value = dummyComments
        return@lazy liveData
    }

    var liked = false
    var disliked = false

    var likes = 32525
    var dislikes = 2

    var descriptionExpanded = false

    fun setPrefs(preferences: SharedPreferences){
        prefs = preferences
    }
}