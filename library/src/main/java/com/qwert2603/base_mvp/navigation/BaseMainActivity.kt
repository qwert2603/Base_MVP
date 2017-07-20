package com.qwert2603.base_mvp.navigation

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.transition.*
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.qwert2603.base_mvp.BaseApplication
import com.qwert2603.base_mvp.BuildConfig
import com.qwert2603.base_mvp.R
import com.qwert2603.base_mvp.base.recyclerview.ClickListener
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationAdapter
import com.qwert2603.base_mvp.navigation.navigation_adapter.NavigationItem
import com.qwert2603.base_mvp.util.*
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_navigation.view.*
import kotlinx.android.synthetic.main.toolbar_default.*
import javax.inject.Inject

abstract class BaseMainActivity : AppCompatActivity(), Navigation {

    companion object {
        private const val BACK_STACK_KEY = BuildConfig.APPLICATION_ID + "BACK_STACK_KEY"
    }

    protected abstract fun createDefaultBackStack(): List<BackStackItem>

    protected abstract fun createBackStackForNavigationItem(navigationItemId: Int): List<BackStackItem>

    protected abstract val navigationItems: List<NavigationItem>

    open protected fun translateFragmentOnDrawerSlide() = true
    open protected fun translateFragmentOnDrawerSlideFraction() = 0.23f
    private var slideOffset = 0f

    private lateinit var backStack: List<BackStackItem>

    @Inject lateinit var navigationAdapter: NavigationAdapter

    lateinit private var headerNavigation: View

    private val backStackPublishSubject = PublishSubject.create<BackStackChange>()
    private lateinit var backStackDisposable: Disposable

    private lateinit var drawerListener: DrawerLayout.SimpleDrawerListener

    private var resumedFragment: BackStackFragment<*, *>? = null

    @SuppressLint("NewApi")
    protected open fun createSharedElementTransition(): Transition = TransitionSet()
            .addTransition(ChangeImageTransform())
            .addTransition(ChangeBounds())
            .addTransition(ChangeClipBounds())
            .addTransition(ChangeTransform())

    open protected val allowTransitionOverlap = true

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApplication.baseDiManager.navigationComponent.inject(this@BaseMainActivity)
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
        if (navigationAdapter.modelList.isEmpty()) {
            navigationAdapter.modelList = navigationItems
        }

