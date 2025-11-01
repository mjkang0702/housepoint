package com.example.accordionapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.accordionapp.ui.MainViewModel
import com.example.accordionapp.ui.AccordionViewModel

data class PageInfo(
    val index: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val category: String
)

data class PageWithPoints(
    val pageInfo: PageInfo,
    val totalPoints: Double,
    val itemCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPage: (Int) -> Unit,
    onShowLogin: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
    accordionViewModel: AccordionViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val accordionItemsMap by accordionViewModel.accordionItems.collectAsState()

    // Define page information with colors matching the original design
    val pages = listOf(
        PageInfo(0, "Canonicus", "Foundation Principles", Icons.Default.School, Color(0xFFF54949), "Foundation"),
        PageInfo(1, "Amicus", "Community Building", Icons.Default.Group, Color(0xFFFFC830), "Community"),
        PageInfo(2, "Capellanius", "Spiritual Wisdom", Icons.Default.AutoAwesome, Color(0xFF8BC34A), "Wisdom"),
        PageInfo(3, "Theoricus", "Deep Knowledge", Icons.Default.Psychology, Color(0xFF46CDFC), "Theory"),
        PageInfo(4, "Educator", "Teaching & Guidance", Icons.Default.Lightbulb, Color(0xFF824FF8), "Teaching"),
        PageInfo(5, "Seminarium", "Advanced Study", Icons.Default.MenuBook, Color(0xFF484848), "Advanced")
    )

    // Calculate points for each page
    val pagesWithPoints = remember(accordionItemsMap) {
        pages.map { pageInfo ->
            val items = accordionItemsMap[pageInfo.index] ?: emptyList()
            val totalPoints = items.sumOf { it.numericalValue ?: 0.0 }
            val itemCount = items.size
            PageWithPoints(pageInfo, totalPoints, itemCount)
        }
    }

    // Sort pages by points (descending) for podium
    val rankedPages = remember(pagesWithPoints) {
        pagesWithPoints.sortedByDescending { it.totalPoints }
    }

    var showPodiumView by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CCAA House Points") },
                actions = {
                    // View toggle button
                    IconButton(
                        onClick = { showPodiumView = !showPodiumView }
                    ) {
                        Icon(
                            imageVector = if (showPodiumView) Icons.Default.GridView else Icons.Default.EmojiEvents,
                            contentDescription = if (showPodiumView) "Grid View" else "Podium View"
                        )
                    }

                    if (isAuthenticated) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = currentUser?.username ?: "User",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            IconButton(onClick = onLogout) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout"
                                )
                            }
                        }
                    } else {
                        TextButton(onClick = onShowLogin) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Login",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Login")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFFFFF)) // Change this to your preferred background color
        ) {
            // Animated transition between views
            Crossfade(
                targetState = showPodiumView,
                animationSpec = tween(300),
                label = "view_transition"
            ) { isPodiumView ->
                if (isPodiumView) {
                    PodiumView(
                        rankedPages = rankedPages,
                        onNavigateToPage = onNavigateToPage,
                        canEdit = { pageIndex ->
                            if (isAuthenticated) viewModel.canEditPage(pageIndex) else false
                        }
                    )
                } else {
                    GridView(
                        pagesWithPoints = pagesWithPoints,
                        onNavigateToPage = onNavigateToPage,
                        canEdit = { pageIndex ->
                            if (isAuthenticated) viewModel.canEditPage(pageIndex) else false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun PodiumView(
    rankedPages: List<PageWithPoints>,
    onNavigateToPage: (Int) -> Unit,
    canEdit: (Int) -> Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "House Points Leaderboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        items(rankedPages.take(3)) { pageWithPoints ->
            val rank = rankedPages.indexOf(pageWithPoints) + 1
            PodiumCard(
                pageWithPoints = pageWithPoints,
                rank = rank,
                onNavigateToPage = onNavigateToPage,
                canEdit = canEdit(pageWithPoints.pageInfo.index)
            )
        }

        if (rankedPages.size > 3) {
            item {
                Text(
                    text = "Other Houses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(rankedPages.drop(3)) { pageWithPoints ->
                val rank = rankedPages.indexOf(pageWithPoints) + 1
                RegularRankCard(
                    pageWithPoints = pageWithPoints,
                    rank = rank,
                    onNavigateToPage = onNavigateToPage,
                    canEdit = canEdit(pageWithPoints.pageInfo.index)
                )
            }
        }
    }
}

@Composable
private fun PodiumCard(
    pageWithPoints: PageWithPoints,
    rank: Int,
    onNavigateToPage: (Int) -> Unit,
    canEdit: Boolean
) {
    val podiumHeight = when (rank) {
        1 -> 200.dp // Increased from 180.dp
        2 -> 180.dp // Increased from 160.dp
        3 -> 160.dp // Increased from 140.dp
        else -> 140.dp // Increased from 120.dp
    }

    val medalEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "🏅"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(podiumHeight)
            .clickable { onNavigateToPage(pageWithPoints.pageInfo.index) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFCFCF7) // Dark gray - change this to your preferred color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (rank == 1) 8.dp else 6.dp // Reduced elevation
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        pageWithPoints.pageInfo.color.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "$medalEmoji #$rank",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = pageWithPoints.pageInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = pageWithPoints.pageInfo.color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pageWithPoints.pageInfo.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = pageWithPoints.pageInfo.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%.1f", pageWithPoints.totalPoints),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = pageWithPoints.pageInfo.color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Points",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pageWithPoints.itemCount.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Permission indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = if (canEdit) "✓ Can Edit" else "👁 View Only",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canEdit)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RegularRankCard(
    pageWithPoints: PageWithPoints,
    rank: Int,
    onNavigateToPage: (Int) -> Unit,
    canEdit: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Increased from 100.dp
            .clickable { onNavigateToPage(pageWithPoints.pageInfo.index) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFCFCF7) // Same color as podium cards
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        pageWithPoints.pageInfo.color.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = pageWithPoints.pageInfo.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = pageWithPoints.pageInfo.color
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pageWithPoints.pageInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${String.format("%.1f", pageWithPoints.totalPoints)} pts • ${pageWithPoints.itemCount} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = if (canEdit) "✓" else "👁",
                style = MaterialTheme.typography.bodyMedium,
                color = if (canEdit)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GridView(
    pagesWithPoints: List<PageWithPoints>,
    onNavigateToPage: (Int) -> Unit,
    canEdit: (Int) -> Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pagesWithPoints) { pageWithPoints ->
            GridPageCard(
                pageWithPoints = pageWithPoints,
                onNavigateToPage = onNavigateToPage,
                canEdit = canEdit(pageWithPoints.pageInfo.index)
            )
        }
    }
}

@Composable
private fun GridPageCard(
    pageWithPoints: PageWithPoints,
    onNavigateToPage: (Int) -> Unit,
    canEdit: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Increased from 160.dp
            .clickable { onNavigateToPage(pageWithPoints.pageInfo.index) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E) // Same color as other cards
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = pageWithPoints.pageInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = pageWithPoints.pageInfo.color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pageWithPoints.pageInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = pageWithPoints.pageInfo.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${String.format("%.1f", pageWithPoints.totalPoints)} pts",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = pageWithPoints.pageInfo.color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (canEdit) "✓ Can Edit" else "👁 View Only",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canEdit)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
