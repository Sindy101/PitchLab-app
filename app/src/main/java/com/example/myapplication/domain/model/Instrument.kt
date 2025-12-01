package com.example.myapplication.domain.model

data class Instrument(
    val name: String,
    val type: InstrumentType,
    val strings: List<Note>
) {
    val stringCount: Int
        get() = strings.size
}

enum class InstrumentType {
    GUITAR, UKULELE, BALALAIKA, OTHER
}