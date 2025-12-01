package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Instrument

class InstrumentRepository(
    private val assetsDataSource: AssetsTuningDataSource
) {

    fun getInstruments(): List<Instrument> {
        return assetsDataSource.loadInstruments()
    }

    fun getInstrumentByName(name: String): Instrument? {
        return getInstruments().find { it.name == name }
    }
}
