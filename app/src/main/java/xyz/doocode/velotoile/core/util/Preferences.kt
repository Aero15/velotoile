package xyz.doocode.velotoile.core.util

import SortField
import SortOrder
import android.content.Context
import android.content.SharedPreferences

class Preferences(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "velotoile_prefs"
        private const val SORT_FIELD_KEY = "sort_field"
        private const val SORT_ORDER_KEY = "sort_order"
    }
    
    fun getSortField(): SortField {
        val savedValue = sharedPreferences.getString(SORT_FIELD_KEY, SortField.NUMBER.name)
        return try {
            SortField.valueOf(savedValue ?: SortField.NUMBER.name)
        } catch (e: IllegalArgumentException) {
            SortField.NUMBER
        }
    }
    
    fun setSortField(field: SortField) {
        sharedPreferences.edit().apply {
            putString(SORT_FIELD_KEY, field.name)
            apply()
        }
    }
    
    fun getSortOrder(): SortOrder {
        val savedValue = sharedPreferences.getString(SORT_ORDER_KEY, SortOrder.ASCENDING.name)
        return try {
            SortOrder.valueOf(savedValue ?: SortOrder.ASCENDING.name)
        } catch (e: IllegalArgumentException) {
            SortOrder.ASCENDING
        }
    }
    
    fun setSortOrder(order: SortOrder) {
        sharedPreferences.edit().apply {
            putString(SORT_ORDER_KEY, order.name)
            apply()
        }
    }
    
    fun saveSortPreferences(field: SortField, order: SortOrder) {
        sharedPreferences.edit().apply {
            putString(SORT_FIELD_KEY, field.name)
            putString(SORT_ORDER_KEY, order.name)
            apply()
        }
    }
}
