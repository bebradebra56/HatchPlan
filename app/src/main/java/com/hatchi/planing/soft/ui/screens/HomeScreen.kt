package com.hatchi.planing.soft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.ui.components.BatchCard
import com.hatchi.planing.soft.ui.components.EmptyState
import com.hatchi.planing.soft.ui.components.HatchPlanButton
import com.hatchi.planing.soft.ui.components.TaskItem
import com.hatchi.planing.soft.viewmodel.HomeViewModel
import java.util.Date

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBatchClick: (Long) -> Unit,
    onCreateBatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.activeBatches.isEmpty()) {
        EmptyState(
            emoji = "🥚",
            title = "No Active Batches",
            description = "Start your first incubation batch to track your eggs",
            modifier = modifier,
            action = {
                HatchPlanButton(
                    text = "Create Batch",
                    onClick = onCreateBatchClick,
                    modifier = Modifier.width(200.dp)
                )
            }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Batches",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.activeBatches) { batch ->
                    val currentDay = viewModel.getCurrentDay(batch)
                    val totalDays = Date().let {
                        ((batch.expectedHatchDate.time - batch.startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                    }
                    
                    BatchCard(
                        batch = batch,
                        currentDay = currentDay,
                        totalDays = totalDays,
                        onClick = { onBatchClick(batch.id) },
                        modifier = Modifier.width(320.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.todayTasks.count { it.status.name == "PENDING" }} pending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (uiState.todayTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ All tasks completed!",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            items(uiState.todayTasks) { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = { viewModel.completeTask(it) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

