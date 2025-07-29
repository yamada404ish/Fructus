package com.example.fructus.util

import android.content.Context
import com.example.fructus.data.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Context.loadRecipesFromJson(): List<Recipe> {
    val json = assets.open("recipes.json").bufferedReader().use { it.readText() }
    return Gson().fromJson(json, object : TypeToken<List<Recipe>>() {}.type)
}

fun Context.getDrawableIdByName(name: String): Int {
    return resources.getIdentifier(name, "drawable", packageName)
}
