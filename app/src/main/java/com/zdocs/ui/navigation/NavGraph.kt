package com.zdocs.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zdocs.data.model.FileType
import com.zdocs.data.model.ZFile
import com.zdocs.data.repository.FileRepository
import com.zdocs.ui.components.RenameFileDialog
import com.zdocs.ui.screens.*
import java.io.File

@Composable
fun ZDocsNavGraph(
    navController: NavHostController,
    repository: FileRepository,
    isDarkTheme: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            var currentFiles by remember { mutableStateOf(repository.getAllFiles()) }
            var fileToRename by remember { mutableStateOf<ZFile?>(null) }

            HomeScreen(
                allFiles = currentFiles,
                onCreateFile = { name, type, content ->
                    repository.createFile(name, type, content)
                    currentFiles = repository.getAllFiles()
                },
                onFileClick = { file ->
                    navController.navigate(Screen.Editor.createRoute(file.path))
                },
                onFavoriteClick = { file ->
                    repository.toggleFavorite(file)
                    currentFiles = repository.getAllFiles()
                },
                onDeleteClick = { file ->
                    repository.deleteFile(file)
                    currentFiles = repository.getAllFiles()
                },
                onRenameClick = { file -> fileToRename = file },
                onShareClick = { file -> shareFile(context, file) }
            )

            fileToRename?.let { zFile ->
                RenameFileDialog(
                    zFile = zFile,
                    onDismiss = { fileToRename = null },
                    onRename = { newName ->
                        repository.renameFile(zFile, newName)
                        currentFiles = repository.getAllFiles()
                        fileToRename = null
                    }
                )
            }
        }

        composable(Screen.Recents.route) {
            var recentFiles by remember { mutableStateOf(repository.getRecentFiles()) }
            var fileToRename by remember { mutableStateOf<ZFile?>(null) }

            RecentsScreen(
                files = recentFiles,
                onFileClick = { file -> navController.navigate(Screen.Editor.createRoute(file.path)) },
                onFavoriteClick = { file ->
                    repository.toggleFavorite(file)
                    recentFiles = repository.getRecentFiles()
                },
                onDeleteClick = { file ->
                    repository.deleteFile(file)
                    recentFiles = repository.getRecentFiles()
                },
                onRenameClick = { file -> fileToRename = file },
                onShareClick = { file -> shareFile(context, file) }
            )

            fileToRename?.let { zFile ->
                RenameFileDialog(
                    zFile = zFile,
                    onDismiss = { fileToRename = null },
                    onRename = { newName ->
                        repository.renameFile(zFile, newName)
                        recentFiles = repository.getRecentFiles()
                        fileToRename = null
                    }
                )
            }
        }

        composable(Screen.Storage.route) {
            StorageScreen()
        }

        composable(Screen.Collabs.route) {
            CollabsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                isDarkMode = isDarkTheme,
                onDarkModeToggle = onDarkModeToggle,
                storagePath = repository.getBaseDir().absolutePath,
                totalStorageUsed = formatStorageSize(repository.getTotalStorageUsed()),
                onClearCache = { repository.clearCache() }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("filePath") ?: "", "UTF-8"
            )
            val file = File(filePath)
            val zFile = if (file.exists()) ZFile.fromFile(file) else null
            val content = zFile?.let { repository.readFileContent(it) } ?: ""

            val isSpreadsheet = zFile?.extension?.lowercase() in listOf("csv", "tsv")
            val csvData = if (isSpreadsheet && content != null) repository.parseCsv(content) else null

            if (zFile != null) {
                EditorScreen(
                    zFile = zFile,
                    content = content ?: "",
                    onSave = { newContent ->
                        repository.writeFileContent(zFile, newContent)
                    },
                    onBack = { navController.popBackStack() },
                    isDarkTheme = isDarkTheme,
                    csvData = csvData,
                    onCsvDataChange = { newData ->
                        val newContent = repository.serializeCsv(newData)
                        repository.writeFileContent(zFile, newContent)
                    },
                    isExternalFile = false
                )
            }
        }

        // External file editor: opens directly, closes app on back
        composable(
            route = Screen.ExternalEditor.route,
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("filePath") ?: "", "UTF-8"
            )
            val file = File(filePath)
            val zFile = if (file.exists()) ZFile.fromFile(file, isFavorite = false).copy(isFromExternal = true) else null
            val content = zFile?.let { file.readText() } ?: ""

            val isSpreadsheet = zFile?.extension?.lowercase() in listOf("csv", "tsv")
            val csvData = if (isSpreadsheet && content != null) repository.parseCsv(content) else null

            if (zFile != null) {
                EditorScreen(
                    zFile = zFile,
                    content = content,
                    onSave = { newContent ->
                        file.writeText(newContent)
                    },
                    onBack = {
                        (context as Activity).finish()
                    },
                    isDarkTheme = isDarkTheme,
                    csvData = csvData,
                    onCsvDataChange = { newData ->
                        val newContent = repository.serializeCsv(newData)
                        file.writeText(newContent)
                    },
                    isExternalFile = true
                )
            }
        }
    }
}

private fun shareFile(context: Context, zFile: ZFile) {
    try {
        val file = File(zFile.path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${zFile.name}"))
    } catch (e: Exception) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "File: ${zFile.name}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${zFile.name}"))
    }
}

private fun formatStorageSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    }
}
