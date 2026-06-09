package com.pdfscan.app.ui.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Editor : Routes("editor")
    data object Export : Routes("export")
}
