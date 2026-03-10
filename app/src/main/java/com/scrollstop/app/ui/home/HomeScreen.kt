package com.scrollstop.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.data.StatsRepository
import com.scrollstop.app.data.local.ScrollStopDatabase
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.security.PinState
import com.scrollstop.app.ui.components.PinDialogMode
import com.scrollstop.app.ui.components.PinEntryDialog
import com.scrollstop.app.ui.components.ProgressRing
import com.scrollstop.app.ui.components.ProtectedSwitch
import com.scrollstop.app.ui.components.StreakDisplay
import com.scrollstop.app.ui.theme.InstagramGradientEnd
import com.scrollstop.app.ui.theme.ScrollStopPrimary
import com.scrollstop.app.ui.theme.ScrollStopSecondary
import com.scrollstop.app.ui.theme.ScrollStopTertiary
import com.scrollstop.app.ui.theme.SuccessGreen
import com.scrollstop.app.ui.theme.TikTokPink
import com.scrollstop.app.ui.theme.YouTubeRed
import com.scrollstop.app.util.AccessibilityUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    preferencesManager: PreferencesManager,
    pinManager: PinManager,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val masterEnabled by preferencesManager.masterEnabled.collectAsState(initial = true)
    val blockMode by preferencesManager.scrollBlockMode.collectAsState(initial = "block_all")
    val blockInstagram by preferencesManager.blockInstagramReels.collectAsState(initial = true)
    val blockYoutube by preferencesManager.blockYoutubeShorts.collectAsState(initial = true)
    val blockTiktok by preferencesManager.blockTiktok.collectAsState(initial = true)
    val blockFacebook by preferencesManager.blockFacebook.collectAsState(initial = true)
    val blockSnapchat by preferencesManager.blockSnapchat.collectAsState(initial = true)
    val blockReddit by preferencesManager.blockReddit.collectAsState(initial = true)
    val blockTwitter by preferencesManager.blockTwitter.collectAsState(initial = true)
    val blockLinkedin by preferencesManager.blockLinkedIn.collectAsState(initial = true)
    val currentStreak by preferencesManager.currentStreak.collectAsState(initial = 0)
    val bestStreak by preferencesManager.bestStreak.collectAsState(initial = 0)
    val parentalLockEnabled by pinManager.isParentalLockEnabled.collectAsState(initial = false)
    val isServiceRunning = AccessibilityUtil.isServiceEnabled(context)

    val db = remember { ScrollStopDatabase.getInstance(context) }
    val statsRepo = remember { StatsRepository(db.blockEventDao()) }
    val todayBlocks by statsRepo.getTodayBlockCount().collectAsState(initial = 0)
    val totalBlocks by statsRepo.getTotalBlockCount().collectAsState(initial = 0)
    val timeSaved by statsRepo.getEstimatedTimeSaved().collectAsState(initial = "0m")

    var showSettingsPinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ScrollStop", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = {
                        if (parentalLockEnabled && !PinState.checkAuthValid()) showSettingsPinDialog = true
                        else onNavigateToSettings()
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Service warning
            if (!isServiceRunning) {
                item {
                    Card(
                        onClick = { AccessibilityUtil.openAccessibilitySettings(context) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Protection Disabled", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                Text("Tap to enable accessibility service", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Master Toggle + Mode
            item {
                val bgColor by animateColorAsState(
                    if (masterEnabled) ScrollStopPrimary.copy(alpha = 0.08f) else Color(0xFFF5F5F5),
                    label = "bg"
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor)
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Shield, null,
                            modifier = Modifier.height(48.dp),
                            tint = if (masterEnabled) ScrollStopPrimary else Color.Gray
                        )
                        Spacer(Modifier.height(12.dp))
                        ProtectedSwitch(
                            checked = masterEnabled,
                            onCheckedChange = { scope.launch { preferencesManager.setMasterEnabled(it) } },
                            pinManager = pinManager,
                            modifier = Modifier.scale(1.3f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (masterEnabled) "Protection Active" else "Protection Off",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (masterEnabled) ScrollStopPrimary else Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (blockMode == "curious") "Curious Plan" else "Block All Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Streak
            if (currentStreak > 0) {
                item { StreakDisplay(currentStreak, bestStreak, Modifier.fillMaxWidth()) }
            }

            // Stats
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProgressRing(value = "$todayBlocks", label = "today", progress = (todayBlocks / 20f).coerceAtMost(1f), color = ScrollStopPrimary)
                    ProgressRing(value = "$totalBlocks", label = "total", progress = (totalBlocks / 100f).coerceAtMost(1f), color = ScrollStopTertiary)
                    ProgressRing(value = timeSaved, label = "saved", progress = 1f, color = SuccessGreen)
                }
            }

            // Pause button
            item {
                OutlinedButton(
                    onClick = { /* TODO: wire pause */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Pause, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Take a Break (5 min)")
                }
            }

            // Apps grid
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Blocked Apps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        AppToggle("Instagram Reels", Color(0xFFE1306C), blockInstagram, pinManager) { scope.launch { preferencesManager.setBlockInstagramReels(it) } }
                        AppToggle("YouTube Shorts", Color(0xFFFF0000), blockYoutube, pinManager) { scope.launch { preferencesManager.setBlockYoutubeShorts(it) } }
                        AppToggle("TikTok", Color(0xFFFE2C55), blockTiktok, pinManager) { scope.launch { preferencesManager.setBlockTiktok(it) } }
                        AppToggle("Facebook Reels", Color(0xFF1877F2), blockFacebook, pinManager) { scope.launch { preferencesManager.setBlockFacebook(it) } }
                        AppToggle("Snapchat", Color(0xFFFFFC00), blockSnapchat, pinManager) { scope.launch { preferencesManager.setBlockSnapchat(it) } }
                        AppToggle("Reddit", Color(0xFFFF4500), blockReddit, pinManager) { scope.launch { preferencesManager.setBlockReddit(it) } }
                        AppToggle("X (Twitter)", Color(0xFF1DA1F2), blockTwitter, pinManager) { scope.launch { preferencesManager.setBlockTwitter(it) } }
                        AppToggle("LinkedIn", Color(0xFF0A66C2), blockLinkedin, pinManager) { scope.launch { preferencesManager.setBlockLinkedIn(it) } }
                    }
                }
            }

            // Quick actions
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledTonalButton(
                        onClick = {
                            if (parentalLockEnabled && !PinState.checkAuthValid()) showSettingsPinDialog = true
                            else onNavigateToSettings()
                        },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, null); Spacer(Modifier.width(4.dp)); Text("Settings")
                    }
                    FilledTonalButton(onClick = onNavigateToStats, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.BarChart, null); Spacer(Modifier.width(4.dp)); Text("Stats")
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showSettingsPinDialog) {
        PinEntryDialog(PinDialogMode.VERIFY, pinManager, { showSettingsPinDialog = false; onNavigateToSettings() }, { showSettingsPinDialog = false })
    }
}

@Composable
private fun AppToggle(name: String, accent: Color, enabled: Boolean, pinManager: PinManager, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.width(4.dp).height(20.dp)) { drawRect(accent) }
            Spacer(Modifier.width(12.dp))
            Text(name, style = MaterialTheme.typography.bodyLarge)
        }
        ProtectedSwitch(checked = enabled, onCheckedChange = onToggle, pinManager = pinManager)
    }
}
