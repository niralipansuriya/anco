/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.app.ancoturf.presentation.home.portfolio.utils

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.view.MotionEvent

/**
 * Zoomable controller that calculates transformation based on touch events.
 */
class DefaultZoomableController(gestureDetector: TransformGestureDetector) :
    ZoomableController, TransformGestureDetector.Listener {
    private val mGestureDetector: TransformGestureDetector
    private var mListener: ZoomableController.Listener? = null
    private var mIsEnabled = false
    /** Gets whether the rotation gesture is enabled or not.  */
    /** Sets whether the rotation gesture is enabled or not.  */
    var isRotationEnabled = false
    /** Gets whether the scale gesture is enabled or not.  */
    /** Sets whether the scale gesture is enabled or not.  */
    var isScaleEnabled = true
    /** Gets whether the translations gesture is enabled or not.  */
    /** Sets whether the translation gesture is enabled or not.  */
    var isTranslationEnabled = true
    private val mMinScaleFactor = 1.0f
    private val mViewBounds = RectF()
    private val mImageBounds = RectF()
    private val mTransformedImageBounds = RectF()
    private val mPreviousTransform = Matrix()
    private val mActiveTransform = Matrix()
    private val mActiveTransformInverse = Matrix()
    private val mTempValues = FloatArray(9)
    override fun setListener(listener: ZoomableController.Listener) {
        mListener = listener
    }

    /** Rests the controller.  */
    fun reset() {
        mGestureDetector.reset()
        mPreviousTransform.reset()
        mActiveTransform.reset()
    }

    /** Sets whether the controller is enabled or not.  */
    override fun setEnabled(enabled: Boolean) {
        mIsEnabled = enabled
        if (!enabled) {
            reset()
        }
    }

    /** Returns whether the controller is enabled or not.  */
    override fun isEnabled(): Boolean {
        return mIsEnabled
    }

    /** Sets the image bounds before zoomable transformation is applied.  */
    override fun setImageBounds(imageBounds: RectF) {
        mImageBounds.set(imageBounds)
    }

    /** Sets the view bounds.  */
    override fun setViewBounds(viewBounds: RectF) {
        mViewBounds.set(viewBounds)
    }

    /**
     * Maps point from the view's to the image's relative coordinate system.
     * This takes into account the zoomable transformation.
     */
    fun mapViewToImage(viewPoint: PointF): PointF {
        val points = mTempValues
        points[0] = viewPoint.x
        points[1] = viewPoint.y
        mActiveTransform.invert(mActiveTransformInverse)
        mActiveTransformInverse.mapPoints(points, 0, points, 0, 1)
        mapAbsoluteToRelative(points, points, 1)
        return PointF(points[0], points[1])
    }

    /**
     * Maps point from the image's relative to the view's coordinate system.
     * This takes into account the zoomable transformation.
     */
    fun mapImageToView(imagePoint: PointF): PointF {
        val points = mTempValues
        points[0] = imagePoint.x
        points[1] = imagePoint.y
        mapRelativeToAbsolute(points, points, 1)
        mActiveTransform.mapPoints(points, 0, points, 0, 1)
        return PointF(points[0], points[1])
    }

    /**
     * Maps array of 2D points from absolute to the image's relative coordinate system,
     * and writes the transformed points back into the array.
     * Points are represented by float array of [x0, y0, x1, y1, ...].
     *
     * @param destPoints destination array (may be the same as source array)
     * @param srcPoints source array
     * @param numPoints number of points to map
     */
    private fun mapAbsoluteToRelative(
        destPoints: FloatArray,
        srcPoints: FloatArray,
        numPoints: Int
    ) {
        for (i in 0 until numPoints) {
            destPoints[i * 2 + 0] =
                (srcPoints[i * 2 + 0] - mImageBounds.left) / mImageBounds.width()
            destPoints[i * 2 + 1] =
                (srcPoints[i * 2 + 1] - mImageBounds.top) / mImageBounds.height()
        }
    }

    /**
     * Maps array of 2D points from relative to the image's absolute coordinate system,
     * and writes the transformed points back into the array
     * Points are represented by float array of [x0, y0, x1, y1, ...].
     *
     * @param destPoints destination array (may be the same as source array)
     * @param srcPoints source array
     * @param numPoints number of points to map
     */
    private fun mapRelativeToAbsolute(
        destPoints: FloatArray,
        srcPoints: FloatArray,
        numPoints: Int
    ) {
        for (i in 0 until numPoints) {
            destPoints[i * 2 + 0] =
                srcPoints[i * 2 + 0] * mImageBounds.width() + mImageBounds.left
            destPoints[i * 2 + 1] =
                srcPoints[i * 2 + 1] * mImageBounds.height() + mImageBounds.top
        }
    }

    /**
     * Gets the zoomable transformation
     * Internal matrix is exposed for performance reasons and is not to be modified by the callers.
     */
    override fun getTransform(): Matrix {
        return mActiveTransform
    }

    /** Notifies controller of the received touch event.   */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mIsEnabled) {
            mGestureDetector.onTouchEvent(event)
        } else false
    }

    /* TransformGestureDetector.Listener methods  */
    override fun onGestureBegin(detector: TransformGestureDetector?) {}

    override fun onGestureUpdate(detector: TransformGestureDetector?) {
        mActiveTransform.set(mPreviousTransform)
        if (isRotationEnabled) {
            val angle: Float =
                detector?.rotation!! * (180 / Math.PI).toFloat()
            mActiveTransform.postRotate(angle, detector.pivotX, detector.pivotY)
        }
        if (isScaleEnabled) {
            val scale: Float = detector?.scale!!
            mActiveTransform.postScale(scale, scale, detector.pivotX, detector.pivotY)
        }
        limitScale(detector?.pivotX!!, detector?.pivotY!!)
        if (isTranslationEnabled) {
            mActiveTransform.postTranslate(detector.translationX, detector.translationY)
        }
        limitTranslation()
        if (mListener != null) {
            mListener!!.onTransformChanged(mActiveTransform)
        }
    }


    override fun onGestureEnd(detector: TransformGestureDetector?) {
        mPreviousTransform.set(mActiveTransform)
    }

    /** Gets the current scale factor.  */
    override fun getScaleFactor(): Float {
        mActiveTransform.getValues(mTempValues)
        return mTempValues[Matrix.MSCALE_X]
    }

    private fun limitScale(pivotX: Float, pivotY: Float) {
        val currentScale = scaleFactor
        if (currentScale < mMinScaleFactor) {
            val scale = mMinScaleFactor / currentScale
            mActiveTransform.postScale(scale, scale, pivotX, pivotY)
        }
    }

    private fun limitTranslation() {
        val bounds = mTransformedImageBounds
        bounds.set(mImageBounds)
        mActiveTransform.mapRect(bounds)
        val offsetLeft = getOffset(bounds.left, bounds.width(), mViewBounds.width())
        val offsetTop = getOffset(bounds.top, bounds.height(), mViewBounds.height())
        if (offsetLeft != bounds.left || offsetTop != bounds.top) {
            mActiveTransform.postTranslate(offsetLeft - bounds.left, offsetTop - bounds.top)
            mGestureDetector.restartGesture()
        }
    }

    private fun getOffset(
        offset: Float,
        imageDimension: Float,
        viewDimension: Float
    ): Float {
        val diff = viewDimension - imageDimension
        return if (diff > 0) diff / 2 else limit(offset, diff, 0f)
    }

    private fun limit(value: Float, min: Float, max: Float): Float {
        return Math.min(Math.max(min, value), max)
    }

    companion object {
        fun newInstance(): DefaultZoomableController {
            return DefaultZoomableController(TransformGestureDetector.newInstance())
        }
    }

    init {
        mGestureDetector = gestureDetector
        mGestureDetector.setListener(this)
    }
}