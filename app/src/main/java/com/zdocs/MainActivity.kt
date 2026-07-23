package com.zdocs

import android.content.Intent
import android.os.Bundle
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zdocs.data.repository.FileRepository
import com.zdocs.ui.components.ZDocsBottomNavBar
import com.zdocs.ui.navigation.Screen
import com.zdocs.ui.navigation.ZDocsNavGraph
import com.zdocs.ui.theme.ZDocsTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: FileRepository
    private var externalFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = FileRepository(this)
        handleIncomingFile(intent)

        setContent {
            val context = LocalContext.current
            var isDarkMode by remember {
                mutableStateOf(
                    context.getSharedPreferences("zdocs_prefs", MODE_PRIVATE)
                        .getBoolean("dark_mode", false)
                )
            }

            ZDocsTheme(darkTheme = isDarkMode) {
                ZDocsMainContent(
                    repository = repository,
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = { enabled ->
                        isDarkMode = enabled
                        context.getSharedPreferences("zdocs_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("dark_mode", enabled)
                            .apply()
                    },
                    externalFilePath = externalFilePath,
                    onExternalFileHandled = { externalFilePath = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingFile(intent)
    }

    private fun handleIncomingFile(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            Intent.ACTION_VIEW, Intent.ACTION_EDIT -> {
                intent.data?.let { uri ->
                    try {
                        val fileName = uri.lastPathSegment ?: "imported_file.txt"
                        val content = contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
                        val tempFile = File(cacheDir, fileName)
                        tempFile.writeText(content)
                        externalFilePath = tempFile.absolutePath
                    } catch (_: Exception) {}
                }
            }
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("text/") == true ||
                    intent.type == "application/json" ||
                    intent.type == "application/xml") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                        val fileName = "shared_${System.currentTimeMillis()}.txt"
                        val tempFile = File(cacheDir, fileName)
                        tempFile.writeText(text)
                        externalFilePath = tempFile.absolutePath
                    }
                }
            }
        }
    }
}

@Composable
fun ZDocsMainContent(
    repository: FileRepository,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    externalFilePath: String?,
    onExternalFileHandled: () -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // If there's an external file to show, navigate to ExternalEditor
    LaunchedEffect(externalFilePath) {
        if (externalFilePath != null) {
            navController.navigate(Screen.ExternalEditor.createRoute(externalFilePath)) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            onExternalFileHandled()
        }
    }

    val showBottomBar = currentRoute != Screen.Editor.route &&
                        currentRoute != Screen.ExternalEditor.route

    val bottomNavRoute = when {
        currentRoute == Screen.Home.route -> "home"
        currentRoute == Screen.Recents.route -> "recents"
        currentRoute == Screen.Storage.route -> "storage"
        currentRoute == Screen.Collabs.route -> "collabs"
        currentRoute == Screen.Settings.route -> "settings"
        else -> "home"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                ZDocsBottomNavBar(
                    currentRoute = bottomNavRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        ZDocsNavGraph(
            navController = navController,
            repository = repository,
            isDarkTheme = isDarkMode,
            onDarkModeToggle = onDarkModeToggle,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
