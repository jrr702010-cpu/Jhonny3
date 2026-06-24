package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.DashboardScreen
import com.example.ui.components.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        val usdRate by viewModel.usdRate.collectAsState()
                        val eurRate by viewModel.eurRate.collectAsState()
                        val isLoading by viewModel.isLoading.collectAsState()

                        DashboardScreen(
                            usdRate = usdRate,
                            eurRate = eurRate,
                            isLoading = isLoading,
                            onSettingsClick = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        Scaffold(
                            topBar = {
                                // Add a simple back button or top bar if needed, 
                                // but we'll let SettingsScreen handle its own padding,
                                // wait, Scaffold will provide it.
                                androidx.compose.material3.TopAppBar(
                                    title = { androidx.compose.material3.Text("Ajustes") },
                                    navigationIcon = {
                                        androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                                            androidx.compose.material3.Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack, 
                                                contentDescription = "Volver"
                                            )
                                        }
                                    }
                                )
                            }
                        ) { innerPadding ->
                            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                                SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Force refresh on resume as requested
        viewModel.fetchRates()
    }
}
