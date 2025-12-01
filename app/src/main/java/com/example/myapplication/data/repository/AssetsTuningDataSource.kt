package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.domain.model.Instrument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetsTuningDataSource(private val context: Context) {

    private val gson = Gson()

    fun loadInstruments(): List<Instrument> {
        val jsonString = try {
            context.assets.open("instruments.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        val listType = object : TypeToken<List<Instrument>>() {}.type
        return gson.fromJson(jsonString, listType)
    }
}
