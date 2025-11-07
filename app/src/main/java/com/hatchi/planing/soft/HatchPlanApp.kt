package com.hatchi.planing.soft

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hatchi.planing.soft.navigation.BottomNavItem
import com.hatchi.planing.soft.navigation.NavGraph
import com.hatchi.planing.soft.navigation.Screen
import com.hatchi.planing.soft.ui.components.AddBatchFab
import com.hatchi.planing.soft.ui.components.BottomNavigationBar
import com.hatchi.planing.soft.viewmodel.AppViewModel
import com.hatchi.planing.soft.viewmodel.ViewModelFactory

@Composable
fun HatchPlanApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val viewModelFactory = ViewModelFactory(context)
    
    val appViewModel: AppViewModel = viewModel(factory = viewModelFactory)
    val appUiState by appViewModel.uiState.collectAsState()

    // Determine if we should show bottom bar and FAB
    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }
    val showFab = currentRoute == Screen.Home.route || currentRoute == Screen.Batches.route

    // Determine start destination based on first launch
    val startDestination = if (appUiState.isFirstLaunch) {
        Screen.Onboarding.route
    } else {
        Screen.Home.route
    }

    if (appUiState.isLoading) {
        // Show loading screen while checking first launch status
        return
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (showFab) {
                AddBatchFab(
                    onClick = { navController.navigate(Screen.CreateBatch.route) }
                )
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            startDestination = startDestination,
            viewModelFactory = viewModelFactory,
            appViewModel = appViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

