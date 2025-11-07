package com.hatchi.planing.soft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.TaskStatus
import com.hatchi.planing.soft.ui.theme.LimeGreen
import com.hatchi.planing.soft.util.DateUtils
import com.hatchi.planing.soft.util.TaskUtils

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            IconButton(
                onClick = { onToggleComplete(task) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (task.status == TaskStatus.DONE)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Outlined.Circle,
                    contentDescription = if (task.status == TaskStatus.DONE) "Completed" else "Pending",
                    tint = if (task.status == TaskStatus.DONE)
                        LimeGreen
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Task icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = TaskUtils.getTaskIcon(task.type),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Task details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = TaskUtils.getTaskDisplayName(task.type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = DateUtils.formatTime(task.dueDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Status badge
            if (task.status == TaskStatus.MISSED) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = "Missed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else if (task.status == TaskStatus.DONE) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LimeGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = LimeGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

