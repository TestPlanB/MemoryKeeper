package com.example.memorykeepercore.core

import android.app.Activity
import android.app.Application
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.CallSuper
import com.example.memorykeepercore.extend.created
import com.example.memorykeepercore.switch.KeeperConfig

internal object ViewKeeper {
    fun clearMemory(view: View) {
        if (view.context == null) {
            return
        }
        try {
            clearViewMemory(view)
            when (view) {
                is ImageView -> clearImageViewMemory(view)
                is TextView -> clearTextViewMemory(view)
                is FrameLayout -> clearFrameLayout(view)
                is LinearLayout -> clearLinearLayout(view)
                is ProgressBar -> clearProgressBarMemory(view)
                is ListView -> clearListViewMemory(view)
                is ViewGroup -> clearViewGroupMemory(view)
            }
        } catch (e: Exception) {
        }


    }

    @CallSuper
    @Synchronized
    fun clearViewMemory(view: View) {
        with(view) {
            val context = view.context
            onFocusChangeListener = null
            setOnClickListener(null)
            setOnCreateContextMenuListener(null)
            setOnKeyListener(null)
            setOnLongClickListener(null)
            setOnTouchListener(null)
            when (context) {
                is Application -> {
                    clearContext(view)
                }
                is Activity -> {
                    if (context.isFinishing || context.isDestroyed) {
                        clearContext(view)
                    }
                }
                else -> {
                }
            }
            view.background?.let {
                view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View?) {
                        view.created = true
                    }

                    override fun onViewDetachedFromWindow(v: View?) {
                        if (view.created) {
                            it.callback = null
                            view.background = null
                            view.removeOnAttachStateChangeListener(this)
                            view.created = false
                        }
                    }

                })

            }

        }

    }

    @Synchronized
    fun clearImageViewMemory(imageView: ImageView) {
        val drawable = imageView.drawable
        drawable?.let {
            it.callback = null
        }
        imageView.setImageDrawable(null)
        recycleBitmap(drawable)
    }
    @Synchronized
    fun clearTextViewMemory(textView: TextView) {
        val drawables = textView.compoundDrawables
        for (drawable in drawables) {
            drawable.callback = null
            recycleBitmap(drawable)
        }
        with(textView) {
            movementMethod = null
            keyListener = null
            setCompoundDrawables(null, null, null, null)
            setOnEditorActionListener(null)
        }
    }
    @Synchronized
    fun clearProgressBarMemory(progressbar: ProgressBar) {
        val drawable = progressbar.progressDrawable
        drawable?.let {
            it.callback = null
        }
        val indeterminateDrawable = progressbar.indeterminateDrawable
        indeterminateDrawable?.let {
            it.callback = null
        }
        progressbar.progressDrawable = null
        progressbar.indeterminateDrawable = null
        recycleBitmap(drawable)
        recycleBitmap(indeterminateDrawable)
    }

    @Synchronized
    fun clearListViewMemory(listView: ListView) {
        val selector = listView.selector
        selector?.let {
            it.callback = null
        }
        val adapter = listView.adapter
        adapter?.let {
            listView.adapter = null
        }
        with(listView) {
            onItemClickListener = null
            onItemLongClickListener = null
            onItemSelectedListener = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOnScrollChangeListener(null)
            }
        }
    }

    @Synchronized
    fun clearFrameLayout(flameLayout: FrameLayout) {
        val drawable = flameLayout.foreground
        drawable?.let {
            drawable.callback = null
        }
        flameLayout.foreground = null
        recycleBitmap(drawable)
    }
    @Synchronized
    fun clearLinearLayout(linearLayout: LinearLayout) {
        val drawable = linearLayout.dividerDrawable
        drawable?.let {
            drawable.callback = null
        }
        linearLayout.dividerDrawable = null
        recycleBitmap(drawable)
    }

    private fun clearContext(view: View) {
        val viewClass = View::class.java
        val mField = viewClass.getDeclaredField("mContext")
        mField.isAccessible = true
        mField.set(view, null)

    }

    // 删除bitmap会回收native bitmap，有可能会导致异常情况，比如复用bitmap异常，默认不开启
    private fun recycleBitmap(drawable: Drawable) {
        if(!KeeperConfig.openBitmapClear){
            return
        }
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }

    }

    private fun clearViewGroupMemory(viewGroup: ViewGroup){
        val childCount = viewGroup.childCount
        for(i in 0 until childCount){
            clearMemory(viewGroup.getChildAt(i))
        }

    }
}