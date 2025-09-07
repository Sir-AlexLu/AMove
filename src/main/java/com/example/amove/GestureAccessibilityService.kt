package com.example.amove

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class GestureAccessibilityService : AccessibilityService() {

    companion object {
        var instance: GestureAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AMove", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    private fun performGesture(path: Path) {
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 150))
            .build()
        dispatchGesture(gesture, null, null)
    }

    fun performTap() {
        Log.d("AMove", "Performing Tap")
        val path = Path().apply { moveTo(500f, 1500f) }
        performGesture(path)
    }

    fun performSwipeLeft() {
        Log.d("AMove", "Performing Swipe Left")
        val path = Path().apply {
            moveTo(800f, 1500f)
            lineTo(200f, 1500f)
        }
        performGesture(path)
    }

    fun performSwipeRight() {
        Log.d("AMove", "Performing Swipe Right")
        val path = Path().apply {
            moveTo(200f, 1500f)
            lineTo(800f, 1500f)
        }
        performGesture(path)
    }

    fun performScrollUp() {
        Log.d("AMove", "Performing Scroll Up")
        val path = Path().apply {
            moveTo(500f, 1800f)
            lineTo(500f, 800f)
        }
        performGesture(path)
    }

    fun performScrollDown() {
        Log.d("AMove", "Performing Scroll Down")
        val path = Path().apply {
            moveTo(500f, 800f)
            lineTo(500f, 1800f)
        }
        performGesture(path)
    }
}
