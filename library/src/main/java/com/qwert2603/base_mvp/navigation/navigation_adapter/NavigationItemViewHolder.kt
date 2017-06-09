package com.qwert2603.base_mvp.navigation.navigation_adapter

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.view.View
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import kotlinx.android.synthetic.main.item_navigation_menu.view.*

class NavigationItemViewHolder(itemView: View) : BaseRecyclerViewHolder<NavigationItem>(itemView) {

    override fun bind(m: NavigationItem) = with(itemView) {
        icon_ImageView.setImageResource(m.iconRes)
        title_TextView.setText(m.titleRes)
        val selected = itemId == (adapter as? NavigationAdapter)?.selectedItemId
        itemView.isSelected = selected
        //todo: check if selection is correct
        with(itemView) {
            val tintColor = if (selected) R.color.navigation_menu_item_tint_selected else android.R.color.black
            title_TextView.setTextColor(ContextCompat.getColor(context, tintColor))
            icon_ImageView.setColorFilter(ContextCompat.getColor(context, tintColor), PorterDuff.Mode.SRC_ATOP)
        }
    }

}