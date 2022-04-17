package com.code.apppraytime.views.sequenceLayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.doOnPreDraw
import androidx.core.widget.TextViewCompat
import com.code.apppraytime.R
import kotlin.math.max

public class SequenceStep(context: Context?, attrs: AttributeSet?)
    : TableRow(context, attrs) {

    private var noAnim = false

    public constructor(context: Context) : this(context, null)

    private var title: TextView
    private var anchor: TextView
    private var subtitle: TextView
    private var isActive: Boolean = false
    internal var onStepChangedListener: OnStepChangedListener? = null

    init {
        View.inflate(context, R.layout.sequence_step, this)

        title = findViewById(R.id.title)
        anchor = findViewById(R.id.anchor)
        subtitle = findViewById(R.id.subtitle)

        clipToPadding = false
        clipChildren = false

        val attributes = getContext().theme.obtainStyledAttributes(
                attrs,
                R.styleable.SequenceStep,
                0,
                R.style.SequenceStep)

        setupAnchor(attributes)
        setupAnchorWidth(attributes)
        setupAnchorTextAppearance(attributes)
        setupTitle(attributes)
        setupTitleTextAppearance(attributes)
        setupSubtitle(attributes)
        setupSubtitleTextAppearance(attributes)
        setupActive(attributes)

        attributes.recycle()
    }

    public fun setAnchor(anchor: CharSequence?) {
        this.anchor.text = anchor
        this.anchor.visibility = View.VISIBLE
        this.anchor.minWidth = resources.getDimensionPixelSize(R.dimen.sequence_anchor_min_width)
    }

    public fun setAnchorMaxWidth(@Dimension(unit = Dimension.PX) maxWidth: Int) {
        anchor.maxWidth = maxWidth
    }

    public fun setAnchorMinWidth(@Dimension(unit = Dimension.PX) minWidth: Int) {
        anchor.minWidth = minWidth
    }

    public fun setAnchorTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(anchor, resourceId)
        verticallyCenter(anchor, title)
    }

    public fun setTitle(title: CharSequence?) {
        this.title.text = title
        this.title.visibility = View.VISIBLE
    }

    public fun setTitle(@StringRes resId: Int) {
        setTitle(context.getString(resId))
    }

    public fun setTitleTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(title, resourceId)
        verticallyCenter(anchor, title)
    }

    public fun setSubtitle(subtitle: CharSequence?) {
        this.subtitle.text = subtitle
        this.subtitle.visibility = View.VISIBLE
    }

    public fun setSubtitle(@StringRes resId: Int) {
        setSubtitle(context.getString(resId))
    }

    public fun setSubtitleTextAppearance(@StyleRes resourceId: Int) {
        TextViewCompat.setTextAppearance(subtitle, resourceId)
    }

    public fun isActive(): Boolean {
        return isActive
    }

    public fun setActive(isActive: Boolean, noAnim: Boolean) {
        this.isActive = isActive
        this.noAnim = noAnim
        doOnPreDraw { onStepChangedListener?.onStepChanged(noAnim) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        doOnPreDraw { onStepChangedListener?.onStepChanged(noAnim) }
    }

    fun getDotOffset(): Int =
            (max(getViewHeight(anchor), getViewHeight(title)) - 8.toPx()) / 2

    private fun setupAnchor(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.SequenceStep_anchor)) {
            anchor.visibility = View.INVISIBLE
        } else {
            setAnchor(attributes.getString(R.styleable.SequenceStep_anchor))
        }
    }

    private fun setupAnchorWidth(attributes: TypedArray) {
        setAnchorMinWidth(attributes.getDimensionPixelSize(R.styleable.SequenceStep_anchorMinWidth, 0))
        setAnchorMaxWidth(attributes.getDimensionPixelSize(R.styleable.SequenceStep_anchorMaxWidth, Integer.MAX_VALUE))
    }

    private fun setupSubtitle(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.SequenceStep_subtitle)) {
            subtitle.visibility = View.GONE
        } else {
            setSubtitle(attributes.getString(R.styleable.SequenceStep_subtitle))
        }
    }

    private fun setupTitle(attributes: TypedArray) {
        if (!attributes.hasValue(R.styleable.SequenceStep_title)) {
            title.visibility = View.GONE
        } else {
            setTitle(attributes.getString(R.styleable.SequenceStep_title))
        }
    }

    private fun setupTitleTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.SequenceStep_titleTextAppearance)) {
            setTitleTextAppearance(attributes.getResourceId(R.styleable.SequenceStep_titleTextAppearance, 0))
        }
    }

    private fun setupSubtitleTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.SequenceStep_subtitleTextAppearance)) {
            setSubtitleTextAppearance(attributes.getResourceId(R.styleable.SequenceStep_subtitleTextAppearance, 0))
        }
    }

    private fun setupAnchorTextAppearance(attributes: TypedArray) {
        if (attributes.hasValue(R.styleable.SequenceStep_anchorTextAppearance)) {
            setAnchorTextAppearance(attributes.getResourceId(R.styleable.SequenceStep_anchorTextAppearance, 0))
        }
    }

    private fun setupActive(attributes: TypedArray) {
        setActive(attributes.getBoolean(R.styleable.SequenceStep_active, false), true)
    }

    private fun verticallyCenter(vararg views: View) {
        val maxHeight = views.map(::getViewHeight).maxOrNull() ?: 0

        views.forEach { view ->
            val height = getViewHeight(view)
            (view.layoutParams as MarginLayoutParams).topMargin = (maxHeight - height) / 2
            view.requestLayout()
        }
    }

    private fun getViewHeight(view: View) =
            if (view is TextView) {
                ((view.lineHeight - view.lineSpacingExtra) / view.lineSpacingMultiplier).toInt()
            } else {
                view.measuredHeight
            }

    internal interface OnStepChangedListener {
        fun onStepChanged(noAnim: Boolean)
    }
}