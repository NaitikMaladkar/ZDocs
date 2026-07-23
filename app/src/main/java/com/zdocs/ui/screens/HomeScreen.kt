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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zdocs.data.model.FileType
import com.zdocs.data.model.ZFile
import com.zdocs.ui.components.CreateFileDialog
import com.zdocs.ui.components.FileListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allFiles: List<ZFile>,
    onCreateFile: (String, FileType, String) -> Unit,
    onFileClick: (ZFile) -> Unit,
    onFavoriteClick: (ZFile) -> Unit,
    onDeleteClick: (ZFile) -> Unit,
    onRenameClick: (ZFile) -> Unit,
    onShareClick: (ZFile) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ZDocs",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Create, Manage, Share files",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create New")
            }
        }
    ) { paddingValues ->
        if (allFiles.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No files yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap + to create your first file",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(allFiles, key = { it.path }) { file ->
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
