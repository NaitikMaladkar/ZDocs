package com.zdocs.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class StorageFileItem(
    val file: File,
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val childCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(checkStoragePermission(context)) }
    var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
    var isGridView by remember { mutableStateOf(false) }
    var pathHistory by remember { mutableStateOf(listOf(Environment.getExternalStorageDirectory())) }

    // Permission launcher
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        hasPermission = checkStoragePermission(context)
    }

    val readStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.all { it.value }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (!hasPermission) {
        // Permission request screen
        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Storage,
                contentDescription = "Storage",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Storage Access Required",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ZDocs needs storage permission to browse your device files like a file manager.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(
                            android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            android.net.Uri.parse("package:com.zdocs")
                        )
                        manageStorageLauncher.launch(intent)
                    } else {
                        readStorageLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Grant Permission")
            }
        }
        return
    }

    // File manager screen
    val filesInDir = remember(currentDir) {
        currentDir.listFiles()?.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            ?.map { file ->
                StorageFileItem(
                    file = file,
                    name = file.name,
                    isDirectory = file.isDirectory,
                    size = if (file.isFile) file.length() else 0L,
                    lastModified = file.lastModified(),
                    childCount = if (file.isDirectory) (file.listFiles()?.size ?: 0) else 0
                )
            } ?: emptyList()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Storage",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentDir.absolutePath,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    if (pathHistory.size > 1) {
                        IconButton(onClick = {
                            pathHistory = pathHistory.dropLast(1)
                            currentDir = pathHistory.last()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = if (isGridView) "List View" else "Grid View"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (filesInDir.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Empty folder",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filesInDir) { item ->
                    GridFileItem(
                        item = item,
                        onClick = {
                            if (item.isDirectory) {
                                currentDir = item.file
                                pathHistory = pathHistory + item.file
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filesInDir) { item ->
                    ListFileItem(
                        item = item,
                        onClick = {
                            if (item.isDirectory) {
                                currentDir = item.file
                                pathHistory = pathHistory + item.file
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListFileItem(
    item: StorageFileItem,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val icon = if (item.isDirectory) Icons.Filled.Folder else getFileIcon(item.name)
    val iconColor = if (item.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.isDirectory) {
                Text(
                    text = "${item.childCount} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "${formatSize(item.size)} · ${dateFormat.format(Date(item.lastModified))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GridFileItem(
    item: StorageFileItem,
    onClick: () -> Unit
) {
    val icon = if (item.isDirectory) Icons.Filled.Folder else getFileIcon(item.name)
    val iconColor = if (item.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (item.isDirectory) {
                Text(
                    text = "${item.childCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = formatSize(item.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getFileIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    val ext = name.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "pdf" -> Icons.Filled.PictureAsPdf
        "docx", "doc", "odt", "txt", "rtf", "md" -> Icons.Filled.Description
        "xlsx", "xls", "csv", "ods", "tsv" -> Icons.Filled.TableChart
        "pptx", "ppt", "odp" -> Icons.Filled.Slideshow
        "py", "js", "ts", "tsx", "html", "css", "java", "kt", "cpp", "c",
        "go", "rs", "rb", "php", "sql", "swift", "dart", "vue", "sh" -> Icons.Filled.Code
        "json", "xml", "yaml", "yml", "toml", "ini" -> Icons.Filled.DataObject
        "jpg", "jpeg", "png", "gif", "bmp", "webp" -> Icons.Filled.Image
        "mp3", "wav", "ogg", "flac" -> Icons.Filled.MusicNote
        "mp4", "avi", "mkv", "mov" -> Icons.Filled.VideoFile
        "zip", "rar", "7z", "tar", "gz" -> Icons.Filled.FolderZip
        else -> Icons.Filled.InsertDriveFile
    }
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
        else -> "%.1f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
    }
}

private fun checkStoragePermission(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
