package com.scrollstop.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrollstop.app.ui.theme.StarGold
import com.scrollstop.app.ui.theme.StreakFire

@Composable
fun StreakDisplay(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    if (currentStreak <= 0) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            if (currentStreak >= 7) Icons.Default.LocalFireDepartment else Icons.Default.Star,
            contentDescription = "Streak",
            tint = if (currentStreak >= 7) StreakFire else StarGold,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "$currentStreak-day streak!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (currentStreak >= 7) StreakFire else StarGold
            )
            if (currentStreak >= bestStreak && bestStreak > 1) {
                Text(
                    "Personal Best!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
