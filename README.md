# Álcool Vs. Gasolina
Este é um aplicativo criado em Kotlin a fim de facilitar a vida de quem está na dúvida se deve utilizar álcool ou gasolina para abastecer seu veículo. Anteriormente criado utilizando Shared Preferences e JSON como estrutura de dados, agora a nova versão utiliza o SQLite, que além de mais consoante com os padrões de aplicativo, envolve uma implementação mais simples e direta.
# Navegação
O aplicativo possui 3 telas: uma de adicionar postos de gasolina, uma com a lista dos postos adicionados e uma tela de edição. As telas de adição e edição são bastante semelhantes, consistindo em dois campos com os preços da gasolina e álcool, um checkbox para ativar o cálculo de 75%, um botão para chamar a localização do usuário, um botão de calcular e o botão de adicionar/editar.
Já a tela com a lista possui cards com os postos com seus dados, um botão para ir para a tela de edição e um botão para deletar.
# SQLite
A classe de dados básica do aplicativo é a `Posto`:
```Kotlin
data class Posto(
    var nome: String,
    var alcool: String,
    var gasolina: String,
    var usar75: Boolean,
    val id: Long = 0L,
    val dataRegistro: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
    var latitude: Double? = null,
    var longitude: Double? = null
)
```
No arquivo `PostoArmazenamento.kt`, temos as funções que irão tratar os dados em uma tabela. Na implementação anterior desse programa, foram utilizados os seguintes imports:
```Kotlin
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
```
Agora, esses:
```Kotlin
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues
import android.database.Cursor
```
Anteriormente, existiam as seguintes funções: `jsonParaPosto()`, `salvarPostoJSONEmLista()`. Agora, visto que JSONs não são mais utilizados, foram criadas novas funções. Por exemplo, a função a seguir é a que cria a tabela:
```Kotlin
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
```
Temos a função que converte as instâncias de `Posto` em `ContentValues`, que é o formato intermediário que vai ser convertido em uma query SQL:
```Kotlin
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
```
Bem como uma função que lida o `Cursor`, que é o formato intermediário que é convertida em uma instância de `Posto`:
```Kotlin
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
```
Falando sobre o CRUD, enquanto na implementação anterior havia implementações como essa:
```Kotlin
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
```
Na implementação com SQLite, as funções são menores, lidando com comando SQL.
```Kotlin
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
```
Menos linhas de código, e já implementamos 3 funções, usando os comandos `INSERT`, `DELETE` e `EDIT` do SQL.
Quanto ao `SELECT`, há duas funções: uma que pega todos os itens da tabela e uma que pega por ID.
```Kotlin
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
```
E em questão de implementação de banco, isso é tudo. Na interface, as únicas mudanças foram em relação às funções chamadas. Então, onde, por exemplo, havia isto:
```Kotlin
Button(
            onClick = {
                if (precoGasolina.isNotBlank() && precoAlcool.isNotBlank() && posto.isNotBlank()) {
                    val novoPosto = Posto(
                        nome = posto,
                        alcool = precoAlcool,
                        gasolina = precoGasolina,
                        usar75 = usarSetentaECinco,
                        latitude = userLatitude,
                        longitude = userLongitude
                    )

                    salvarPostoJSONEmLista(context, novoPosto)
...
```
Agora, há isto:
```Kotlin
Button(
            onClick = {
                if (precoGasolina.isNotBlank() && precoAlcool.isNotBlank() && posto.isNotBlank()) {
                    val novoPosto = Posto(
                        nome = posto,
                        ...
                    )

                    salvarPosto(dbHelper, novoPosto)
```
