package com.example.accordionapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.accordionapp.model.AccordionItem
import com.example.accordionapp.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

// Updated AccordionCard component with custom background color support
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccordionCard(
    item: AccordionItem,
    canEdit: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    backgroundColor: Color = Color(0xFFFCFCF7).copy(alpha = 0.7f), // Add custom background color parameter
    accentColor: Color = Color(0xFF6200EE) // Add accent color for numerical value container
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Remove shadow for cleaner look
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor // Actually use the background color parameter
        )
    ) {
        Column {
            // Header with title and numerical value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title (takes most space)
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                // Numerical value display - always show, even when 0
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.numericalValue != 0.0) {
                            accentColor.copy(alpha = 0.9f) // Use accent color with transparency
                        } else {
                            Color.Gray.copy(alpha = 0.2f) // Subtle gray for zero values
                        }
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (item.numericalValue % 1.0 == 0.0) {
                            item.numericalValue.toInt().toString()
                        } else {
                            String.format("%.2f", item.numericalValue)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White, // Consistent text color
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }

                // Edit/Delete buttons
                if (canEdit) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Expand/Collapse icon
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    Divider(modifier = Modifier.padding(bottom = 12.dp))
                    Text(
                        text = item.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
