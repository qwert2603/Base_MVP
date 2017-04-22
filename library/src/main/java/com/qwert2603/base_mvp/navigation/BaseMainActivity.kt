package com.qwert2603.base_mvp.navigation

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.qwert2603.base_mvp.BaseApplication
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.base.recyclerview.ClickListener
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationAdapter
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationItem
import com.qwert2603.base_mvp.util.LogUtils
import com.qwert2603.base_mvp.util.inflate
import com.qwert2603.base_mvp.util.runOnLollipopOrHigher
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_navigation.view.*
import kotlinx.android.synthetic.main.toolbar_default.*
import javax.inject.Inject

abstract class BaseMainActivity : AppCompatActivity(), Navigation {

    companion object {
        private const val BACK_STACK_KEY = "com.atconsulting.at_timing.navigation.BACK_STACK_KEY"
    }

    protected abstract fun createDefaultBackStack(): List<BackStackItem>

    protected abstract fun createBackStackForNavigationItem(navigationItemId: Int): List<BackStackItem>

    protected abstract val navigationItems: List<NavigationItem>

    private lateinit var backStack: List<BackStackItem>

    @Inject lateinit var navigationAdapter: NavigationAdapter

    lateinit private var headerNavigation: View

    private var isInTransaction = false
        set(value) {
            field = value
            LogUtils.d("isInTransaction == $field")
        }

    private var stackChanges = mutableListOf<BackStackChange>()

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApplication.baseDiManager.navigationComponent().inject(this@BaseMainActivity)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        headerNavigation = navigation_view.inflate(R.layout.header_navigation)
        navigation_view.addHeaderView(headerNavigation)

