package com.hatchi.planing.soft.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Batches : Screen("batches")
    object Calendar : Screen("calendar")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Presets : Screen("presets")
    object About : Screen("about")
    object CreateBatch : Screen("create_batch")
    object BatchDetail : Screen("batch_detail/{batchId}") {
        fun createRoute(batchId: Long) = "batch_detail/$batchId"
    }
    object AddReading : Screen("add_reading/{batchId}") {
        fun createRoute(batchId: Long) = "add_reading/$batchId"
    }
    object AddNote : Screen("add_note/{batchId}") {
        fun createRoute(batchId: Long) = "add_note/$batchId"
    }
}

