package com.flxrs.dankchat.utils.extensions

import android.graphics.drawable.LayerDrawable
import android.text.SpannableString
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.text.getSpans
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.request.ImageRequest
import com.flxrs.dankchat.R
import com.google.android.material.snackbar.Snackbar

fun View.showShortSnackbar(text: String, block: Snackbar.() -> Unit = {}) = Snackbar.make(this, text, Snackbar.LENGTH_SHORT)
    .apply(block)
    .show()

fun View.showLongSnackbar(text: String, block: Snackbar.() -> Unit = {}) = Snackbar.make(this, text, Snackbar.LENGTH_LONG)
    .apply(block)
    .show()

inline fun ImageView.loadImage(
    url: String,
    @DrawableRes placeholder: Int? = R.drawable.ic_missing_emote,
    noinline afterLoad: (() -> Unit)? = null,
    block: ImageRequest.Builder.() -> Unit = {}
) {
    load(url) {
        error(R.drawable.ic_missing_emote)
        placeholder?.let { placeholder(it) }
        afterLoad?.let {
            listener(
                onCancel = { it() },
                onSuccess = { _, _ -> it() },
                onError = { _, _ -> it() }
            )
        }
        block()
    }
}

inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachViewHolder(itemCount: Int, action: (T) -> Unit) {
    for (i in 0 until itemCount) {
        val holder = findViewHolderForAdapterPosition(i)
        if (holder is T) {
            action(holder)
        }
    }
}

inline fun <reified T : Any> TextView.forEachSpan(action: (T) -> Unit) {
    (text as? SpannableString)
        ?.getSpans<T>()
        .orEmpty()
        .forEach(action)
}

inline fun <reified T : Any> LayerDrawable.forEachLayer(action: (T) -> Unit) {
    for (i in 0 until numberOfLayers) {
        val drawable = getDrawable(i)
        if (drawable is T) {
            action(drawable)
        }
    }
}

fun ViewPager2.reduceDragSensitivity() {
    val viewPager = this
    val recyclerView = ViewPager2::class.java.getDeclaredField("mRecyclerView").run {
        isAccessible = true
        get(viewPager) as RecyclerView
    }

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop").apply { isAccessible = true }
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop * 2)
}