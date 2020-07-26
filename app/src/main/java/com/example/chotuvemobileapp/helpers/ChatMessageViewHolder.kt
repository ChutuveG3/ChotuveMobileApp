package com.example.chotuvemobileapp.helpers

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R

class ChatMessageViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
    val message: TextView = itemView.findViewById(R.id.MessageText)
    var layout: ConstraintLayout = itemView.findViewById(R.id.MessageLayout)
}
