package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.data.InventoryItem
import com.example.data.SalesLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavController, viewModel: MainViewModel) {
    var barcode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }

    var showCamera by remember { mutableStateOf(false) }
    
    val items by viewModel.inventoryItems.collectAsState()

    if (showCamera) {
        Dialog(onDismissRequest = { showCamera = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            BarcodeScannerScreen(
                onBarcodeScanned = { 
                    barcode = it
                    showCamera = false
                },
                onClose = { showCamera = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة المخزن والمشتريات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = { Text("باركود القطعة") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showCamera = true },
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم القطعة / السلعة") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("سعر الجملة (التكلفة)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("سعر البيع") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("الكمية") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (name.isNotBlank() && cost.isNotBlank() && price.isNotBlank()) {
                                viewModel.addOrUpdateInventoryItem(
                                    barcode = barcode.ifBlank { "GEN_" + System.currentTimeMillis() },
                                    name = name,
                                    cost = cost.toDoubleOrNull() ?: 0.0,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    qty = qty.toIntOrNull() ?: 1
                                )
                                barcode = ""; name = ""; cost = ""; price = ""; qty = "1"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("حفظ وتخزين السلعة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("المخزون المتوفر:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(item.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("كود: ${item.barcode}") },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("السعر: ${item.price}")
                                Text("المتوفر: ${item.qty} ق", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(navController: NavController, viewModel: MainViewModel) {
    var searchBarcode by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }
    val scannedItem by viewModel.posScannedItem.collectAsState()

    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(searchBarcode) {
        if (searchBarcode.isNotEmpty()) {
            viewModel.searchItemByBarcode(searchBarcode) { item ->
                if (item != null) viewModel.setPosScannedItem(item)
            }
        }
    }

    if (showCamera) {
        Dialog(onDismissRequest = { showCamera = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            BarcodeScannerScreen(
                onBarcodeScanned = { 
                    searchBarcode = it
                    showCamera = false
                },
                onClose = { showCamera = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("صندوق النقدية والبيع", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showToast) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "تم معالجة العملية وتحديث البيانات!",
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showToast = false
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchBarcode,
                    onValueChange = { searchBarcode = it },
                    label = { Text("امسح باركود للبيع الفوري...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { showCamera = true },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (scannedItem == null) {
                        Text(
                            "في انتظار قراءة أو مسح باركود السلعة لعرض البيانات وجلب السعر تلقائياً...",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (scannedItem!!.qty <= 0) {
                        Text(
                            "⚠️ السلعة: ${scannedItem!!.name} نفدت كميتها تماماً!",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text("🛍️ بضاعة جاهزة للبيع الفوري:", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(scannedItem!!.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("السعر المطلوب: ${scannedItem!!.price} ر.ي", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("المتوفر على الرف: ${scannedItem!!.qty} قطعة", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (scannedItem != null && scannedItem!!.qty > 0) {
                Button(
                    onClick = {
                        viewModel.processSale(scannedItem!!)
                        searchBarcode = ""
                        showToast = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تأكيد عملية البيع الفوري", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController, viewModel: MainViewModel) {
    val salesLogs by viewModel.salesLogs.collectAsState()

    var todaySales = 0.0
    var todayProfit = 0.0
    var allSales = 0.0
    var allProfit = 0.0

    val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val todayDate = format.format(java.util.Date())

    salesLogs.forEach { sale ->
        allSales += sale.price
        allProfit += sale.profit
        if (sale.date == todayDate) {
            todaySales += sale.price
            todayProfit += sale.profit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التقارير المالية والأرباح", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("إحصائيات اليوم", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReportMiniCard(title = "مبيعات اليوم:", value = String.format("%.2f ر.ي", todaySales), modifier = Modifier.weight(1f))
                        ReportMiniCard(title = "صافي الأرباح:", value = String.format("%.2f ر.ي", todayProfit), modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("التقارير الكلية الشاملة", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReportMiniCard(title = "إجمالي المبيعات:", value = String.format("%.2f ر.ي", allSales), modifier = Modifier.weight(1f))
                        ReportMiniCard(title = "إجمالي الأرباح:", value = String.format("%.2f ر.ي", allProfit), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMiniCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesLogsScreen(navController: NavController, viewModel: MainViewModel) {
    val salesLogs by viewModel.salesLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل المبيعات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (salesLogs.isEmpty()) {
                item {
                    Text("لا توجد عمليات مبيعات مسجلة.", modifier = Modifier.padding(16.dp))
                }
            }
            items(salesLogs) { sale ->
                ListItem(
                    headlineContent = { Text(sale.name, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("الوقت: ${sale.date} ${sale.time}") },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("السعر: ${sale.price}", fontWeight = FontWeight.Bold)
                            Text("الربح: +${String.format("%.2f", sale.profit)}", color = Color(0xFF0F5132), fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Divider()
            }
        }
    }
}
