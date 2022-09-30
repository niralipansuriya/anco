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

import android.annotation.TargetApi
import android.os.Build
import android.view.MotionEvent

/**
 * Component that detects and tracks multiple pointers based on touch events.
 *
 *
 * Each time a pointer gets pressed or released, the current gesture (if any) will end, and a new
 * one will be started (if there are still pressed pointers left). It is guaranteed that the number
 * of pointers within the single gesture will remain the same during the whole gesture.
 */
class MultiPointerGestureDetector {
    /** The listener for receiving notifications when gestures occur.  */
    interface Listener {
        /** Responds to the beginning of a gesture.  */
        fun onGestureBegin(detector: MultiPointerGestureDetector?)

        /** Responds to the update of a gesture in progress.  */
        fun onGestureUpdate(detector: MultiPointerGestureDetector?)

        /** Responds to the end of a gesture.  */
        fun onGestureEnd(detector: MultiPointerGestureDetector?)
    }

    /** Gets whether gesture is in progress or not  */
    var isGestureInProgress = false
        private set
    /** Gets the number of pointers in the current gesture  */
    var count = 0
        private set
    private val mId =
        IntArray(MAX_POINTERS)
    /**
     * Gets the start X coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    val startX =
        FloatArray(MAX_POINTERS)
    /**
     * Gets the start Y coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    val startY =
        FloatArray(MAX_POINTERS)
    /**
     * Gets the current X coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    val currentX =
        FloatArray(MAX_POINTERS)
    /**
     * Gets the current Y coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    val currentY =
        FloatArray(MAX_POINTERS)
    private var mListener: Listener? =
        null

    /**
     * Sets the listener.
     * @param listener listener to set
     */
    fun setListener(listener: Listener?) {
        mListener = listener
    }

    /**
     * Resets the component to the initial state.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun reset() {
        isGestureInProgress = false
        count = 0
        for (i in 0 until MAX_POINTERS) {
            mId[i] =
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) -1 else MotionEvent.INVALID_POINTER_ID
        }
    }

    /**
     * This method can be overridden in order to perform threshold check or something similar.
     * @return whether or not to start a new gesture
     */
    protected fun shouldStartGesture(): Boolean {
        return true
    }

    private fun startGesture() {
        if (!isGestureInProgress) {
            isGestureInProgress = true
            if (mListener != null) {
                mListener!!.onGestureBegin(this)
            }
        }
    }

    private fun stopGesture() {
        if (isGestureInProgress) {
            isGestureInProgress = false
            if (mListener != null) {
                mListener!!.onGestureEnd(this)
            }
        }
    }

    /**
     * Gets the index of the i-th pressed pointer.
     * Normally, the index will be equal to i, except in the case when the pointer is released.
     * @return index of the specified pointer or -1 if not found (i.e. not enough pointers are down)
     */
    private fun getPressedPointerIndex(event: MotionEvent, i: Int): Int {
        var i = i
        val count = event.pointerCount
        val action = event.actionMasked
        val index = event.actionIndex
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_POINTER_UP
        ) {
            if (i >= index) {
                i++
            }
        }
        return if (i < count) i else -1
    }

    /**
     * Handles the given motion event.
     * @param event event to handle
     * @return whether or not the event was handled
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                // update pointers
                var i = 0
                while (i < MAX_POINTERS) {
                    val index = event.findPointerIndex(mId[i])
                    if (index != -1) {
                        currentX[i] = event.getX(index)
                        currentY[i] = event.getY(index)
                    }
                    i++
                }
                // start a new gesture if not already started
                if (!isGestureInProgress && shouldStartGesture()) {
                    startGesture()
                }
                // notify listener
                if (isGestureInProgress && mListener != null) {
                    mListener!!.onGestureUpdate(this)
                }
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                // we'll restart the current gesture (if any) whenever the number of pointers changes
// NOTE: we only restart existing gestures here, new gestures are started in ACTION_MOVE
                val wasGestureInProgress = isGestureInProgress
                stopGesture()
                reset()
                // update pointers
                var i = 0
                while (i < MAX_POINTERS) {
                    val index = getPressedPointerIndex(event, i)
                    if (index == -1) {
                        break
                    }
                    mId[i] = event.getPointerId(index)
                    startX[i] = event.getX(index)
                    currentX[i] = startX[i]
                    startY[i] = event.getY(index)
                    currentY[i] = startY[i]
                    count++
                    i++
                }
                // restart the gesture (if any) if there are still pointers left
                if (wasGestureInProgress && count > 0) {
                    startGesture()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                stopGesture()
                reset()
            }
        }
        return true
    }

    /** Restarts the current gesture  */
    fun restartGesture() {
        if (!isGestureInProgress) {
            return
        }
        stopGesture()
        for (i in 0 until MAX_POINTERS) {
            startX[i] = currentX[i]
            startY[i] = currentY[i]
        }
        startGesture()
    }

    companion object {
        private const val MAX_POINTERS = 2
        /** Factory method that creates a new instance of MultiPointerGestureDetector  */
        @JvmStatic
        fun newInstance(): MultiPointerGestureDetector {
            return MultiPointerGestureDetector()
        }
    }

    init {
        reset()
    }
}