package com.sudokumaster.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sudokumaster.android.presentation.ui.auth.AuthScreen
import com.sudokumaster.android.presentation.ui.game.GameScreen
import com.sudokumaster.android.presentation.ui.profile.ProfileScreen
import com.sudokumaster.android.presentation.theme.SudokuMasterExpressiveTheme
import com.sudokumaster.android.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SudokuMasterExpressiveTheme {
                SudokuMasterApp()
            }
        }
    }
}

@Composable
fun SudokuMasterApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    
    // Development utility: Delete pgujar user on startup
    LaunchedEffect(Unit) {
        try {
            authViewModel.deleteUser("pgujar")
        } catch (e: Exception) {
            println("Note: Could not delete pgujar user (this is expected if user doesn't exist or no network): ${e.message}")
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "auth"
        ) {
            composable("auth") {
                AuthScreen(
                    authViewModel = authViewModel,
                    onNavigateToGame = {
                        navController.navigate("game") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            
            composable("game") {
                GameScreen(
                    authViewModel = authViewModel,
                    onNavigateToAuth = {
                        navController.navigate("auth") {
                            popUpTo("game") { inclusive = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    }
                )
            }
            
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.navigate("auth") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}