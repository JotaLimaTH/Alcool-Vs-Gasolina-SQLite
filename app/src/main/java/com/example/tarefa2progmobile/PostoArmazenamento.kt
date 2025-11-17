package com.example.tarefa2progmobile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

data class Posto(
    val nome: String,
    val alcool: String,
    val gasolina: String,
    val usar75: Boolean
)

fun postoParaJSON(p: Posto): JSONObject {
    val json = JSONObject()
    json.put("nome", p.nome)
    json.put("alcool", p.alcool)
    json.put("gasolina", p.gasolina)
    json.put("usar75", p.usar75)
    return json
}

fun jsonParaPosto(json: JSONObject): Posto{
    return Posto(
        nome = json.getString("nome"),
        alcool = json.getString("alcool"),
        gasolina = json.getString("gasolina"),
        usar75 = json.getBoolean("usar75")
    )
}

fun salvarPostoJSONEmLista(context: Context, posto: Posto){
    Log.v("PDM25","Salvando o posto em JSON")
    val sharedFileName="POSTOS"
    var sp: SharedPreferences = context.getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
    var editor = sp.edit()

    val jsonListStr = sp.getString("lista", "[]")
    val jsonArray = JSONArray(jsonListStr)

    jsonArray.put(postoParaJSON(posto))

    editor.putString("lista",jsonArray.toString())
    editor.apply()
}