package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barcode: String,
    val name: String,
    val cost: Double,
    val price: Double,
    val qty: Int
)

@Entity(tableName = "sales_logs")
data class SalesLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val profit: Double,
    val timestamp: Long,
    val date: String,
    val time: String
)