        navigationAdapter.clickListener = object : ClickListener {
            override fun onItemClicked(itemId: Long) {
                blockUI(70, {
                    closeDrawer()
                    modifyBackStack(createBackStackForNavigationItem(itemId.toInt()))
                })
            }
        }
        with(headerNavigation) {
            navigation_recyclerView.layoutManager = LinearLayoutManager(this@BaseMainActivity)
            navigation_recyclerView.adapter = navigationAdapter
        }
        navigationAdapter.modelList = navigationItems

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    hideKeyboard()
                }
            }
        })

        @Suppress("UNCHECKED_CAST")
        backStack = savedInstanceState?.getSerializable(BACK_STACK_KEY) as? List<BackStackItem> ?: createDefaultBackStack()

        modifyBackStack(backStack)
    }

    override fun onDestroy() {
        with(headerNavigation) {
            navigation_recyclerView.adapter = null
        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BACK_STACK_KEY, ArrayList(backStack))
    }

    override fun modifyBackStack(newBackStack: List<BackStackItem>) {
        // Using View::post to avoid exceptions because this method can be called while FragmentManager is executing transaction.
        fullscreen_FrameLayout.post {
            val oldBackStack = backStack
            backStack = newBackStack

            val backStackChange = BackStackChange(oldBackStack, newBackStack)

            if (isInTransaction) {
                LogUtils.d("modifyBackStack tackChanges.add ${backStackChange.from.map { it.tag }} ${backStackChange.to.map { it.tag }}")
                stackChanges.add(backStackChange)
                return@post
            }

            changeBackStack(backStackChange)

            while (stackChanges.isNotEmpty()) {
                val changes = ArrayList(stackChanges)
                stackChanges.clear()
                changes.forEach { changeBackStack(it) }
            }
        }
    }

    private fun changeBackStack(backStackChange: BackStackChange) {
        isInTransaction = true

        if (backStackChange.from.last() != backStackChange.to.last()) {
            hideKeyboard()
        }

        LogUtils.d("changeBackStack ${backStackChange.from.map { it.tag }} ${backStackChange.to.map { it.tag }}")

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.disallowAddToBackStack()

        val fragmentsToAppear = mutableListOf<Fragment>()
        val fragmentsToDisappear = mutableListOf<Fragment>()

        backStackChange.from.forEach { backStackItem ->
            val fragment: Fragment? = supportFragmentManager.findFragmentByTag(backStackItem.tag)
            if (fragment != null) {
                if (!backStackChange.to.map { it.tag }.contains(backStackItem.tag)) {
                    fragmentTransaction.remove(fragment)
                    fragmentsToDisappear.add(fragment)
                } else if (backStackItem.tag != backStackChange.to.last().tag && !fragment.isDetached) {
                    fragmentTransaction.detach(fragment)
                    fragmentsToDisappear.add(fragment)
                }
            }
        }

        backStackChange.to.forEachIndexed { i, backStackItem ->
            var fragment: Fragment? = supportFragmentManager.findFragmentByTag(backStackItem.tag)
            if (i == backStackChange.to.size - 1) {
                if (fragment != null) {
                    if (fragment.isDetached) {
                        fragmentTransaction.attach(fragment)
                        fragmentsToAppear.add(fragment)
                    }
                } else {
                    fragment = backStackItem.createFragment()
                    if (!backStackItem.fullscreen) {
                        fragment.allowEnterTransitionOverlap = true
                        fragment.allowReturnTransitionOverlap = true
                    }
                    fragmentTransaction.add(if (backStackItem.fullscreen) R.id.fullscreen_fragment_container else R.id.fragment_container, fragment, backStackItem.tag)
                    fragmentsToAppear.add(fragment)
                }
            } else {
                if (fragment != null && !fragment.isDetached) {
                    fragmentTransaction.detach(fragment)
                    fragmentsToDisappear.add(fragment)
                }
            }
        }

        runOnLollipopOrHigher {
            if (backStackChange.from.last().fullscreen || backStackChange.to.last().fullscreen) {
                (fragmentsToAppear union fragmentsToDisappear)
                        .forEach {
                            it.enterTransition = null
                            it.exitTransition = null
                        }
            } else {
                (fragmentsToAppear - fragmentsToDisappear)
                        .forEach {
                            it as BackStackFragment<*, *>
                            val moveStart = it.getBackStackItem().tag in backStackChange.from.map { it.tag }
                                    || backStackChange.from.first().tag != backStackChange.to.first().tag
                                    || it.getBackStackItem().asNested && backStackChange.to.filter { !it.asNested }.size == 1
                            @SuppressLint("NewApi")
                            it.enterTransition = Slide(if (moveStart) Gravity.LEFT else Gravity.RIGHT)
                        }

                (fragmentsToDisappear - fragmentsToAppear)
                        .forEach {
                            it as BackStackFragment<*, *>
                            val moveStart = it.getBackStackItem().tag in backStackChange.to.map { it.tag } || backStackChange.from.first().tag != backStackChange.to.first().tag
                            @SuppressLint("NewApi")
                            it.exitTransition = Slide(if (moveStart) Gravity.LEFT else Gravity.RIGHT)
                        }
            }
        }

        // todo: стоит использовать fragmentTransaction.commitNow(), но с таким методом происходит ошибка когда
        // новая транзакция запускается из fragment::onResume
        // (кидается исключение, что supportFragmentManager уже выполняет транзакцию, хотя isInTransaction == false).
        // Чтобы исключения не было я использую fragmentTransaction.commitNowAllowingStateLoss() тут и
        // fullscreen_FrameLayout.post {...} в override fun modifyBackStack(newBackStack: List<BackStackItem>).
        fragmentTransaction.commitNowAllowingStateLoss()

        isInTransaction = false
    }

    override fun navigateTo(backStackItem: BackStackItem, delay: Boolean) {
        LogUtils.d("navigateTo $backStackItem")
        val action = { modifyBackStack(backStack + backStackItem) }
        if (delay) {
            blockUI(150, action)
        } else {
            action()
        }
    }

    override fun removeBackStackItem(backStackItem: BackStackItem) {
        LogUtils.d("removeBackStackItem $backStackItem")
        modifyBackStack(backStack.filter { it.tag != backStackItem.tag })
    }

    override fun onBackPressed() {
        if (closeDrawer()) {
            return
        }
        if (backStack.dropLastWhile { it.asNested }.size > 1) {
            goBack()
        } else {
            backStack = createDefaultBackStack()
            finish()
        }
    }

    private fun goBack() {
        closeDrawer()
        modifyBackStack(backStack.dropLastWhile { it.asNested }.dropLast(1))
    }

    override fun onFragmentResumed(fragment: BackStackFragment<*, *>) {
        if (fragment.getBackStackItem().tag == backStack.last().tag) {
            navigationAdapter.selectedItemId = navigationItems.find { it.fragmentClass == fragment.javaClass }?.id ?: 0

            fragment.toolbar?.apply {
                setSupportActionBar(this)
                navigationIcon = ContextCompat.getDrawable(this@BaseMainActivity,
                        if (fragment.closable) R.drawable.ic_close_white_24dp else
                            if (backStack.filter { !it.asNested }.size == 1) R.drawable.ic_menu_white_24dp else R.drawable.ic_arrow_back_white_24dp)
                this@BaseMainActivity.title = (supportFragmentManager.findFragmentByTag(backStack.last { !it.asNested }.tag) as BackStackFragment<*, *>).title()
                setNavigationOnClickListener {
                    if (backStack.dropLastWhile { it.asNested }.size == 1) {
                        hideKeyboard()
                        drawer_layout.openDrawer(GravityCompat.START)
                    } else {
                        blockUI(150, { goBack() })
                    }
                }
            }
        }
    }

    override fun onFragmentPaused(fragment: BackStackFragment<*, *>) {
        fragment.toolbar?.setNavigationOnClickListener(null)
    }

    private fun closeDrawer(): Boolean {
        val opened = drawer_layout.isDrawerOpen(GravityCompat.START)
        if (opened) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        return opened
    }

    override fun hideKeyboard(removeFocus: Boolean) {
        LogUtils.d("hideKeyboard $removeFocus")

        if (removeFocus) {
            activity_root_FrameLayout.requestFocus()
        }
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }

    override fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        (getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, 0)
    }

    override fun blockUI(millis: Long, actionOnEnd: (() -> Unit)?) {
        LogUtils.d("blockUI & fullscreen_FrameLayout.visibility == ${fullscreen_FrameLayout.visibility}")
        if (fullscreen_FrameLayout.visibility == View.VISIBLE) return
        fullscreen_FrameLayout.visibility = View.VISIBLE
        fullscreen_FrameLayout.postDelayed({
            fullscreen_FrameLayout.visibility = View.GONE
            actionOnEnd?.invoke()
        }, millis)
    }
}