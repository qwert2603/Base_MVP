package com.qwert2603.base_mvp.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.inflate
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionAdapter(context: Context, s: String, suggestions: List<String>) : ArrayAdapter<String>(context, 0, suggestions) {

    val search = s.toLowerCase()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: parent.inflate(R.layout.item_suggestion)
        val s = getItem(position)
        val spannableStringBuilder = SpannableStringBuilder(s)
        val indexOf = s.toLowerCase().indexOf(search)
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), indexOf, indexOf + search.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        view.suggestion_TextView.text = spannableStringBuilder
        return view
    }
}