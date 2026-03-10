package com.scrollstop.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.scrollstop.app.R
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.util.Constants

class OverlayManager(private val service: AccessibilityService) {

    private var overlayView: View? = null
    private val windowManager: WindowManager =
        service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val handler = Handler(Looper.getMainLooper())

    var onAllowTemporary: (() -> Unit)? = null

    private val overlayParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        format = PixelFormat.TRANSLUCENT
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        gravity = Gravity.CENTER
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    }

    fun showBlockedOverlay(result: DetectionResult) {
        if (overlayView != null) return

        handler.post {
            try {
                val inflater = LayoutInflater.from(service)
                overlayView = inflater.inflate(R.layout.overlay_blocked, null).apply {
                    findViewById<TextView>(R.id.blocked_app_name).text =
                        service.getString(R.string.blocked_message, result.appTarget.displayName)

                    findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
                        hideIfShowing()
                    }

                    findViewById<Button>(R.id.btn_allow_temporary).setOnClickListener {
                        onAllowTemporary?.invoke()
                        hideIfShowing()
                    }
                }

                windowManager.addView(overlayView, overlayParams)

                // Auto-dismiss after configured duration
                handler.postDelayed(
                    { hideIfShowing() },
                    Constants.OVERLAY_AUTO_DISMISS_MS
                )
            } catch (e: Exception) {
                overlayView = null
            }
        }
    }

    fun hideIfShowing() {
        handler.post {
            overlayView?.let {
                try {
                    windowManager.removeView(it)
                } catch (_: Exception) { }
                overlayView = null
            }
            handler.removeCallbacksAndMessages(null)
        }
    }

    fun isShowing(): Boolean = overlayView != null

    fun cleanup() {
        hideIfShowing()
    }
}
