package com.github.devjn.githubsearch.views

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.utils.PinnedRepo
import com.github.devjn.githubsearch.utils.AndroidUtils
import com.github.devjn.githubsearch.utils.setLangImage


/**
 * Created by @author Jahongir on 01-May-17
 * devjn@jn-arts.com
 * PinnedCell
 */
class PinnedCell : FrameLayout {

    val nameTextView: TextView
    val descriptionTextView: TextView
    val langTextView: TextView

    constructor(context: Context) : super(context) {
        val pad = AndroidUtils.dp(8f)
        setPadding(pad, pad, pad, pad)
        setBackgroundResource(R.drawable.border)

        nameTextView = TextView(context)
        nameTextView.setLines(1)
        nameTextView.setMaxLines(1)
        nameTextView.setSingleLine(true)
        nameTextView.setEllipsize(TextUtils.TruncateAt.END)
        addView(nameTextView)
        var layoutParams: FrameLayout.LayoutParams = nameTextView.layoutParams as LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        nameTextView.layoutParams = layoutParams

        descriptionTextView = TextView(context)
        descriptionTextView.maxLines = 2
        addView(descriptionTextView)
        layoutParams = descriptionTextView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.topMargin = AndroidUtils.dp(24f)
        descriptionTextView.layoutParams = layoutParams

        langTextView = TextView(context)
        langTextView.setLines(1)
        langTextView.setMaxLines(1)
        langTextView.setSingleLine(true)
        addView(langTextView)
        layoutParams = langTextView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.BOTTOM
        langTextView.layoutParams = layoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtils.dp(120f), View.MeasureSpec.EXACTLY))
    }

    fun setData(repo: PinnedRepo) {
        nameTextView.setText(repo.repo)
        descriptionTextView.setText(repo.description)
        langTextView.setText(repo.language)
        setLangImage(langTextView, repo.language)
    }
}