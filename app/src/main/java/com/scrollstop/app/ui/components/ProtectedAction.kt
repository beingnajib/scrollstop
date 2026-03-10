package com.scrollstop.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.security.PinState

@Composable
fun ProtectedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    pinManager: PinManager,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val parentalLockEnabled by pinManager.isParentalLockEnabled.collectAsState(initial = false)
    var showPinDialog by remember { mutableStateOf(false) }
    var pendingValue by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (parentalLockEnabled) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Protected",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.width(4.dp))
        }
        Switch(
            checked = checked,
            onCheckedChange = { newValue ->
                if (parentalLockEnabled && !PinState.checkAuthValid()) {
                    pendingValue = newValue
                    showPinDialog = true
                } else {
                    onCheckedChange(newValue)
                }
            },
            enabled = enabled
        )
    }

    if (showPinDialog) {
        PinEntryDialog(
            mode = PinDialogMode.VERIFY,
            pinManager = pinManager,
            onSuccess = {
                showPinDialog = false
                onCheckedChange(pendingValue)
            },
            onDismiss = { showPinDialog = false }
        )
    }
}
