package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): InventoryItem?

    @Query("SELECT * FROM inventory_items WHERE barcode LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)
}

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales_logs ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SalesLog>>

    @Query("SELECT * FROM sales_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getSalesByDate(date: String): Flow<List<SalesLog>>

    @Query("SELECT * FROM sales_logs WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getSalesSince(startTime: Long): Flow<List<SalesLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SalesLog)
}
