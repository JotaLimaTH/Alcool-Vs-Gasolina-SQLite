package com.example.tarefa2progmobile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
/*import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID*/
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues
import android.database.Cursor

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE postos(
                id TEXT PRIMARY KEY,
                nome TEXT NOT NULL,
                alcool TEXT NOT NULL,
                gasolina TEXT NOT NULL,
                usar75 INTEGER NOT NULL,
                data_registro TEXT NOT NULL,
                latitude REAL,
                longitude REAL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS postos")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "app_database.db"
        const val DATABASE_VERSION = 1
    }
}

fun postoParaContentValues(posto: Posto): ContentValues {
    return ContentValues().apply {
        put("id", posto.id)
        put("nome", posto.nome)
        put("alcool", posto.alcool)
        put("gasolina", posto.gasolina)
        put("usar75", if (posto.usar75) 1 else 0)
        put("data_registro", posto.dataRegistro)
        put("latitude", posto.latitude)
        put("longitude", posto.longitude)
    }
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

fun deletarPosto(context: Context, index: Int){
    val sharedFileName="POSTOS"
    val sp: SharedPreferences = context.getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
    val jsonListStr = sp.getString("lista", "[]")
    val jsonArray = JSONArray(jsonListStr)

    val novoArray = JSONArray()
    for (i in 0 until jsonArray.length()) {
        if (i != index) {
            novoArray.put(jsonArray.getJSONObject(i))
        }
    }

    sp.edit()
        .putString("lista", novoArray.toString())
        .apply()
}

fun editarPosto(context: Context, index: Int, posto: Posto) {
    val sharedFileName="POSTOS"
    var sp: SharedPreferences = context.getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
    var editor = sp.edit()

    val jsonListStr = sp.getString("lista", "[]")
    val jsonArray = JSONArray(jsonListStr)

    val novoArray = JSONArray()
    for (i in 0 until jsonArray.length()) {
        if (i != index) {
            novoArray.put(jsonArray.getJSONObject(i))
        } else {
            novoArray.put(postoParaJSON(posto))
        }
    }

    editor.putString("lista",novoArray.toString())
    editor.apply()
}

fun carregarListaPosto(context: Context): MutableList<Posto> {
    val sp = context.getSharedPreferences("POSTOS", Context.MODE_PRIVATE)
    val jsonListStr = sp.getString("lista", "[]")
    val jsonArray = JSONArray(jsonListStr)

    val lista = mutableListOf<Posto>()
    for (i in 0 until jsonArray.length()){
        val obj = jsonArray.getJSONObject(i)
        lista.add(jsonParaPosto(obj))
    }
    return lista
}