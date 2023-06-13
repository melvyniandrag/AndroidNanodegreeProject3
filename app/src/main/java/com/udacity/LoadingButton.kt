package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt
import kotlin.properties.Delegates

private const val LOADING_ANIMATION_DURATION = 10000L
private const val LOADING_ARC_RADIUS = 50.0f
private const val LOADING_ARC_TRANSLATION_RIGHT = 80.0f

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // Question! - Why do we save these? Why can't we just use width and height?
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var animationProgressPercent = 0.0f

    private val loadingPaint = Paint().apply {
        color = resources.getColor(R.color.colorPrimaryDark, null)
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL // default: FILL
    }

    private val loadingTextPaint = Paint().apply {
        color = resources.getColor(R.color.white, null)
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL_AND_STROKE // default: FILL
        textSize = 90f
        textAlign = Paint.Align.CENTER

    }

    private val loadingCirclePaint = Paint().apply {
        color = resources.getColor(R.color.colorAccent, null)
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.FILL_AND_STROKE // default: FILL
    }

    private var loadingRect : Rect = Rect(0, 0, (widthSize * animationProgressPercent).roundToInt(), heightSize )
    private val textWidth = loadingTextPaint.measureText(resources.getString(R.string.button_loading))
    private var loadingArcRect = RectF(
        widthSize / 2.0f - LOADING_ARC_RADIUS + textWidth/2.0f  + LOADING_ARC_TRANSLATION_RIGHT,
        heightSize/2.0f - LOADING_ARC_RADIUS,
        widthSize / 2.0f + LOADING_ARC_RADIUS + textWidth/2.0f + LOADING_ARC_TRANSLATION_RIGHT,
        heightSize/2.0f + LOADING_ARC_RADIUS
    )

    init {
        isClickable = true // wow, I lost a half hour trying to figure out why I couldnt click my button!!

        valueAnimator.duration = LOADING_ANIMATION_DURATION

        valueAnimator.addUpdateListener { animation ->
            animationProgressPercent = (animation.animatedValue as Float)
            loadingRect = Rect(0, 0, (widthSize * animationProgressPercent).roundToInt(), heightSize )
            invalidate()
        }

    }

    override fun performClick(): Boolean {
        super.performClick()
        loadingRect = Rect(0, 0, (widthSize * animationProgressPercent).roundToInt(), heightSize )
        loadingArcRect = RectF(
            widthSize / 2.0f - LOADING_ARC_RADIUS + textWidth/2.0f  + LOADING_ARC_TRANSLATION_RIGHT,
            heightSize/2.0f - LOADING_ARC_RADIUS,
            widthSize / 2.0f + LOADING_ARC_RADIUS + textWidth/2.0f + LOADING_ARC_TRANSLATION_RIGHT,
            heightSize/2.0f + LOADING_ARC_RADIUS
        )
        valueAnimator.start()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(resources.getColor(R.color.colorPrimary, null))
        canvas?.drawRect(loadingRect, loadingPaint)
        canvas?.drawText(resources.getString(R.string.button_loading), width/2.0f, height * 0.65f, loadingTextPaint)
        canvas?.drawArc(loadingArcRect, 0.0f, (animationProgressPercent * 360.0f), true, loadingCirclePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

    }

}