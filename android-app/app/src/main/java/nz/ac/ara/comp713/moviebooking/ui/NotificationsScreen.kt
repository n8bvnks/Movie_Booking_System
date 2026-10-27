package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nz.ac.ara.comp713.moviebooking.data.AppNotification
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    vm: NotificationsViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.refresh() }

    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { vm.refresh() }, enabled = !vm.loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh notifications")
            }
        }

        when {
            vm.loading && vm.notifications.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            vm.error != null && vm.notifications.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(vm.error ?: "", color = MaterialTheme.colorScheme.error)
                Button(onClick = { vm.refresh() }) { Text("Try again") }
            }
            vm.notifications.isEmpty() -> Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
                Text("No notifications yet. Book a showing and your confirmation will appear here.")
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.notifications, key = { it.id }) { NotificationCard(it) }
            }
        }
    }
}

@Composable
private fun NotificationCard(n: AppNotification) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(n.message, style = MaterialTheme.typography.titleSmall)
            Text(
                "${n.movieTitle} • ${n.date} at ${n.timeslot} • ${n.seats} seat(s)",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Booking #${n.bookingId} • ${formatReceived(n.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// the server sends an ISO-8601 instant; show it in the phone's own time zone
private fun formatReceived(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return ""
    return try {
        val instant = createdAt.toDoubleOrNull()
            ?.let { Instant.ofEpochSecond(it.toLong()) } // tolerate a numeric timestamp too
            ?: Instant.parse(createdAt)
        instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
    } catch (_: Exception) {
        createdAt
    }
}
