package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    // We navigate to POS upon finding an item from the search
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.updateSearchQuery(searchQuery)
            // Ideally wait for posScannedItem not to be null
        }
    }
    
    val scannedItem by viewModel.posScannedItem.collectAsState()
    LaunchedEffect(scannedItem) {
        if (scannedItem != null && searchQuery.isNotEmpty()) {
            navController.navigate("pos")
            searchQuery = "" // Reset
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("HOME APP", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                        Text("MICRO-TECH", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    DashboardCard(
                        title = "صندوق النقدية\n(شاشة البيع)",
                        icon = Icons.Default.ShoppingCart,
                        onClick = { navController.navigate("pos") }
                    )
                }
                item {
                    DashboardCard(
                        title = "إدارة المخزن\n(المشتريات)",
                        icon = Icons.Default.Store,
                        onClick = { navController.navigate("inventory") }
                    )
                }
                item {
                    DashboardCard(
                        title = "سجل المبيعات\nاليومية",
                        icon = Icons.Default.List,
                        onClick = { navController.navigate("sales_logs") }
                    )
                }
                item {
                    DashboardCard(
                        title = "تقارير الأرباح\nوالإحصائيات",
                        icon = Icons.Default.Timeline,
                        onClick = { navController.navigate("reports") }
                    )
                }
            }
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("اكتب اسم القطعة أو امسح الباركود للبيع...") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.End)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}
