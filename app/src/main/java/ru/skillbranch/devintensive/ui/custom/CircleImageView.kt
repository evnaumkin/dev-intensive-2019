package ru.skillbranch.devintensive.ui.custom


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.*
import androidx.core.graphics.drawable.toDrawable
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ImageView(context, attrs, defStyle) {

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val COLORDRAWABLE_DIMENSION = 2

        private val DEFAULT_BORDER_WIDTH = 2f
        private val DEFAULT_BORDER_COLOR = Color.WHITE
        private val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
        private val DEFAULT_BORDER_OVERLAY = false
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPixels(context, DEFAULT_BORDER_WIDTH)
    private var circleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
    private var borderOverlay: Boolean = false

    private val drawableRect = RectF()
    private val borderRect = RectF()

    private val shaderMatrix = Matrix()
    private val paintBitmap = Paint()
    private val paintBorder = Paint()
    private val paintBackground = Paint()

    private var civDrawable: Drawable? = null
    private var bitmap: Bitmap? = null
    private var bitmapShader: BitmapShader? = null
    private var bitmapWidth: Int = 0
    private var bitmapHeight: Int = 0

    private var drawableRadius: Float = 0.toFloat()
    private var borderRadius: Float = 0.toFloat()
    private var colorFilter: ColorFilter? = null

    private var ready: Boolean = false
    private var setupPending: Boolean = false

    var isDisableCircularTransformation: Boolean = false
        set(value) {
            if (isDisableCircularTransformation == value) {
                return
            }
            field = value
            loadBitmap()
        }

    @ColorInt
    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        setBorderColorInt(Color.parseColor(hex))
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        setBorderColorInt(context.resources.getColor(colorId, context.theme))
    }

    fun setBorderColorInt(@ColorInt color: Int) {
        if (color == borderColor) {
            return
        }
        borderColor = color
        paintBorder.color = borderColor
        invalidate()
    }

    fun getBorderWidth(): Int = Utils.convertPixelsToDp(context, borderWidth)

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = Utils.convertDpToPixels(context, dp.toFloat())
        invalidate()
    }


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle,0)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, Utils.convertDpToPixels(context, DEFAULT_BORDER_WIDTH))
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderOverlay = DEFAULT_BORDER_OVERLAY
            circleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
            a.recycle()
        }
        init()
    }


    private fun init() {
        super.setScaleType(SCALE_TYPE)
        ready = true

        outlineProvider = OutlineProvider()

        if (setupPending) {
            setup()
            setupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }

        if (bitmap == null) {
            return
        }

        if (circleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), drawableRadius, paintBackground)
        }
        canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), drawableRadius, paintBitmap)
        if (borderWidth > 0) {
            canvas.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, paintBorder)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        loadBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        loadBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        loadBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        loadBitmap()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === colorFilter) {
            return
        }

        colorFilter = cf
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return colorFilter
    }

    private fun applyColorFilter() {
        paintBitmap.colorFilter = colorFilter
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {

        return when (drawable) {
            null -> null
            is BitmapDrawable -> drawable.bitmap
            else -> try {
                val bitmap: Bitmap
                if (drawable is ColorDrawable) {
                    bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
                } else {
                    bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
                }
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    private fun loadBitmap() {
        if (isDisableCircularTransformation) {
            bitmap = null
        } else {
            bitmap = getBitmapFromDrawable(drawable)
        }
        setup()
    }

    private fun setup() {
        if (!ready) {
            setupPending = true
            return
        }

        if (width == 0 && height == 0) {
            return
        }

        if (bitmap == null) {
            invalidate()
            return
        }

        bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        paintBitmap.isAntiAlias = true
        paintBitmap.shader = bitmapShader

        paintBorder.style = Paint.Style.STROKE
        paintBorder.isAntiAlias = true
        paintBorder.color = borderColor
        paintBorder.strokeWidth = borderWidth.toFloat()

        paintBackground.style = Paint.Style.FILL
        paintBackground.isAntiAlias = true
        paintBackground.color = circleBackgroundColor

        bitmapHeight = bitmap!!.height
        bitmapWidth = bitmap!!.width

        borderRect.set(calculateBounds())
        borderRadius =
            Math.min((borderRect.height() - borderWidth) / 2.0f, (borderRect.width() - borderWidth) / 2.0f)

        drawableRect.set(borderRect)
        if (!borderOverlay && borderWidth > 0) {
            drawableRect.inset(borderWidth - 1.0f, borderWidth - 1.0f)
        }
        drawableRadius = Math.min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)

        applyColorFilter()
        updateshaderMatrixx()
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = Math.min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateshaderMatrixx() {
        val scale: Float
        var dx = 0f
        var dy = 0f

        shaderMatrix.set(null)

        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
            scale = drawableRect.height() / bitmapHeight.toFloat()
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmapWidth.toFloat()
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate((dx + 0.5f).toInt() + drawableRect.left, (dy + 0.5f).toInt() + drawableRect.top)

        bitmapShader!!.setLocalMatrix(shaderMatrix)
    }

    fun createAvatar(str: String, context: Context): Drawable {
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRect(
                0f,
                0f,
                500f,
                500f,
                Paint().apply {
                    color = context.theme.obtainStyledAttributes(listOf(R.attr.colorAccent).toIntArray()).getColor(0, 0)
                })
        canvas.drawText(str, 250f, 320f, Paint().apply {
            color = Color.WHITE
            textSize = 200f
            textAlign = Paint.Align.CENTER
        })
        canvas.save()
        return bitmap.toDrawable(context.resources)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return inTouchableArea(event.x, event.y) && super.onTouchEvent(event)
    }

    private fun inTouchableArea(x: Float, y: Float): Boolean {
        return Math.pow((x - borderRect.centerX()).toDouble(), 2.0) + Math.pow(
            (y - borderRect.centerY()).toDouble(),
            2.0
        ) <= Math.pow(borderRadius.toDouble(), 2.0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private inner class OutlineProvider : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            val bounds = Rect()
            borderRect.roundOut(bounds)
            outline.setRoundRect(bounds, bounds.width() / 2.0f)
        }

    }
}