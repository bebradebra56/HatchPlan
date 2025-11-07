package com.hatchi.planing.soft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hatchi.planing.soft.ui.components.BatchCard
import com.hatchi.planing.soft.ui.components.EmptyState
import com.hatchi.planing.soft.ui.components.HatchPlanButton
import com.hatchi.planing.soft.viewmodel.BatchesViewModel
import java.util.Date

@Composable
fun BatchesScreen(
    viewModel: BatchesViewModel,
    onBatchClick: (Long) -> Unit,
    onCreateBatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "My Batches",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active (${uiState.activeBatches.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed (${uiState.completedBatches.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("All (${uiState.allBatches.size})") }
                )
            }
        }

        // Content
        val batches = when (selectedTab) {
            0 -> uiState.activeBatches
            1 -> uiState.completedBatches
            else -> uiState.allBatches
        }

        if (batches.isEmpty()) {
            EmptyState(
                emoji = if (selectedTab == 0) "🥚" else "📦",
                title = if (selectedTab == 0) "No Active Batches" else "No Batches",
                description = if (selectedTab == 0)
                    "Create your first batch to start tracking your incubation"
                else
                    "You don't have any batches in this category yet",
                action = if (selectedTab == 0) {
                    {
                        HatchPlanButton(
                            text = "Create Batch",
                            onClick = onCreateBatchClick,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                } else null
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(batches) { batch ->
                    val currentDay = viewModel.getCurrentDay(batch)
                    val totalDays = Date().let {
                        ((batch.expectedHatchDate.time - batch.startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                    }

                    BatchCard(
                        batch = batch,
                        currentDay = currentDay,
                        totalDays = totalDays,
                        onClick = { onBatchClick(batch.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

