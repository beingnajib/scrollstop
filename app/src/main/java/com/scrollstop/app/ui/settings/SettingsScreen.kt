package com.scrollstop.app.ui.settings

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.ui.components.PinDialogMode
import com.scrollstop.app.ui.components.PinEntryDialog
import com.scrollstop.app.ui.theme.ScrollStopPrimary
import com.scrollstop.app.ui.theme.SuccessGreen
import com.scrollstop.app.util.AccessibilityUtil
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    pinManager: PinManager,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val blockMode by preferencesManager.scrollBlockMode.collectAsState(initial = "block_all")
    val blockingAction by preferencesManager.blockingAction.collectAsState(initial = "close_reel")
    val sessionDuration by preferencesManager.sessionDurationMinutes.collectAsState(initial = 3)
    val cooldownDuration by preferencesManager.cooldownDurationMinutes.collectAsState(initial = 30)
    val allowOne by preferencesManager.allowOneInCooldown.collectAsState(initial = false)
    val pauseDuration by preferencesManager.pauseDurationMinutes.collectAsState(initial = 5)
    val maxPause by preferencesManager.maxPauseMinutes.collectAsState(initial = 15)
    val parentalLockEnabled by pinManager.isParentalLockEnabled.collectAsState(initial = false)
    val isServiceRunning = AccessibilityUtil.isServiceEnabled(context)

    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showPinChangeDialog by remember { mutableStateOf(false) }
    var showResetCode by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Service status
            item {
                Card(
                    onClick = { if (!isServiceRunning) AccessibilityUtil.openAccessibilitySettings(context) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isServiceRunning) SuccessGreen.copy(alpha = 0.1f) else ScrollStopPrimary.copy(alpha = 0.1f))
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (isServiceRunning) Icons.Default.Check else Icons.Default.Close, null, tint = if (isServiceRunning) SuccessGreen else ScrollStopPrimary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Accessibility Service", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(if (isServiceRunning) "Running" else "Tap to enable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Blocking Mode
            item {
                Section("Blocking Mode") {
                    val modes = listOf("block_all" to "Block All — Block immediately on detection", "curious" to "Curious Plan — Allow a session window, then block")
                    modes.forEach { (value, label) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = blockMode == value, onClick = { scope.launch { preferencesManager.setScrollBlockMode(value) } })
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Curious Plan settings (only show when curious mode selected)
            if (blockMode == "curious") {
                item {
                    Section("Curious Plan Settings") {
                        SliderRow("Session duration", "${sessionDuration}min", sessionDuration.toFloat(), 1f..15f, 13) {
                            scope.launch { preferencesManager.setSessionDurationMinutes(it.roundToInt()) }
                        }
                        SliderRow("Cooldown duration", "${cooldownDuration}min", cooldownDuration.toFloat(), 15f..60f, 8) {
                            scope.launch { preferencesManager.setCooldownDurationMinutes(it.roundToInt()) }
                        }
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Allow 1 video in cooldown", style = MaterialTheme.typography.bodyMedium)
                            Switch(checked = allowOne, onCheckedChange = { scope.launch { preferencesManager.setAllowOneInCooldown(it) } })
                        }
                    }
                }
            }

            // Blocking Action
            item {
                Section("When Blocked") {
                    val actions = listOf(
                        "close_reel" to "Close the reel",
                        "close_app" to "Close the app",
                        "lock_device" to "Lock your device"
                    )
                    actions.forEach { (value, label) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = blockingAction == value, onClick = { scope.launch { preferencesManager.setBlockingAction(value) } })
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // Pause Settings
            item {
                Section("Pause / Break") {
                    Text("Configure the temporary pause feature.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    SliderRow("Pause duration", "${pauseDuration}min", pauseDuration.toFloat(), 1f..15f, 13) {
                        scope.launch { preferencesManager.setPauseDurationMinutes(it.roundToInt()) }
                    }
                    SliderRow("Max pause per day", "${maxPause}min", maxPause.toFloat(), 5f..60f, 10) {
                        scope.launch { preferencesManager.setMaxPauseMinutes(it.roundToInt()) }
                    }
                }
            }

            // Parental Lock
            item {
                Section("Parental Lock") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Enable Parental Lock", style = MaterialTheme.typography.bodyLarge)
                            Text("Require PIN to change settings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = parentalLockEnabled, onCheckedChange = { if (it) showPinSetupDialog = true else scope.launch { pinManager.clearPin() } })
                    }
                    if (parentalLockEnabled) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showPinChangeDialog = true }) { Icon(Icons.Default.Lock, null); Spacer(Modifier.width(4.dp)); Text("Change PIN") }
                    }
                }
            }

            // About
            item {
                Section("About") {
                    Text("ScrollStop v3.0.0", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Free, privacy-first. No data leaves your device.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showPinSetupDialog) PinEntryDialog(PinDialogMode.SETUP, pinManager, { showPinSetupDialog = false; showResetCode = it }, { showPinSetupDialog = false })
    if (showPinChangeDialog) PinEntryDialog(PinDialogMode.CHANGE, pinManager, { showPinChangeDialog = false; showResetCode = it }, { showPinChangeDialog = false })
    showResetCode?.let { code ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetCode = null },
            title = { Text("Save Your Reset Code", fontWeight = FontWeight.Bold) },
            text = { Column { Text("Write this down:"); Spacer(Modifier.height(8.dp)); Text(code, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("Won't be shown again.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) } },
            confirmButton = { Button(onClick = { showResetCode = null }) { Text("I've saved it") } }
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SliderRow(label: String, valueText: String, value: Float, range: ClosedFloatingPointRange<Float>, steps: Int, onChanged: (Float) -> Unit) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(valueText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }
        Slider(value = value, onValueChange = onChanged, valueRange = range, steps = steps)
    }
}
