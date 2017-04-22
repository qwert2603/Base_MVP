package com.qwert2603.base_mvp.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.qwert2603.base_mvp.R
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionAdapter(context: Context, suggestions: List<String>) : ArrayAdapter<String>(context, 0, suggestions) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: parent.inflate(R.layout.item_suggestion)
        view.suggestion_TextView.text = getItem(position)
        return view
    }
}