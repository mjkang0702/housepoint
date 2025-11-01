package com.example.accordionapp.ui.screens

import com.example.accordionapp.ui.components.AccordionCard
import com.example.accordionapp.ui.components.AddEditDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.accordionapp.model.AccordionItem
import com.example.accordionapp.ui.AccordionViewModel
import com.example.accordionapp.ui.MainViewModel
import com.example.accordionapp.R

data class PageContent(
    val title: String,
    val imageRes: Int, // Drawable resource ID
    val description: String,
    val gradient: List<Color>,
    val accordionColor: Color, // customizable color for accordion items
    val accentColor: Color // color for numerical value containers
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(
    pageIndex: Int,
    onBackPressed: () -> Unit,
    onShowLogin: () -> Unit,
    accordionViewModel: AccordionViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    // Page content configuration - Using Material Icons as placeholders
    val pageContents = listOf(
        PageContent(
            title = "Canonicus",
            imageRes = R.drawable.canonicus_image, // Placeholder - will use icon instead
            description = "Master the fundamentals and canonical principles of your craft.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFFF54949).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Red accent
        ),
        PageContent(
            title = "Amicus",
            imageRes = R.drawable.amicus_image, // Placeholder - will use icon instead
            description = "Build meaningful connections and foster collaborative relationships.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFFFFC830).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Yellow accent
        ),
        PageContent(
            title = "Capellanius",
            imageRes = R.drawable.capellanius_image, // Placeholder - will use icon instead
            description = "Develop spiritual wisdom and sacred understanding.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFF8BC34A).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Green accent
        ),
        PageContent(
            title = "Theoricus",
            imageRes = R.drawable.theoricus_image, // Placeholder - will use icon instead
            description = "Explore theoretical foundations and deep conceptual knowledge.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFF46CDFC).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Blue accent
        ),
        PageContent(
            title = "Educator",
            imageRes = R.drawable.educator_image, // Placeholder - will use icon instead
            description = "Share knowledge and guide others on their learning journey.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFF824FF8).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Purple accent
        ),
        PageContent(
            title = "Seminarium",
            imageRes = R.drawable.seminarium_image, // Placeholder - will use icon instead
            description = "Engage in advanced study and scholarly discourse.",
            gradient = listOf(Color(0xFFFFFFFF), Color(0xFF484848).copy(alpha = 0.5f)),
            accordionColor = Color(0xFFFCFCF7).copy(alpha = 0.9f), // Slightly more visible
            accentColor = Color(0xFF000000) // Dark grey accent
        )
    )

    val currentPageContent = pageContents.getOrElse(pageIndex) {
        PageContent(
            title = "Page ${pageIndex + 1}",
            imageRes = 0, // Fallback - will use icon
            description = "Welcome to this page",
            gradient = listOf(Color(0xFF424242), Color(0xFF757575)),
            accordionColor = Color(0xFF424242).copy(alpha = 0.2f),
            accentColor = Color(0xFF424242)
        )
    }

    val accordionItemsMap by accordionViewModel.accordionItems.collectAsState()
    val items = accordionItemsMap[pageIndex]?.sortedByDescending { it.createdAt } ?: emptyList()

    // Calculate sum of numerical values
    val totalSum = remember(items) {
        items.sumOf { it.numericalValue ?: 0.0 }
    }

    val currentUser by mainViewModel.currentUser.collectAsState()
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState()
    val canEdit = if (isAuthenticated) mainViewModel.canEditPage(pageIndex) else false

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<AccordionItem?>(null) }
    var isEditMode by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = currentPageContent.gradient,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp) // Add top padding to account for floating buttons
        ) {
            // Page Header without Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = currentPageContent.imageRes),
                    contentDescription = "${currentPageContent.title} image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .padding(top = 50.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ), // Optional: subtle background for better visibility

                    contentScale = ContentScale.Fit // Changed from Crop to Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Page Title
                Text(
                    text = currentPageContent.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sum Display
                if (items.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Points",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%.2f", totalSum),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Authentication Status Cards
            if (!isAuthenticated) {

            } else if (!canEdit) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "View-only access to this page (Role: ${currentUser?.role?.name})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Main content area with accordion items
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (items.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = currentPageContent.accordionColor // Use configurable color
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No accordion items yet",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )

                            if (isAuthenticated && canEdit) {
                                Button(
                                    onClick = { showAddDialog = true },
                                    modifier = Modifier.padding(top = 16.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Add First Item")
                                }
                            } else if (!isAuthenticated) {
                                Text(
                                    text = "Login to start adding accordion items",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                                TextButton(
                                    onClick = onShowLogin,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Login Now")
                                }
                            } else {
                                Text(
                                    text = "You don't have permission to edit this page",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        items(items) { item ->
                            AccordionCard(
                                item = item,
                                canEdit = canEdit && isAuthenticated,
                                backgroundColor = currentPageContent.accordionColor, // Pass the custom color
                                accentColor = currentPageContent.accentColor, // Pass the accent color
                                onEdit = {
                                    selectedItem = item
                                    isEditMode = true
                                    showAddDialog = true
                                },
                                onDelete = {
                                    selectedItem = item
                                    showDeleteConfirmation = true
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Floating back button (top left)
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)

        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Floating action button (top right)
        if (!isAuthenticated) {
            TextButton(
                onClick = onShowLogin,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 18.dp)

            ) {
                Text("Login", color = Color.Black)
            }
        } else if (canEdit) {
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top=48.dp, end = 18.dp)

            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Item",
                    tint = Color.Black
                )
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddEditDialog(
                item = if (isEditMode) selectedItem else null,
                onDismiss = {
                    showAddDialog = false
                    isEditMode = false
                    selectedItem = null
                },
                onSave = { title, content, numericalValue ->
                    if (isEditMode && selectedItem != null) {
                        val updated = selectedItem!!.copy(
                            title = title,
                            content = content,
                            numericalValue = numericalValue,
                            lastModifiedBy = currentUser?.id ?: "",
                            lastModifiedAt = System.currentTimeMillis()
                        )
                        currentUser?.id?.let { userId ->
                            accordionViewModel.updateAccordionItem(pageIndex, updated, numericalValue, userId)
                        }
                    } else {
                        currentUser?.id?.let { userId ->
                            accordionViewModel.addAccordionItem(pageIndex, title, content, numericalValue, userId)
                        }
                    }
                }
            )
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirmation = false
                    selectedItem = null
                },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete '${selectedItem?.title}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedItem?.id?.let { itemId ->
                                accordionViewModel.deleteAccordionItem(pageIndex, itemId)
                            }
                            showDeleteConfirmation = false
                            selectedItem = null
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = false
                            selectedItem = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
