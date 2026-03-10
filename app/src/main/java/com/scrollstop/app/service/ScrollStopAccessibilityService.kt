package com.scrollstop.app.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.scrollstop.app.blocking.BlockingMode
import com.scrollstop.app.blocking.CooldownManager
import com.scrollstop.app.blocking.ScrollSessionTracker
import com.scrollstop.app.blocking.SessionVerdict
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionEngine
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ScrollStopAccessibilityService : AccessibilityService() {

    private lateinit var detectionEngine: DetectionEngine
    private lateinit var overlayManager: OverlayManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var cooldownManager: CooldownManager
    private lateinit var sessionTracker: ScrollSessionTracker
    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Volatile private var masterEnabled = true
    @Volatile private var blockInstagram = true
    @Volatile private var blockYoutube = true
    @Volatile private var blockTiktok = true
    @Volatile private var blockFacebook = true
    @Volatile private var blockSnapchat = true
    @Volatile private var blockReddit = true
    @Volatile private var blockTwitter = true
    @Volatile private var blockLinkedin = true
    @Volatile private var blockingAction = "close_reel"

    private var pendingScanRunnable: Runnable? = null

    override fun onServiceConnected() {
        super.onServiceConnected()

        detectionEngine = DetectionEngine()
        overlayManager = OverlayManager(this)
        preferencesManager = PreferencesManager(applicationContext)
        cooldownManager = CooldownManager()
        sessionTracker = ScrollSessionTracker()

        serviceScope.launch { preferencesManager.masterEnabled.collect { masterEnabled = it } }
        serviceScope.launch { preferencesManager.blockInstagramReels.collect { blockInstagram = it } }
        serviceScope.launch { preferencesManager.blockYoutubeShorts.collect { blockYoutube = it } }
        serviceScope.launch { preferencesManager.blockTiktok.collect { blockTiktok = it } }
        serviceScope.launch { preferencesManager.blockFacebook.collect { blockFacebook = it } }
        serviceScope.launch { preferencesManager.blockSnapchat.collect { blockSnapchat = it } }
        serviceScope.launch { preferencesManager.blockReddit.collect { blockReddit = it } }
        serviceScope.launch { preferencesManager.blockTwitter.collect { blockTwitter = it } }
        serviceScope.launch { preferencesManager.blockLinkedIn.collect { blockLinkedin = it } }
        serviceScope.launch { preferencesManager.blockingAction.collect { blockingAction = it } }
        serviceScope.launch {
            preferencesManager.scrollBlockMode.collect { mode ->
                sessionTracker.setMode(if (mode == "curious") BlockingMode.CURIOUS else BlockingMode.BLOCK_ALL)
            }
        }
        serviceScope.launch {
            preferencesManager.sessionDurationMinutes.collect { sessionTracker.setSessionDuration(it * 60 * 1000L) }
        }
        serviceScope.launch {
            preferencesManager.cooldownDurationMinutes.collect { sessionTracker.setCooldownDuration(it * 60 * 1000L) }
        }
        serviceScope.launch {
            preferencesManager.allowOneInCooldown.collect { sessionTracker.setAllowOneInCooldown(it) }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!masterEnabled) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName !in Constants.TARGET_PACKAGES) return
        if (!isAppBlocked(packageName)) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> debounceScan(packageName)
        }
    }

    private fun debounceScan(packageName: String) {
        pendingScanRunnable?.let { handler.removeCallbacks(it) }
        pendingScanRunnable = Runnable { handleContentChange(packageName) }
        handler.postDelayed(pendingScanRunnable!!, Constants.DEBOUNCE_MS)
    }

    private fun handleContentChange(packageName: String) {
        val rootNode = rootInActiveWindow ?: return
        try {
            val result = detectionEngine.scan(packageName, rootNode)
            if (result.detected) {
                val verdict = sessionTracker.onContentDetected(result.appTarget)
                when (verdict) {
                    SessionVerdict.ALLOW -> overlayManager.hideIfShowing()
                    SessionVerdict.BLOCK, SessionVerdict.ALREADY_BLOCKED -> {
                        if (!cooldownManager.isInCooldown()) onShortFormContentDetected(result)
                    }
                }
            } else {
                overlayManager.hideIfShowing()
            }
        } finally {
            @Suppress("DEPRECATION") // needed for API < 34
            rootNode.recycle()
        }
    }

    private fun onShortFormContentDetected(result: DetectionResult) {
        cooldownManager.startCooldown()
        when (blockingAction) {
            "close_reel" -> {
                performGlobalAction(GLOBAL_ACTION_BACK)
                handler.postDelayed({ overlayManager.showBlockedOverlay(result) }, Constants.OVERLAY_SHOW_DELAY_MS)
            }
            "close_app" -> performGlobalAction(GLOBAL_ACTION_HOME)
            "lock_device" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                } else {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    private fun isAppBlocked(packageName: String): Boolean = when (packageName) {
        Constants.PACKAGE_INSTAGRAM -> blockInstagram
        Constants.PACKAGE_YOUTUBE -> blockYoutube
        Constants.PACKAGE_TIKTOK, Constants.PACKAGE_TIKTOK_ALT -> blockTiktok
        Constants.PACKAGE_FACEBOOK, Constants.PACKAGE_FACEBOOK_LITE -> blockFacebook
        Constants.PACKAGE_SNAPCHAT -> blockSnapchat
        Constants.PACKAGE_REDDIT -> blockReddit
        Constants.PACKAGE_TWITTER -> blockTwitter
        Constants.PACKAGE_LINKEDIN -> blockLinkedin
        else -> false
    }

    override fun onInterrupt() { overlayManager.hideIfShowing() }

    override fun onDestroy() {
        pendingScanRunnable?.let { handler.removeCallbacks(it) }
        overlayManager.cleanup()
        sessionTracker.resetAll()
        serviceScope.cancel()
        super.onDestroy()
    }
}
