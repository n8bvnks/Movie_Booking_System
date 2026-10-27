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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nz.ac.ara.comp713.moviebooking.data.Booking

@Composable
fun BookingsScreen(
    snackbar: SnackbarHostState,
    modifier: Modifier = Modifier,
    vm: BookingsViewModel = viewModel()
) {
    var toCancel by remember { mutableStateOf<Booking?>(null) }

    // reload every time the tab is opened, so a booking just made on the Showings tab appears
    LaunchedEffect(Unit) { vm.refresh() }

    LaunchedEffect(vm.message) {
        vm.message?.let {
            snackbar.showSnackbar(it)
            vm.message = null
        }
    }

    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("My bookings", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { vm.refresh() }, enabled = !vm.loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh bookings")
            }
        }

        when {
            vm.loading && vm.bookings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            vm.error != null && vm.bookings.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(vm.error ?: "", color = MaterialTheme.colorScheme.error)
                Button(onClick = { vm.refresh() }) { Text("Try again") }
            }
            vm.bookings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("You have no bookings yet")
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.bookings, key = { it.bookingId }) { booking ->
                    BookingCard(booking, onCancel = { toCancel = booking })
                }
            }
        }
    }

    toCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { toCancel = null },
            title = { Text("Cancel booking #${booking.bookingId}?") },
            text = { Text("${booking.seats} seat(s) for ${booking.movieTitle} on ${booking.date} at ${booking.timeslot} will be released.") },
            confirmButton = {
                Button(onClick = {
                    vm.cancel(booking)
                    toCancel = null
                }) { Text("Cancel booking") }
            },
            dismissButton = { TextButton(onClick = { toCancel = null }) { Text("Keep it") } }
        )
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(booking.movieTitle, style = MaterialTheme.typography.titleMedium)
                Text("${booking.date} at ${booking.timeslot}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${booking.seats} seat(s) • Booking #${booking.bookingId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onCancel) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
