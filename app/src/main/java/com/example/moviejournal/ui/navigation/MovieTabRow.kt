package com.example.moviejournal.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moviejournal.navigation.MovieJournalDestination

@Composable
fun MovieTabRow(
    allScreens: List<MovieJournalDestination>,
    onTabSelected: (MovieJournalDestination) -> Unit,
    currentScreen: MovieJournalDestination
) {


    NavigationBar {
        allScreens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.titleRes)
                    )
                },
                label = { Text(stringResource(screen.titleRes)) },
                selected = currentScreen == screen,
                onClick = { onTabSelected(screen) }
            )
        }
    }
}