package com.qwert2603.base_mvp.navigation.navigation_adapter

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.view.View
import com.qwert2603.base_mvp.BaseApplication
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.base.recyclerview.BaseRecyclerViewHolder
import kotlinx.android.synthetic.main.item_navigation_menu.view.*
import javax.inject.Inject

class NavigationItemViewHolder(itemView: View)
    : BaseRecyclerViewHolder<NavigationItem, NavigationItemView, NavigationItemPresenter>(itemView), NavigationItemView {

    @Inject lateinit override var presenter: NavigationItemPresenter

    init {
        BaseApplication.baseDiManager.navigationComponent().inject(this@NavigationItemViewHolder)
    }

    override fun bindPresenter() {
        super.bindPresenter()
        val selected = itemId == (adapter as? NavigationAdapter)?.selectedItemId
        itemView.isSelected = selected
        with(itemView) {
            val tintColor = if (selected) R.color.navigation_menu_item_tint_selected else android.R.color.black
            title_TextView.setTextColor(ContextCompat.getColor(context, tintColor))
            icon_ImageView.setColorFilter(ContextCompat.getColor(context, tintColor), PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun setIconRes(iconRes: Int) = with(itemView) {
        icon_ImageView.setImageResource(iconRes)
    }

    override fun setTitleRes(titleRes: Int) = with(itemView) {
        title_TextView.setText(titleRes)
    }
}