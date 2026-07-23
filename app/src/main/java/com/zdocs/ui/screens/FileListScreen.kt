package com.zdocs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zdocs.data.model.FileType
import com.zdocs.data.model.ZFile
import com.zdocs.ui.components.CreateFileDialog
import com.zdocs.ui.components.FileListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    fileType: FileType,
    files: List<ZFile>,
    onFileClick: (ZFile) -> Unit,
    onFavoriteClick: (ZFile) -> Unit,
    onDeleteClick: (ZFile) -> Unit,
    onRenameClick: (ZFile) -> Unit,
    onShareClick: (ZFile) -> Unit,
    onCreateFile: (String, FileType, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = fileType.color,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create ${fileType.displayName}")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("← Back") }
            }

            Text(
                text = fileType.displayName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = fileType.color,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = fileType.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "${files.size} file${if (files.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (files.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No ${fileType.displayName.lowercase()} yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap + to create one", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
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

    if (showCreateDialog) {
        CreateFileDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, type, content ->
                onCreateFile(name, type, content)
                showCreateDialog = false
            }
        )
    }
}
