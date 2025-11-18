package com.example.tarefa2progmobile

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Posto(
    var nome: String,
    var alcool: String,
    var gasolina: String,
    var usar75: Boolean,
    val id: String = UUID.randomUUID().toString(),
    val dataRegistro: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
    var latitude: Double? = null,
    var longitude: Double? = null
)
