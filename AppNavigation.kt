package com.example.accordionapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.accordionapp.ui.MainViewModel
import com.example.accordionapp.ui.screens.HomeScreen
import com.example.accordionapp.ui.screens.PageScreen
import com.example.accordionapp.ui.screens.LoginDialog

@Composable
fun AppNavigation(viewModel: MainViewModel = hiltViewModel()) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf<Int?>(null) }

    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    when {
        currentPage != null -> {
            // Use your existing PageScreen composable
            PageScreen(
                pageIndex = currentPage!!,
                onBackPressed = { currentPage = null },
                onShowLogin = { showLoginDialog = true },
                mainViewModel = viewModel  // Fixed parameter name
            )
        }
        else -> {
            // Show home screen
            HomeScreen(
                onNavigateToPage = { pageIndex -> currentPage = pageIndex },
                onShowLogin = { showLoginDialog = true },
                onLogout = {
                    viewModel.logout()
                    // Reset to home page after logout
                    currentPage = null
                },
                viewModel = viewModel
            )
        }
    }

    // Login dialog appears over whatever screen is showing
    if (showLoginDialog) {
        LoginDialog(
            onDismiss = { showLoginDialog = false },
            onLoginSuccess = { showLoginDialog = false },
            viewModel = viewModel
        )
    }
}

// Your existing PageScreen.kt handles all the accordion functionality
// No need for a duplicate PageScreen composable here
