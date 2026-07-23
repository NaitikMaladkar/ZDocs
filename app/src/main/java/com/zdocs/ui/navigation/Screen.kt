package com.zdocs.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Recents : Screen("recents")
    data object Storage : Screen("storage")
    data object Collabs : Screen("collabs")
    data object Settings : Screen("settings")
    data object Editor : Screen("editor/{filePath}") {
        fun createRoute(filePath: String): String = "editor/${java.net.URLEncoder.encode(filePath, "UTF-8")}"
    }
    data object ExternalEditor : Screen("external_editor/{filePath}") {
        fun createRoute(filePath: String): String = "external_editor/${java.net.URLEncoder.encode(filePath, "UTF-8")}"
    }
}
