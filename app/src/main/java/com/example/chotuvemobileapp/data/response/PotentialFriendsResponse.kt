package com.example.chotuvemobileapp.data.response

import com.google.gson.annotations.SerializedName

class PotentialFriendsResponse (
    @SerializedName("potential_friends")
    val potentialFriends: ArrayList<String>
)