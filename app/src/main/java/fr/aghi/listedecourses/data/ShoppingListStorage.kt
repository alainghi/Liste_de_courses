package fr.aghi.listedecourses.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.aghi.listedecourses.model.ShoppingItem

class ShoppingListStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "shopping_list_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    fun saveShoppingList(items: List<ShoppingItem>) {
        val jsonString = gson.toJson(items)
        sharedPreferences.edit().putString(KEY_SHOPPING_LIST, jsonString).apply()
    }

    fun loadShoppingList(): List<ShoppingItem> {
        val jsonString = sharedPreferences.getString(KEY_SHOPPING_LIST, "[]")
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        return gson.fromJson(jsonString, type) ?: emptyList()
    }

    companion object {
        private const val KEY_SHOPPING_LIST = "shopping_list"
    }
} 