package com.scrollstop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.security.PinVerifyResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PinDialogMode { SETUP, VERIFY, CHANGE }

@Composable
fun PinEntryDialog(
    mode: PinDialogMode,
    pinManager: PinManager,
    onSuccess: (resetCode: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var remainingAttempts by remember { mutableStateOf(5) }
    var isLockedOut by remember { mutableStateOf(false) }
    var lockoutSeconds by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Lockout countdown timer
    LaunchedEffect(isLockedOut) {
        if (isLockedOut) {
            while (lockoutSeconds > 0) {
                delay(1000)
                lockoutSeconds--
            }
            isLockedOut = false
            errorMessage = null
        }
    }

    val title = when {
        mode == PinDialogMode.SETUP && !isConfirmStep -> "Set Parent PIN"
        mode == PinDialogMode.SETUP && isConfirmStep -> "Confirm PIN"
        mode == PinDialogMode.VERIFY -> "Enter Parent PIN"
        mode == PinDialogMode.CHANGE && !isConfirmStep -> "Enter New PIN"
        else -> "Confirm New PIN"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // PIN dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    val currentPin = if (isConfirmStep) confirmPin else pin
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index < currentPin.length)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Error message
                errorMessage?.let { msg ->
                    Text(
                        msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (isLockedOut) {
                    Text(
                        "Try again in ${lockoutSeconds}s",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Number pad
                val numbers = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )
                numbers.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(Modifier.size(72.dp).padding(4.dp))
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable(enabled = !isLockedOut) {
                                            val currentPin = if (isConfirmStep) confirmPin else pin
                                            if (key == "DEL") {
                                                if (isConfirmStep) {
                                                    confirmPin = confirmPin.dropLast(1)
                                                } else {
                                                    pin = pin.dropLast(1)
                                                }
                                                errorMessage = null
                                            } else if (currentPin.length < 4) {
                                                if (isConfirmStep) {
                                                    confirmPin += key
                                                } else {
                                                    pin += key
                                                }
                                                errorMessage = null

                                                // Auto-submit on 4 digits
                                                val updatedPin = if (isConfirmStep) confirmPin + (if (confirmPin.length < 3) "" else key) else pin + (if (pin.length < 3) "" else key)
                                                if (updatedPin.length == 4) {
                                                    scope.launch {
                                                        handlePinComplete(
                                                            mode, pin, confirmPin + if (!isConfirmStep) "" else key,
                                                            isConfirmStep, pinManager,
                                                            onSuccess = onSuccess,
                                                            onConfirmStep = {
                                                                isConfirmStep = true
                                                            },
                                                            onError = { msg, attempts, locked, seconds ->
                                                                errorMessage = msg
                                                                remainingAttempts = attempts
                                                                isLockedOut = locked
                                                                lockoutSeconds = seconds
                                                                if (isConfirmStep) confirmPin = "" else pin = ""
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    if (key == "DEL") {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            key,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (mode == PinDialogMode.VERIFY) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Attempts remaining: $remainingAttempts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private suspend fun handlePinComplete(
    mode: PinDialogMode,
    pin: String,
    confirmPin: String,
    isConfirmStep: Boolean,
    pinManager: PinManager,
    onSuccess: (String?) -> Unit,
    onConfirmStep: () -> Unit,
    onError: (String, Int, Boolean, Int) -> Unit
) {
    when (mode) {
        PinDialogMode.SETUP, PinDialogMode.CHANGE -> {
            if (!isConfirmStep) {
                if (pin.length == 4) {
                    onConfirmStep()
                }
            } else {
                if (pin == confirmPin) {
                    val resetCode = pinManager.setPin(pin)
                    onSuccess(resetCode)
                } else {
                    onError("PINs don't match. Try again.", 5, false, 0)
                }
            }
        }
        PinDialogMode.VERIFY -> {
            val actualPin = if (isConfirmStep) confirmPin else pin
            when (pinManager.verifyPin(actualPin)) {
                PinVerifyResult.CORRECT -> onSuccess(null)
                PinVerifyResult.INCORRECT -> {
                    val remaining = pinManager.getRemainingAttempts()
                    onError("Wrong PIN. $remaining attempts remaining.", remaining, false, 0)
                }
                PinVerifyResult.LOCKED_OUT -> {
                    val lockMs = pinManager.getLockoutRemainingMs()
                    onError("Too many attempts.", 0, true, (lockMs / 1000).toInt())
                }
            }
        }
    }
}