        // fixme: memory leak here (может, это из-за фрагментов во viewPager).
        drawerListener = object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    hideKeyboard()
                }
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                translateFragment(slideOffset)
                this@BaseMainActivity.slideOffset = slideOffset
            }
        }
        drawer_layout.addDrawerListener(drawerListener)

        // fixme: memory leak here
        backStackDisposable = backStackPublishSubject.subscribe { changeBackStack(it) }

        @Suppress("UNCHECKED_CAST")
        backStack = savedInstanceState?.getSerializable(BACK_STACK_KEY) as? List<BackStackItem> ?: createDefaultBackStack()

        modifyBackStack(backStack)
    }

    override fun onDestroy() {
        with(headerNavigation) {
            navigation_recyclerView.adapter = null
        }
        drawer_layout.removeDrawerListener(drawerListener)
        navigationAdapter.clickListener = null
        backStackDisposable.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        drawer_layout.setOnPreDrawAction {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                translateFragment(1f)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BACK_STACK_KEY, ArrayList(backStack))
    }

    override fun modifyBackStack(newBackStack: List<BackStackItem>, sharedElements: List<View>) {
        val oldBackStack = backStack
        backStack = newBackStack
        backStackPublishSubject.onNext(BackStackChange(oldBackStack, newBackStack, sharedElements))
    }

    private fun changeBackStack(backStackChange: BackStackChange) {
        LogUtils.d("changeBackStack ${backStackChange.from.map { it.tag }} ${backStackChange.to.map { it.tag }}")

        if (isDestroyed) {
            LogUtils.d("changeBackStack isDestroyed return")
            return
        }

        if (backStackChange.to.isEmpty()) {
            finish()
            return
        }

        if (backStackChange.from.last() != backStackChange.to.last()) {
            hideKeyboard()
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.disallowAddToBackStack()

        val fragmentsToAppear = mutableListOf<Fragment>()
        val fragmentsToDisappear = mutableListOf<Fragment>()

        backStackChange.from.forEach { backStackItem ->
            val fragment: Fragment? = supportFragmentManager.findFragmentByTag(backStackItem.tag)
            if (fragment != null) {
                if (backStackItem.tag !in backStackChange.to.map { it.tag }) {
                    fragmentTransaction.remove(fragment)
                    fragmentsToDisappear.add(fragment)
                } else if (backStackItem.tag != backStackChange.to.last().tag && !fragment.isDetached) {
                    fragmentTransaction.detach(fragment)
                    fragmentsToDisappear.add(fragment)
                }
            }
        }

        backStackChange.to.forEach { backStackItem ->
            var fragment: Fragment? = supportFragmentManager.findFragmentByTag(backStackItem.tag)
            if (backStackItem.tag == backStackChange.to.last().tag) {
                if (fragment != null) {
                    if (fragment.isDetached) {
                        fragmentTransaction.attach(fragment)
                        fragmentsToAppear.add(fragment)
                    }
                } else {
                    fragment = backStackItem.createFragment()

                    fragment.allowEnterTransitionOverlap = allowTransitionOverlap
                    fragment.allowReturnTransitionOverlap = allowTransitionOverlap

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val transition = createSharedElementTransition()
                        fragment.sharedElementEnterTransition = transition
                        fragment.sharedElementReturnTransition = transition
                    }

                    fragmentTransaction.add(R.id.fragment_container, fragment, backStackItem.tag)
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
            backStackChange.sharedElements.forEach { fragmentTransaction.addSharedElement(it, ViewCompat.getTransitionName(it)) }

            if (backStackChange.from.last().fullscreen || backStackChange.to.last().fullscreen) {
                (fragmentsToAppear union fragmentsToDisappear)
                        .forEach {
                            @SuppressLint("NewApi")
                            it.enterTransition = Fade()
                            @SuppressLint("NewApi")
                            it.exitTransition = Fade()
                        }
            } else {
                (fragmentsToAppear - fragmentsToDisappear)
                        .forEach {
                            it as BackStackFragment<*, *>
                            val moveStart = it.getBackStackItem().tag in backStackChange.from.map { it.tag }
                                    || backStackChange.from.first().tag != backStackChange.to.first().tag
                                    || it.getBackStackItem().asNested && backStackChange.to.filter { !it.asNested }.size == 1
                            @SuppressLint("NewApi")
                            it.enterTransition = createFragmentTransition(moveStart)
                        }

                (fragmentsToDisappear - fragmentsToAppear)
                        .forEach {
                            it as BackStackFragment<*, *>
                            val moveStart = it.getBackStackItem().tag in backStackChange.to.map { it.tag } || backStackChange.from.first().tag != backStackChange.to.first().tag
                            @SuppressLint("NewApi")
                            it.exitTransition = createFragmentTransition(moveStart)
                        }
            }
        }

        translateFragment(0f) // we need it because otherwise fragment.enterTransition finishes in wrong position on screen.
        fragmentTransaction.commitAllowingStateLoss()
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open protected fun createFragmentTransition(moveStart: Boolean): Transition? = Slide(if (moveStart) Gravity.LEFT else Gravity.RIGHT)

    override fun navigateTo(backStackItem: BackStackItem, delay: Boolean, sharedElements: List<View>) {
        LogUtils.d("navigateTo $backStackItem")
        val action = { modifyBackStack(backStack + backStackItem, sharedElements) }
        if (delay) {
            blockUI(80, action)
        } else {
            action()
        }
    }

    override fun removeBackStackItem(backStackItem: BackStackItem, sharedElements: List<View>) {
        LogUtils.d("removeBackStackItem $backStackItem")
        modifyBackStack(backStack.filter { it.tag != backStackItem.tag }, sharedElements)
    }

    override fun isInBackStack(tagFilter: (String) -> Boolean) = backStack.map { it.tag }.any(tagFilter)

    override fun showDialog(dialog: DialogFragment, tag: String, startX: Int?, startY: Int?) {
        blockUI(0, {
            blockUI(1000)
            val args: Bundle = dialog.arguments ?: Bundle()
            startX?.let { args.putInt(CircularRevealDialogFragment.START_POSITION_X, it) }
            startY?.let { args.putInt(CircularRevealDialogFragment.START_POSITION_Y, it) }
            dialog.arguments = args
            dialog.show(supportFragmentManager, tag)
        })
    }

    override fun onBackPressed() {
        if (closeDrawer()) {
            return
        }
        if (resumedFragment?.isBackPressConsumed() ?: false) {
            return
        }
        goBack()
    }

    private fun goBack() {
        closeDrawer()
        modifyBackStack(backStack.dropLastWhile { it.asNested }.dropLast(1), resumedFragment?.getSharedElements() ?: emptyList())
    }

    override fun onFragmentResumed(fragment: BackStackFragment<*, *>) {
        val backStackItem = fragment.getBackStackItem()
        if (backStackItem.tag == backStack.last().tag) {
            drawer_layout.setDrawerLockMode(if (backStackItem.fullscreen) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)

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
                        blockUI(80, { goBack() })
                    }
                }
            }

            resumedFragment = fragment
        }
    }

    override fun onFragmentPaused(fragment: BackStackFragment<*, *>) {
        if (resumedFragment === fragment) resumedFragment = null
        fragment.toolbar?.setNavigationOnClickListener(null)
    }

    private fun closeDrawer(): Boolean {
        return drawer_layout.isDrawerOpen(GravityCompat.START)
                .also { if (it) drawer_layout.closeDrawer(GravityCompat.START) }
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

    private fun translateFragment(slideOffset: Float) {
        if (translateFragmentOnDrawerSlide()) {
            fragment_container.translationX = navigation_view.width * slideOffset * translateFragmentOnDrawerSlideFraction()
        }
    }
}
