package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.InventoryItem
import com.example.data.SalesLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val inventoryItems: StateFlow<List<InventoryItem>> = repository.allInventoryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salesLogs: StateFlow<List<SalesLog>> = repository.allSalesLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _posScannedItem = MutableStateFlow<InventoryItem?>(null)
    val posScannedItem: StateFlow<InventoryItem?> = _posScannedItem.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                val item = repository.getInventoryItemByBarcode(query)
                if (item != null) {
                    _posScannedItem.value = item
                }
            }
        }
    }

    fun setPosScannedItem(item: InventoryItem?) {
        _posScannedItem.value = item
    }

    fun addOrUpdateInventoryItem(
        barcode: String,
        name: String,
        cost: Double,
        price: Double,
        qty: Int
    ) {
        viewModelScope.launch {
            val existing = repository.getInventoryItemByBarcode(barcode)
            if (existing != null) {
                repository.updateInventoryItem(
                    existing.copy(
                        name = name,
                        cost = cost,
                        price = price,
                        qty = existing.qty + qty
                    )
                )
            } else {
                repository.insertInventoryItem(
                    InventoryItem(
                        barcode = barcode,
                        name = name,
                        cost = cost,
                        price = price,
                        qty = qty
                    )
                )
            }
        }
    }

    fun searchItemByBarcode(barcode: String, onResult: (InventoryItem?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getInventoryItemByBarcode(barcode))
        }
    }

    fun processSale(item: InventoryItem) {
        viewModelScope.launch {
            if (item.qty > 0) {
                val updatedItem = item.copy(qty = item.qty - 1)
                repository.updateInventoryItem(updatedItem)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val now = Date()

                val profit = item.price - item.cost
                val sale = SalesLog(
                    name = item.name,
                    price = item.price,
                    profit = profit,
                    timestamp = now.time,
                    date = dateFormat.format(now),
                    time = timeFormat.format(now)
                )

                repository.insertSalesLog(sale)
                _posScannedItem.value = null // clear after sale
            }
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
