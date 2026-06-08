package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val inventoryDao: InventoryDao, private val salesDao: SalesDao) {
    val allInventoryItems: Flow<List<InventoryItem>> = inventoryDao.getAllItems()
    val allSalesLogs: Flow<List<SalesLog>> = salesDao.getAllSales()

    fun searchInventoryItems(query: String): Flow<List<InventoryItem>> = inventoryDao.searchItems(query)

    suspend fun getInventoryItemByBarcode(barcode: String): InventoryItem? = inventoryDao.getItemByBarcode(barcode)

    suspend fun insertInventoryItem(item: InventoryItem) = inventoryDao.insertItem(item)

    suspend fun updateInventoryItem(item: InventoryItem) = inventoryDao.updateItem(item)

    suspend fun deleteInventoryItem(id: Int) = inventoryDao.deleteItemById(id)

    suspend fun insertSalesLog(sale: SalesLog) = salesDao.insertSale(sale)

    fun getSalesByDate(date: String): Flow<List<SalesLog>> = salesDao.getSalesByDate(date)

    fun getSalesSince(startTime: Long): Flow<List<SalesLog>> = salesDao.getSalesSince(startTime)
}
