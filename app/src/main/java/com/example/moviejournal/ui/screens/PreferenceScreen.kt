package com.example.moviejournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.moviejournal.utils.PreferencesManager

@Composable
fun PreferencesScreen(
    preferencesManager: PreferencesManager,
    onRequestGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBackgroundUri by remember {
        mutableStateOf(preferencesManager.backgroundImageUri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "App Preferences",
            style = MaterialTheme.typography.headlineMedium
        )

        // Background Image Selection
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "App Background",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (currentBackgroundUri != null) {
                    Image(
                        painter = rememberImagePainter(currentBackgroundUri),
                        contentDescription = "Current background",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onRequestGallery,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Background Image")
                }

                if (currentBackgroundUri != null) {
                    Button(
                        onClick = {
                            preferencesManager.backgroundImageUri = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Remove Background")
                    }
                }
            }
        }
    }
}