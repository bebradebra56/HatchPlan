package com.hatchi.planing.soft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hatchi.planing.soft.ui.components.TaskItem
import com.hatchi.planing.soft.util.DateUtils
import com.hatchi.planing.soft.util.SpeciesUtils
import com.hatchi.planing.soft.viewmodel.BatchDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    viewModel: BatchDetailViewModel,
    onNavigateBack: () -> Unit,
    onAddReading: () -> Unit,
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddReadingDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val batch = uiState.batch ?: return
    val preset = uiState.preset

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(batch.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        when (selectedTab) {
                            1 -> showAddReadingDialog = true
                            2 -> showAddNoteDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Add, "Add")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = SpeciesUtils.getSpeciesEmoji(batch.species),
                            style = MaterialTheme.typography.displayMedium
                        )
                        Column {
                            Text(
                                text = SpeciesUtils.getSpeciesDisplayName(batch.species),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Day ${uiState.currentDay}/${preset?.totalDays ?: "?"}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (preset != null) uiState.currentDay.toFloat() / preset.totalDays.toFloat()
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Started",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                            Text(
                                text = DateUtils.formatDate(batch.startDate),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Expected Hatch",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                            Text(
                                text = DateUtils.formatDate(batch.expectedHatchDate),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Timeline") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Readings") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Notes") }
                )
            }

            // Tab content
            when (selectedTab) {
                0 -> TimelineTab(viewModel, uiState)
                1 -> ReadingsTab(uiState)
                2 -> NotesTab(uiState)
            }
        }
    }

    // Dialogs
    if (showAddReadingDialog) {
        com.hatchi.planing.soft.ui.components.AddReadingDialog(
            onDismiss = { showAddReadingDialog = false },
            onConfirm = { temp, humidity, notes ->
                viewModel.addReading(temp, humidity, notes)
            }
        )
    }

    if (showAddNoteDialog) {
        com.hatchi.planing.soft.ui.components.AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { content ->
                viewModel.addNote(content)
            }
        )
    }
}

@Composable
private fun TimelineTab(viewModel: BatchDetailViewModel, uiState: com.hatchi.planing.soft.viewmodel.BatchDetailUiState) {
    val preset = uiState.preset

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (preset != null) {
            items((1..preset.totalDays).toList()) { day ->
                DayCard(
                    day = day,
                    currentDay = uiState.currentDay,
                    tasks = viewModel.getTasksForDay(day),
                    preset = preset,
                    onTaskToggle = { viewModel.completeTask(it) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DayCard(
    day: Int,
    currentDay: Int,
    tasks: List<com.hatchi.planing.soft.data.entity.Task>,
    preset: com.hatchi.planing.soft.data.entity.Preset,
    onTaskToggle: (com.hatchi.planing.soft.data.entity.Task) -> Unit
) {
    val stage = preset.stages.firstOrNull { day in it.dayStart..it.dayEnd }
    val isPast = day < currentDay
    val isToday = day == currentDay

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isToday -> MaterialTheme.colorScheme.primaryContainer
                isPast -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Day $day",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isToday) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (stage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🌡️ ${stage.tempMin}-${stage.tempMax}°C  💧 ${stage.humidityMin}-${stage.humidityMax}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (stage.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stage.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tasks.forEach { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = onTaskToggle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingsTab(uiState: com.hatchi.planing.soft.viewmodel.BatchDetailUiState) {
    if (uiState.readings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📊",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "No readings yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Add temperature and humidity readings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.readings) { reading ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = DateUtils.formatDateTime(reading.timestamp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Temperature",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${reading.temperature}°C",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = "Humidity",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${reading.humidity}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (reading.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = reading.notes,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun NotesTab(uiState: com.hatchi.planing.soft.viewmodel.BatchDetailUiState) {
    if (uiState.notes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📝",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Add observations and photos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.notes) { note ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Day ${note.day}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = DateUtils.formatDate(note.timestamp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

