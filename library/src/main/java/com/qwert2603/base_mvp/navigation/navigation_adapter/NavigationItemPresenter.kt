package com.qwert2603.base_mvp.navigation.navigation_adapter

import com.qwert2603.base_mvp.base.BasePresenter

class NavigationItemPresenter : BasePresenter<NavigationItem, NavigationItemView>() {

    override fun onUpdateViewWithModel(view: NavigationItemView, model: NavigationItem) {
        super.onUpdateViewWithModel(view, model)
        view.setIconRes(model.iconRes)
        view.setTitleRes(model.titleRes)
    }
}