package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import nz.ac.ara.comp713.moviebooking.data.Session

private enum class Tab(val label: String, val icon: ImageVector) {
    Showings("Showings", Icons.Default.Home),
    Bookings("My bookings", Icons.Default.DateRange),
    Notifications("Notifications", Icons.Default.Notifications)
}

// Decides what the user sees: the login screen until a customer is logged in, then the app.
@Composable
fun AppRoot() {
    if (Session.token == null) {
        LoginScreen()
    } else {
        MainScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold() {
    val snackbar = remember { SnackbarHostState() }
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hi, ${Session.username}") },
                actions = {
                    IconButton(onClick = { Session.clear() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val content = Modifier.padding(padding)
        when (Tab.entries[tabIndex]) {
            Tab.Showings -> ShowingsScreen(snackbar, content)
            Tab.Bookings -> BookingsScreen(snackbar, content)
            Tab.Notifications -> NotificationsScreen(content)
        }
    }
}
