package com.qwert2603.base_mvp.search

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.PopupWindow
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.util.runOnLollipopOrHigher
import com.qwert2603.base_mvp.widgets.ShowAllButtonWithLoading
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.popup_autocomplete_message.view.*

class AutocompleteUI(
        val autoCompleteTextView: AutoCompleteTextView,
        val showAllButtonWithLoading: ShowAllButtonWithLoading
) {
    var popup: PopupWindow? = null

    @SuppressLint("InflateParams")
    fun showPopup(@StringRes stringId: Int) {
        PopupWindow(
                LayoutInflater.from(autoCompleteTextView.context).inflate(R.layout.popup_autocomplete_message, null)
                        .also {
                            it.message_TextView.setText(stringId)
                            runOnLollipopOrHigher {
                                @SuppressLint("NewApi")
                                it.message_CardView.elevation = 8.0f
                            }
                        },
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.isOutsideTouchable = true
            it.setOnDismissListener { popup = null }
            popup = it
        }.showAsDropDown(autoCompleteTextView)
    }
}

fun AutocompleteUI.suggestionsConsumer() = Consumer<AutocompleteHelper.State> {
    when (it) {
        AutocompleteHelper.State.Cancel -> {
            autoCompleteTextView.dismissDropDown()
            showAllButtonWithLoading.setState(ShowAllButtonWithLoading.State.SHOW_ALL)
        }
        AutocompleteHelper.State.Loading -> {
            autoCompleteTextView.dismissDropDown()
            showAllButtonWithLoading.setState(ShowAllButtonWithLoading.State.LOADING)
        }
        AutocompleteHelper.State.NothingFound -> {
            autoCompleteTextView.dismissDropDown()
            showAllButtonWithLoading.setState(ShowAllButtonWithLoading.State.SHOW_ALL)
            showPopup(R.string.nothing_found_text)
        }
        AutocompleteHelper.State.Error -> {
            autoCompleteTextView.dismissDropDown()
            showAllButtonWithLoading.setState(ShowAllButtonWithLoading.State.SHOW_ALL)
            showPopup(R.string.suggestions_loading_error_text)
        }
        is AutocompleteHelper.State.Suggestions -> {
            autoCompleteTextView.setAdapter(SuggestionAdapter(autoCompleteTextView.context, autoCompleteTextView.text.toString(), it.suggestions))
            autoCompleteTextView.showDropDown()
            showAllButtonWithLoading.setState(ShowAllButtonWithLoading.State.SHOW_ALL)
        }
    }
}