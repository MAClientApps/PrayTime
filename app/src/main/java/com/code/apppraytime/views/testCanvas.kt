package com.code.apppraytime.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.code.apppraytime.R
import kotlin.jvm.JvmOverloads

class testCanvas : View {

//    private val mPaint = Paint()
//    private var mBitmap: Bitmap? = null
//    private var mCanvas: Canvas? = null
//    private var mPath: Path? = null
//    private var mBitmapPaint: Paint? = null
//    private var mContext: Context? = null
//    private var circlePaint: Paint? = null
//    private var circlePath: Path? = null

    constructor(context: Context?) : super(context) {
//        mContext = context
//
//        mPaint.isAntiAlias = true
//        mPaint.isDither = true
//        mPaint.color = Color.GREEN
//        mPaint.style = Paint.Style.STROKE
//        mPaint.strokeJoin = Paint.Join.ROUND
//        mPaint.strokeCap = Paint.Cap.ROUND
//        mPaint.strokeWidth = 12F
//
//        mPath = Path()
//        mBitmapPaint = Paint(Paint.DITHER_FLAG)
//        circlePaint = Paint()
//        circlePath = Path()
//        circlePaint!!.isAntiAlias = true
//        circlePaint!!.color = Color.BLUE
//        circlePaint!!.style = Paint.Style.STROKE
//        circlePaint!!.strokeJoin = Paint.Join.MITER
//        circlePaint!!.strokeWidth = 4f
    }

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : super (
        context,
        attrs,
        defStyleAttr) {

//        mContext = context
//
//        mPaint.isAntiAlias = true
//        mPaint.isDither = true
//        mPaint.color = Color.GREEN
//        mPaint.style = Paint.Style.STROKE
//        mPaint.strokeJoin = Paint.Join.ROUND
//        mPaint.strokeCap = Paint.Cap.ROUND
//        mPaint.strokeWidth = 12F
//
//        mPath = Path()
//        mBitmapPaint = Paint(Paint.DITHER_FLAG)
//        circlePaint = Paint()
//        circlePath = Path()
//        circlePaint!!.isAntiAlias = true
//        circlePaint!!.color = Color.BLUE
//        circlePaint!!.style = Paint.Style.STROKE
//        circlePaint!!.strokeJoin = Paint.Join.MITER
//        circlePaint!!.strokeWidth = 4f

    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        mCanvas = Canvas(mBitmap!!)
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        canvas!!.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint);
//        canvas.drawPath(mPath!!,  mPaint);
//        canvas.drawPath(circlePath!!,  circlePaint!!);
//    }
//
//    private var mX = 0f
//    private  var mY:kotlin.Float = 0f
//    private val TOUCH_TOLERANCE = 4f
//
//    private fun touchStart(x: Float, y: Float) {
//        mPath!!.reset()
//        mPath!!.moveTo(x, y)
//        mX = x
//        mY = y
//    }
//
//    private fun touchMove(x: Float, y: Float) {
//        val dx = abs(x - mX)
//        val dy = abs(y - mY)
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
//            mX = x
//            mY = y
//            circlePath!!.reset()
//            circlePath!!.addCircle(mX, mY, 30f, Path.Direction.CW)
//        }
//    }
//
//    private fun touchUp() {
//        mPath!!.lineTo(mX, mY)
//        circlePath!!.reset()
//        // commit the path to our offscreen
//        mCanvas!!.drawPath(mPath!!, mPaint)
//        // kill this so we don't double draw
//        mPath!!.reset()
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val x = event!!.x
//        val y = event.y
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                touchStart(x, y)
//                invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                touchMove(x, y)
//                invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                touchUp()
//                invalidate()
//            }
//        }
//        return true
//    }

    private val paint: Paint = Paint()
    private var rectangle: RectF? = null
    private var margin: Float

    init {
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.green)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        margin = 3f // margin should be >= strokeWidth / 2 (otherwise the arc is cut)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (rectangle == null) {
            rectangle = RectF(0f + margin, 0f + margin, width.toFloat() - margin, height.toFloat() - margin)
        }
        canvas?.drawArc(rectangle!!, 180f, 180f, false, paint)
    }
}