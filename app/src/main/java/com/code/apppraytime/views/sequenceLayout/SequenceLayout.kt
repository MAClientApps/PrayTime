package com.code.apppraytime.views.sequenceLayout

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TableLayout
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import com.code.apppraytime.R

public class SequenceLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : FrameLayout(context, attrs, defStyleAttr), SequenceStep.OnStepChangedListener {

    private var noAnim = false
    private var dotsWrapper: FrameLayout
    private var stepsWrapper: TableLayout
    private var progressBarForeground: View
    private var progressBarBackground: View
    private var progressBarWrapper: FrameLayout

    public constructor(context: Context) : this(context, null)
    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        inflate(getContext(), R.layout.sequence_layout, this)
        dotsWrapper = findViewById(R.id.dotsWrapper)
        stepsWrapper = findViewById(R.id.stepsWrapper)
        progressBarWrapper = findViewById(R.id.progressBarWrapper)
        progressBarForeground = findViewById(R.id.progressBarForeground)
        progressBarBackground = findViewById(R.id.progressBarBackground)

        val attributes = getContext().theme.obtainStyledAttributes(
                attrs,
                R.styleable.SequenceLayout,
                0,
                R.style.SequenceLayout)
        applyAttributes(attributes)
        attributes.recycle()

        clipToPadding = false
        clipChildren = false
    }

    @ColorInt
    private var progressBackgroundColor: Int = 0
    @ColorInt
    private var progressForegroundColor: Int = 0

    public fun setStyle(@StyleRes defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(defStyleAttr, R.styleable.SequenceLayout)
        applyAttributes(attributes)
        attributes.recycle()
    }

    private fun setProgressForegroundColor(@ColorInt color: Int) {
        this.progressForegroundColor = color
        progressBarForeground.setBackgroundColor(color)
    }

    private fun setProgressBackgroundColor(@ColorInt progressBackgroundColor: Int) {
        this.progressBackgroundColor = progressBackgroundColor
        progressBarBackground.setBackgroundColor(progressBackgroundColor)
    }


    private fun removeAllSteps() {
        stepsWrapper.removeAllViews()
    }

    fun <T> setAdapter(adapter: SequenceAdapter<T>) where T : Any {
        removeCallbacks(animateToActive)
        removeAllSteps()
        val count = adapter.getCount()
        for (i in 0 until count) {
            val item = adapter.getItem(i)
            val view = SequenceStep(context)
            adapter.bindView(view, item)
            addView(view)
        }
    }

    private fun applyAttributes(attributes: TypedArray) {
        setupProgressForegroundColor(attributes)
        setupProgressBackgroundColor(attributes)
    }

    private fun setupProgressForegroundColor(attributes: TypedArray) {
        setProgressForegroundColor(attributes.getColor(R.styleable.SequenceLayout_progressForegroundColor, 0))
    }

    private fun setupProgressBackgroundColor(attributes: TypedArray) {
        setProgressBackgroundColor(attributes.getColor(R.styleable.SequenceLayout_progressBackgroundColor, 0))
    }

    private fun setProgressBarHorizontalOffset() {
        val firstAnchor: View = stepsWrapper.getChildAt(0).findViewById(R.id.anchor)
        progressBarWrapper.translationX = firstAnchor.measuredWidth + 4.toPx() - (progressBarWrapper.measuredWidth / 2f)
    }

    private fun placeDots() {
        dotsWrapper.removeAllViews()
        var firstOffset = 0
        var lastOffset = 0

        stepsWrapper.children().forEachIndexed { i, view ->
            val sequenceStep = view as SequenceStep
            val sequenceStepDot = SequenceStepDot(context)
            sequenceStepDot.setDotBackground(progressForegroundColor, progressBackgroundColor)
            sequenceStepDot.setPulseColor(progressForegroundColor)
            sequenceStepDot.clipChildren = false
            sequenceStepDot.clipToPadding = false
            val layoutParams = FrameLayout.LayoutParams(8.toPx(), 8.toPx())
            val totalDotOffset = getRelativeTop(sequenceStep, stepsWrapper) + sequenceStep.paddingTop + sequenceStep.getDotOffset() + 2.toPx() //TODO dynamic dot size
            layoutParams.topMargin = totalDotOffset
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            dotsWrapper.addView(sequenceStepDot, layoutParams)
            if (i == 0) {
                firstOffset = totalDotOffset
            }
            lastOffset = totalDotOffset
        }

        val backgroundLayoutParams = progressBarBackground.layoutParams as MarginLayoutParams
        backgroundLayoutParams.topMargin = firstOffset + 4.toPx()
        backgroundLayoutParams.height = lastOffset - firstOffset
        progressBarBackground.requestLayout()

        val foregroundLayoutParams = progressBarForeground.layoutParams as MarginLayoutParams
        foregroundLayoutParams.topMargin = firstOffset + 4.toPx()
        foregroundLayoutParams.height = lastOffset - firstOffset
        progressBarForeground.requestLayout()
    }

    private val animateToActive = {
        progressBarForeground.visibility = VISIBLE
        progressBarForeground.pivotY = 0f
        progressBarForeground.scaleY = 0f

        val activeStepIndex = stepsWrapper.children().indexOfFirst { it is SequenceStep && it.isActive() }

        if (activeStepIndex != -1) {
            val activeDot = dotsWrapper.getChildAt(activeStepIndex)
            val activeDotTopMargin = (activeDot.layoutParams as LayoutParams).topMargin
            val progressBarForegroundTopMargin = (progressBarForeground.layoutParams as LayoutParams).topMargin
            val scaleEnd = (activeDotTopMargin + (activeDot.measuredHeight / 2) - progressBarForegroundTopMargin) /
                    progressBarBackground.measuredHeight.toFloat()

            val duration = if (noAnim) 0L else resources.getInteger(R.integer.sequence_step_duration).toLong()

            ViewCompat.animate(progressBarForeground)
                    .setStartDelay(duration)
                    .scaleY(scaleEnd)
                    .setInterpolator(LinearInterpolator())
                    .setDuration(activeStepIndex * duration)
                    .setUpdateListener {
                        val animatedOffset = progressBarForeground.scaleY * progressBarBackground.measuredHeight
                        dotsWrapper.children()
                            .forEachIndexed { i, view ->
                                if (i > activeStepIndex) {
                                    return@forEachIndexed
                                }
                                val dot = view as SequenceStepDot
                                val dotTopMargin = (dot.layoutParams as LayoutParams).topMargin -
                                        progressBarForegroundTopMargin -
                                        (dot.measuredHeight / 2)
                                if (animatedOffset >= dotTopMargin) {
                                    if (i < activeStepIndex && !dot.isEnabled) {
                                        dot.isEnabled = true
                                    } else if (i == activeStepIndex && !dot.isActivated) {
                                        dot.isActivated = true
                                    }
                                }
                            }
                    }
                    .start()
        }
    }

    private fun getRelativeTop(child: View, parent: ViewGroup): Int {
        val offsetViewBounds = Rect()
        child.getDrawingRect(offsetViewBounds)
        parent.offsetDescendantRectToMyCoords(child, offsetViewBounds)
        return offsetViewBounds.top
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is SequenceStep) {
            if (child.isActive()) {
                child.setPadding(
                        0,
                        if (stepsWrapper.childCount == 0) 0 else resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_top), //no paddingTop if first step is active
                        0,
                        resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_bottom)
                )
            }
            child.onStepChangedListener = this
            stepsWrapper.addView(child, params)
            return
        }
        super.addView(child, index, params)
    }

    override fun onStepChanged(noAnim: Boolean) {
        this.noAnim = noAnim
        setProgressBarHorizontalOffset()
        placeDots()
        removeCallbacks(animateToActive)
        post(animateToActive)
    }
}