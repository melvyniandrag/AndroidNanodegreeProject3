package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.Math.ceil
import java.lang.Math.round
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    //private val valueAnimator = ValueAnimator()
    private val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var animationProgress = 0.0f


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
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL_AND_STROKE // default: FILL
    }

    private var loadingRectWidth = 0

    init {
        isClickable = true
        valueAnimator.setDuration(2000)

        valueAnimator.addUpdateListener { animation ->
            animationProgress = (animation.animatedValue as Float)
            loadingRectWidth = (width * animationProgress).roundToInt()
            invalidate()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        valueAnimator.start()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(resources.getColor(R.color.colorPrimary, null))
        //Log.e("hello", (width * animationProgress).toString())

        val loadingRect = Rect(0, 0, loadingRectWidth, height )
        canvas?.drawRect(loadingRect, loadingPaint)
        canvas?.drawText("We are loading", width/2.0f, height * 0.65f, loadingTextPaint)
        //canvas?.drawCircle(width * 0.5f + 550.0f, height * 0.5f, 50f, loadingCirclePaint)
        val textWidth = loadingTextPaint.measureText("We are loading")
        val paint = Paint()
        val rect = RectF(width / 2.0f - 50.0f + textWidth/2.0f  + 80.0f, height/2.0f - 50.0f, width / 2.0f + 50.0f + textWidth/2.0f + 80.0f, height/2.0f + 50.0f)

        //Example values
        //Example values

        paint.color = resources.getColor(R.color.colorAccent, null)
        paint.strokeWidth = 0.0f
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas!!.drawArc(rect, 0.0f, (animationProgress * 360.0f), true, paint)

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