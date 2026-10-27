package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nz.ac.ara.comp713.moviebooking.data.Showing
import nz.ac.ara.comp713.moviebooking.location.distanceToCinemaKm
import nz.ac.ara.comp713.moviebooking.location.driveMinutes
import nz.ac.ara.comp713.moviebooking.location.isDrivable

@Composable
fun ShowingsScreen(
    snackbar: SnackbarHostState,
    modifier: Modifier = Modifier,
    vm: ShowingsViewModel = viewModel(),
    location: LocationViewModel = viewModel()
) {
    var selected by remember { mutableStateOf<Showing?>(null) }

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
            Text("Now showing", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { vm.refresh() }, enabled = !vm.loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh showings")
            }
        }

        LocationBanner(Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

        when {
            vm.loading && vm.showings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            vm.error != null && vm.showings.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(vm.error ?: "", color = MaterialTheme.colorScheme.error)
                Button(onClick = { vm.refresh() }) { Text("Try again") }
            }
            vm.showings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No showings available yet")
            }
            else -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.showings, key = { "${it.movieTitle}|${it.date}|${it.time}" }) { showing ->
                    ShowingCard(showing, onBook = { selected = showing })
                }
            }
        }
    }

    selected?.let { showing ->
        val travel = location.origin?.let { origin ->
            val km = origin.distanceToCinemaKm()
            if (isDrivable(km)) driveMinutes(km) else null
        }
        BookDialog(
            showing = showing,
            travelMinutes = travel,
            onDismiss = { selected = null },
            onConfirm = { seats ->
                vm.book(showing, seats)
                selected = null
            }
        )
    }
}

@Composable
private fun ShowingCard(showing: Showing, onBook: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(showing.movieTitle, style = MaterialTheme.typography.titleMedium)
            Text(
                "${showing.genre} • ${showing.runtime} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.padding(top = 4.dp))
            Text(
                showing.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.padding(top = 8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("${showing.date} at ${showing.time}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (showing.seats > 0) "${showing.seats} seats left" else "Sold out",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (showing.seats > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Button(onClick = onBook, enabled = showing.seats > 0) { Text("Book") }
            }
        }
    }
}

@Composable
private fun BookDialog(showing: Showing, travelMinutes: Int?, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var seats by remember { mutableIntStateOf(1) }
    val maxSeats = minOf(showing.seats, 10)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(showing.movieTitle) },
        text = {
            Column {
                Text("${showing.date} at ${showing.time}")
                if (travelMinutes != null) {
                    Text(
                        "Allow about $travelMinutes min to get there",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.padding(top = 12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { if (seats > 1) seats-- }, enabled = seats > 1) { Text("−") }
                    Text(
                        "$seats seat(s)",
                        modifier = Modifier.width(110.dp).padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedButton(onClick = { if (seats < maxSeats) seats++ }, enabled = seats < maxSeats) { Text("+") }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(seats) }) { Text("Confirm booking") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
