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
                id INTEGER PRIMARY KEY AUTOINCREMENT,
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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
        put("nome", posto.nome)
        put("alcool", posto.alcool)
        put("gasolina", posto.gasolina)
        put("usar75", if (posto.usar75) 1 else 0)
        put("data_registro", posto.dataRegistro)
        put("latitude", posto.latitude)
        put("longitude", posto.longitude)
    }
}

fun cursorParaPosto(cursor: Cursor): Posto {
    return Posto(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
        alcool = cursor.getString(cursor.getColumnIndexOrThrow("alcool")),
        gasolina = cursor.getString(cursor.getColumnIndexOrThrow("gasolina")),
        usar75 = cursor.getInt(cursor.getColumnIndexOrThrow("usar75")) == 1,
        dataRegistro = cursor.getString(cursor.getColumnIndexOrThrow("data_registro")),
        latitude = if (!cursor.isNull(cursor.getColumnIndexOrThrow("latitude")))
            cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")) else null,
        longitude = if (!cursor.isNull(cursor.getColumnIndexOrThrow("longitude")))
            cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")) else null,
    )
}

fun salvarPosto(dbHelper: AppDatabaseHelper, posto: Posto): Long {
    val db = dbHelper.writableDatabase
    return db.insert("postos", null, postoParaContentValues(posto))
}

fun deletarPosto(dbHelper: AppDatabaseHelper, id: Long){
    val db = dbHelper.writableDatabase
    db.delete("postos", "id = ?", arrayOf(id.toString()))
}

fun editarPosto(dbHelper: AppDatabaseHelper, posto: Posto) {
    val db = dbHelper.writableDatabase
    db.update("postos", postoParaContentValues(posto), "id = ?", arrayOf(posto.id.toString()))
}

fun carregarListaPosto(dbHelper: AppDatabaseHelper): MutableList<Posto> {
    val db = dbHelper.readableDatabase
    val cursor = db.query(
        "postos",
        null,
        null,
        null,
        null,
        null,
        "data_registro DESC"
    )

    val lista = mutableListOf<Posto>()
    while (cursor.moveToNext()){
        lista.add(cursorParaPosto(cursor))
    }
    cursor.close()
    return lista
}

fun buscarPostoPorId(dbHelper: AppDatabaseHelper, id: Long): Posto? {
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        "postos",
        null,
        "id = ?",
        arrayOf(id.toString()),
        null,
        null,
        null,
    )

    val posto = if (cursor.moveToFirst()){
        cursorParaPosto(cursor)
    } else {
        null
    }

    cursor.close()
    return posto
}