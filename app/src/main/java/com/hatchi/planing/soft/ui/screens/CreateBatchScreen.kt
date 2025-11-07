package com.hatchi.planing.soft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hatchi.planing.soft.data.model.Species
import com.hatchi.planing.soft.ui.components.HatchPlanButton
import com.hatchi.planing.soft.ui.components.HatchPlanOutlinedButton
import com.hatchi.planing.soft.util.DateUtils
import com.hatchi.planing.soft.util.SpeciesUtils
import com.hatchi.planing.soft.viewmodel.CreateBatchViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBatchScreen(
    viewModel: CreateBatchViewModel,
    onNavigateBack: () -> Unit,
    onBatchCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Batch") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { uiState.step / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step content
            when (uiState.step) {
                1 -> Step1SelectSpecies(
                    selectedSpecies = uiState.species,
                    onSpeciesSelected = viewModel::selectSpecies,
                    modifier = Modifier.weight(1f)
                )
                2 -> Step2SelectPreset(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
                3 -> Step3BatchDetails(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
                4 -> Step4Review(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.step > 1) {
                    HatchPlanOutlinedButton(
                        text = "Back",
                        onClick = viewModel::previousStep,
                        modifier = Modifier.weight(1f)
                    )
                }

                HatchPlanButton(
                    text = if (uiState.step == 4) "Create Batch" else "Next",
                    onClick = {
                        if (uiState.step == 4) {
                            viewModel.createBatch(onBatchCreated)
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    enabled = when (uiState.step) {
                        1 -> uiState.species != null
                        2 -> uiState.selectedPreset != null
                        3 -> uiState.batchName.isNotEmpty() && uiState.totalEggs.isNotEmpty()
                        else -> true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun Step1SelectSpecies(
    selectedSpecies: Species?,
    onSpeciesSelected: (Species) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Select Bird Species",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose the type of eggs you'll be incubating",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Species.entries.filter { it != Species.CUSTOM }) { species ->
                SpeciesCard(
                    species = species,
                    isSelected = species == selectedSpecies,
                    onClick = { onSpeciesSelected(species) }
                )
            }
        }
    }
}

@Composable
private fun SpeciesCard(
    species: Species,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(width = 2.dp)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = SpeciesUtils.getSpeciesEmoji(species),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = SpeciesUtils.getSpeciesDisplayName(species),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun Step2SelectPreset(
    viewModel: CreateBatchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val presets = uiState.presets.filter { it.species == uiState.species }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Select Incubation Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose a preset or customize your own settings",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(presets) { preset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectPreset(preset) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (preset.id == uiState.selectedPreset?.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${preset.totalDays} days • ${preset.turnsPerDay}x turns/day",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (preset.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step3BatchDetails(
    viewModel: CreateBatchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Batch Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.batchName,
            onValueChange = viewModel::updateBatchName,
            label = { Text("Batch Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.totalEggs,
            onValueChange = viewModel::updateTotalEggs,
            label = { Text("Number of Eggs") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = DateUtils.formatDate(uiState.startDate),
            onValueChange = { },
            label = { Text("Start Date") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, "Select Date")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.notes,
            onValueChange = viewModel::updateNotes,
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }

    if (showDatePicker) {
        com.hatchi.planing.soft.ui.components.DatePickerDialog(
            onDateSelected = { date ->
                viewModel.updateStartDate(date)
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun Step4Review(
    viewModel: CreateBatchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val preset = uiState.selectedPreset

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Review & Confirm",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReviewRow("Species", uiState.species?.let { SpeciesUtils.getSpeciesDisplayName(it) } ?: "")
                ReviewRow("Batch Name", uiState.batchName)
                ReviewRow("Number of Eggs", uiState.totalEggs)
                ReviewRow("Start Date", DateUtils.formatDate(uiState.startDate))
                preset?.let {
                    ReviewRow("Duration", "${it.totalDays} days")
                    ReviewRow("Expected Hatch", DateUtils.formatDate(DateUtils.addDays(uiState.startDate, it.totalDays)))
                }
                if (uiState.notes.isNotEmpty()) {
                    ReviewRow("Notes", uiState.notes)
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

