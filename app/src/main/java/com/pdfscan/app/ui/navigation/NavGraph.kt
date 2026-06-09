package com.pdfscan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdfscan.app.ui.screens.EditorScreen
import com.pdfscan.app.ui.screens.ExportScreen
import com.pdfscan.app.ui.screens.HomeScreen
import com.pdfscan.app.ui.viewmodels.DocumentViewModel

@Composable
fun PdfScanNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
    ) {
        composable(Routes.Home.route) { entry ->
            val viewModel: DocumentViewModel = hiltViewModel(entry)
            HomeScreen(
                onNavigateToEditor = {
                    navController.navigate(Routes.Editor.route)
                },
                viewModel = viewModel,
            )
        }

        composable(Routes.Editor.route) {
            val parentEntry = remember(it) { navController.getBackStackEntry(Routes.Home.route) }
            val viewModel: DocumentViewModel = hiltViewModel(parentEntry)
            EditorScreen(
                onNavigateToExport = {
                    navController.navigate(Routes.Export.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
            )
        }

        composable(Routes.Export.route) {
            val parentEntry = remember(it) { navController.getBackStackEntry(Routes.Home.route) }
            val viewModel: DocumentViewModel = hiltViewModel(parentEntry)
            ExportScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExportDone = {
                    navController.popBackStack(Routes.Home.route, inclusive = false)
                },
                viewModel = viewModel,
            )
        }
    }
}
