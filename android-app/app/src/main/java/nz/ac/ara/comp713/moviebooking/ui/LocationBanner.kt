package nz.ac.ara.comp713.moviebooking.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import nz.ac.ara.comp713.moviebooking.location.Cinema
import nz.ac.ara.comp713.moviebooking.location.distanceToCinemaKm
import nz.ac.ara.comp713.moviebooking.location.driveMinutes
import nz.ac.ara.comp713.moviebooking.location.formatDistance
import nz.ac.ara.comp713.moviebooking.location.getDeviceLocation
import nz.ac.ara.comp713.moviebooking.location.hasLocationPermission
import nz.ac.ara.comp713.moviebooking.location.isDrivable
import nz.ac.ara.comp713.moviebooking.location.serviceAreas

// Mobile-specific feature: how far is the customer from the cinema?
// Device location first; if the customer says no, or no fix is available, they pick their area.
@Composable
fun LocationBanner(modifier: Modifier = Modifier, vm: LocationViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fetchDeviceLocation: () -> Unit = {
        vm.fetchStarted()
        scope.launch {
            val location = getDeviceLocation(context)
            if (location != null) {
                vm.useDevice(location.latitude, location.longitude)
            } else {
                vm.deviceUnavailable("We couldn't get your location. Is location switched on?")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            fetchDeviceLocation()
        } else {
            vm.deviceUnavailable("Location permission was denied.")
        }
    }

    val requestLocation: () -> Unit = {
        if (hasLocationPermission(context)) {
            fetchDeviceLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // if permission was granted on an earlier visit, just use it - no need to ask again
    LaunchedEffect(Unit) {
        if (vm.origin == null && hasLocationPermission(context)) fetchDeviceLocation()
    }

    Card(modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                val origin = vm.origin
                when {
                    vm.fetching -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("  Finding your location…", style = MaterialTheme.typography.bodyMedium)
                    }
                    origin != null -> {
                        val km = origin.distanceToCinemaKm()
                        Text(
                            "${formatDistance(km)} from ${Cinema.NAME}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        val source = if (origin.fromDevice) "from your device" else "from ${origin.name}"
                        val drive = if (isDrivable(km)) " • about ${driveMinutes(km)} min drive" else ""
                        Text(
                            "Measured $source$drive",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> Text(
                        "See how far you are from the cinema",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = requestLocation, enabled = !vm.fetching) {
                    Text(if (vm.origin == null) "Use my location" else "Update")
                }
                TextButton(onClick = { vm.showPicker = true }, enabled = !vm.fetching) {
                    Text("Choose area")
                }
            }
        }
    }

    if (vm.showPicker) {
        AlertDialog(
            onDismissRequest = { vm.showPicker = false },
            title = { Text("Where are you travelling from?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    vm.notice?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                    LazyColumn(Modifier.heightIn(max = 320.dp)) {
                        items(serviceAreas, key = { it.name }) { area ->
                            TextButton(onClick = { vm.useManual(area) }, modifier = Modifier.fillMaxWidth()) {
                                Text(area.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { vm.showPicker = false }) { Text("Close") } }
        )
    }
}
