package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.DashboardScreen
import com.example.ui.InventoryScreen
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.PosScreen
import com.example.ui.ReportsScreen
import com.example.ui.SalesLogsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository(database.inventoryDao(), database.salesDao())

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))

                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") { DashboardScreen(navController, viewModel) }
                        composable("inventory") { InventoryScreen(navController, viewModel) }
                        composable("pos") { PosScreen(navController, viewModel) }
                        composable("reports") { ReportsScreen(navController, viewModel) }
                        composable("sales_logs") { SalesLogsScreen(navController, viewModel) }
                    }
                }
            }
        }
    }
}
