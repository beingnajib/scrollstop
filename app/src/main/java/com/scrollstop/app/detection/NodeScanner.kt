package com.scrollstop.app.detection

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.util.Constants

@Suppress("DEPRECATION") // recycle() needed for API < 34 compat
object NodeScanner {

    fun findNode(
        node: AccessibilityNodeInfo?,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH,
        predicate: (AccessibilityNodeInfo) -> Boolean
    ): Boolean {
        if (node == null || maxDepth <= 0) return false
        if (predicate(node)) return true

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                if (findNode(child, maxDepth - 1, predicate)) return true
            } finally {
                child.recycle()
            }
        }
        return false
    }

    fun findByViewId(rootNode: AccessibilityNodeInfo, resourceId: String): Boolean {
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceId)
        val found = nodes.isNotEmpty()
        nodes.forEach { it.recycle() }
        return found
    }

    fun findByContentDescription(
        rootNode: AccessibilityNodeInfo,
        pattern: Regex,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH
    ): Boolean {
        return findNode(rootNode, maxDepth) { node ->
            node.contentDescription?.let { pattern.containsMatchIn(it) } == true
        }
    }

    fun findByText(
        rootNode: AccessibilityNodeInfo,
        pattern: Regex,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH
    ): Boolean {
        return findNode(rootNode, maxDepth) { node ->
            node.text?.let { pattern.containsMatchIn(it) } == true
        }
    }

    fun findSelectedNodeWithText(
        rootNode: AccessibilityNodeInfo,
        pattern: Regex,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH
    ): Boolean {
        return findNode(rootNode, maxDepth) { node ->
            node.isSelected &&
                (node.text?.let { pattern.containsMatchIn(it) } == true ||
                    node.contentDescription?.let { pattern.containsMatchIn(it) } == true)
        }
    }

    fun hasVideoSurface(
        rootNode: AccessibilityNodeInfo,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH
    ): Boolean {
        return findNode(rootNode, maxDepth) { node ->
            node.className?.toString()?.let {
                it.contains("SurfaceView") ||
                    it.contains("TextureView") ||
                    it.contains("PlayerView")
            } == true
        }
    }

    fun hasVerticalPager(
        rootNode: AccessibilityNodeInfo,
        maxDepth: Int = Constants.MAX_SCAN_DEPTH
    ): Boolean {
        return findNode(rootNode, maxDepth) { node ->
            node.className?.toString() == "androidx.viewpager2.widget.ViewPager2"
        }
    }

    fun computeContentFingerprint(
        rootNode: AccessibilityNodeInfo,
        maxNodes: Int = Constants.FINGERPRINT_MAX_NODES,
        maxDepth: Int = Constants.FINGERPRINT_MAX_DEPTH
    ): Int {
        val builder = StringBuilder()
        collectSignificantText(rootNode, builder, maxNodes, maxDepth, 0)
        return builder.toString().hashCode()
    }

    private fun collectSignificantText(
        node: AccessibilityNodeInfo?,
        builder: StringBuilder,
        remaining: Int,
        maxDepth: Int,
        currentDepth: Int
    ): Int {
        if (node == null || remaining <= 0 || currentDepth > maxDepth) return remaining

        var left = remaining

        // Collect text that likely identifies video content (usernames, captions)
        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()

        if (text != null && text.length > 3 && !text.all { it.isDigit() }) {
            builder.append(text).append("|")
            left--
        } else if (desc != null && desc.length > 3 && !desc.all { it.isDigit() }) {
            builder.append(desc).append("|")
            left--
        }

        if (left <= 0) return left

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                left = collectSignificantText(child, builder, left, maxDepth, currentDepth + 1)
                if (left <= 0) break
            } finally {
                child.recycle()
            }
        }
        return left
    }
}
