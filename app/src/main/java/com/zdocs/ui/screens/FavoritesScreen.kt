package com.zdocs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zdocs.data.model.ZFile
import com.zdocs.ui.components.FileListItem

@Composable
fun FavoritesScreen(
    files: List<ZFile>,
    onFileClick: (ZFile) -> Unit,
    onFavoriteClick: (ZFile) -> Unit,
    onDeleteClick: (ZFile) -> Unit,
    onRenameClick: (ZFile) -> Unit,
    onShareClick: (ZFile) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(top = 16.dp)) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "${files.size} favorited file${if (files.size != 1) "s" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No favorites yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Star files to find them quickly", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(files, key = { it.path }) { file ->
                    FileListItem(
                        zFile = file,
                        onClick = { onFileClick(file) },
                        onFavoriteClick = { onFavoriteClick(file) },
                        onDeleteClick = { onDeleteClick(file) },
                        onRenameClick = { onRenameClick(file) },
                        onShareClick = { onShareClick(file) }
                    )
                }
            }
        }
    }
}
