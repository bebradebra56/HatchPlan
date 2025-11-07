package com.hatchi.planing.soft.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hatchi.planing.soft.ui.screens.*
import com.hatchi.planing.soft.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    viewModelFactory: ViewModelFactory,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    appViewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = viewModel,
                onBatchClick = { batchId ->
                    navController.navigate(Screen.BatchDetail.createRoute(batchId))
                },
                onCreateBatchClick = {
                    navController.navigate(Screen.CreateBatch.route)
                }
            )
        }

        composable(Screen.Batches.route) {
            val viewModel: BatchesViewModel = viewModel(factory = viewModelFactory)
            BatchesScreen(
                viewModel = viewModel,
                onBatchClick = { batchId ->
                    navController.navigate(Screen.BatchDetail.createRoute(batchId))
                },
                onCreateBatchClick = {
                    navController.navigate(Screen.CreateBatch.route)
                }
            )
        }

        composable(Screen.Calendar.route) {
            val viewModel: CalendarViewModel = viewModel(factory = viewModelFactory)
            CalendarScreen(
                viewModel = viewModel,
                onBatchClick = { batchId ->
                    navController.navigate(Screen.BatchDetail.createRoute(batchId))
                },
                onCreateBatchClick = {
                    navController.navigate(Screen.CreateBatch.route)
                }
            )
        }

        composable(Screen.Reports.route) {
            val viewModel: ReportsViewModel = viewModel(factory = viewModelFactory)
            ReportsScreen(
                viewModel = viewModel,
                onBatchClick = { batchId ->
                    navController.navigate(Screen.BatchDetail.createRoute(batchId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToPresets = {
                    navController.navigate(Screen.Presets.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Presets.route) {
            val viewModel: PresetsViewModel = viewModel(factory = viewModelFactory)
            PresetsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.CreateBatch.route) {
            val viewModel: CreateBatchViewModel = viewModel(factory = viewModelFactory)
            CreateBatchScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onBatchCreated = {
                    navController.navigate(Screen.Batches.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.BatchDetail.route,
            arguments = listOf(
                navArgument("batchId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getLong("batchId") ?: return@composable
            val viewModel = viewModelFactory.createBatchDetailViewModel(batchId)
            
            BatchDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onAddReading = {
                    // TODO: Implement add reading dialog
                },
                onAddNote = {
                    // TODO: Implement add note dialog
                }
            )
        }
    }
}

