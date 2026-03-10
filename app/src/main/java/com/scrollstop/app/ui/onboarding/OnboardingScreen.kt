package com.scrollstop.app.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.ui.components.GradientBackground
import com.scrollstop.app.ui.components.MascotState
import com.scrollstop.app.ui.components.PinDialogMode
import com.scrollstop.app.ui.components.PinEntryDialog
import com.scrollstop.app.ui.components.ShieldMascot
import com.scrollstop.app.util.AccessibilityUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    preferencesManager: PreferencesManager,
    pinManager: PinManager,
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    GradientBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> HowItWorksPage()
                    2 -> PermissionPage(context)
                    3 -> ConfigurePage(preferencesManager)
                    4 -> PinSetupPage(pinManager)
                }
            }

            // Page indicators
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == pagerState.currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = pagerState.currentPage > 0) {
                    TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                        Text("Back")
                    }
                }
                if (pagerState.currentPage == 0) Spacer(Modifier.width(1.dp))

                if (pagerState.currentPage < 4) {
                    Button(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }) {
                        Text("Next")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                } else {
                    Button(onClick = {
                        scope.launch {
                            preferencesManager.setOnboardingCompleted(true)
                            onComplete()
                        }
                    }) {
                        Text("Get Started")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ShieldMascot(state = MascotState.HAPPY, size = 120.dp)
        Spacer(Modifier.height(32.dp))
        Text(
            "Take Control of\nYour Scrolling",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "ScrollStop detects and blocks Reels, Shorts, and TikToks before you fall into the doomscroll trap.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HowItWorksPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ShieldMascot(state = MascotState.CELEBRATING, size = 80.dp)
        Spacer(Modifier.height(24.dp))
        Text("How It Works", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        val steps = listOf(
            "We watch for short-form video feeds using Android's Accessibility Service",
            "After a few videos, we gently block and remind you to take a break",
            "Your data never leaves your device"
        )
        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                Text("${index + 1}.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(28.dp))
                Text(step, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun PermissionPage(context: android.content.Context) {
    var isEnabled by remember { mutableStateOf(AccessibilityUtil.isServiceEnabled(context)) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Lock, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(32.dp))
        Text("Enable Accessibility Service", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text("This is the only way to detect content inside other apps. We never read your messages, passwords, or personal data.",
            style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        if (isEnabled) {
            FilledTonalButton(onClick = {}) {
                Icon(Icons.Filled.Check, null); Spacer(Modifier.width(8.dp)); Text("Service Enabled")
            }
        } else {
            Button(onClick = { AccessibilityUtil.openAccessibilitySettings(context) }) { Text("Enable Accessibility Service") }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { isEnabled = AccessibilityUtil.isServiceEnabled(context) }) { Text("Check Status") }
    }
}

@Composable
private fun ConfigurePage(preferencesManager: PreferencesManager) {
    val scope = rememberCoroutineScope()
    val blockInstagram by preferencesManager.blockInstagramReels.collectAsState(initial = true)
    val blockYoutube by preferencesManager.blockYoutubeShorts.collectAsState(initial = true)
    val blockTiktok by preferencesManager.blockTiktok.collectAsState(initial = true)

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Choose What to Block", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("You can change these anytime in Settings", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        ToggleRow("Instagram Reels", blockInstagram) { scope.launch { preferencesManager.setBlockInstagramReels(it) } }
        ToggleRow("YouTube Shorts", blockYoutube) { scope.launch { preferencesManager.setBlockYoutubeShorts(it) } }
        ToggleRow("TikTok", blockTiktok) { scope.launch { preferencesManager.setBlockTiktok(it) } }
    }
}

@Composable
private fun PinSetupPage(pinManager: PinManager) {
    var showPinDialog by remember { mutableStateOf(false) }
    var pinSet by remember { mutableStateOf(false) }
    var resetCode by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.Lock, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Text("Set Up Parent PIN", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("This prevents your child from changing blocking settings. You can skip this and set it up later.",
            style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))

        if (pinSet) {
            FilledTonalButton(onClick = {}) {
                Icon(Icons.Filled.Check, null); Spacer(Modifier.width(8.dp)); Text("PIN Set!")
            }
            resetCode?.let { code ->
                Spacer(Modifier.height(16.dp))
                Text("Your reset code:", style = MaterialTheme.typography.bodyMedium)
                Text(code, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Write this down! It won't be shown again.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        } else {
            Button(onClick = { showPinDialog = true }) { Text("Set PIN") }
        }
    }

    if (showPinDialog) {
        PinEntryDialog(mode = PinDialogMode.SETUP, pinManager = pinManager,
            onSuccess = { code -> showPinDialog = false; pinSet = true; resetCode = code },
            onDismiss = { showPinDialog = false })
    }
}

@Composable
private fun ToggleRow(name: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
        Switch(checked = enabled, onCheckedChange = onToggle)
    }
}
