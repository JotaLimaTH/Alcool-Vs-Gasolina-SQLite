package com.example.tarefa2progmobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tarefa2progmobile.ui.theme.Tarefa2ProgMobileTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tarefa2ProgMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "lista"){
        composable("lista"){
            ListaPostos(
                irParaAdicionarPosto = { nav.navigate("adicionarPosto") }
            )
        }

        composable("adicionarPosto"){
            AlcoolOuGasolinaApp(
                voltar = { nav.popBackStack() }
            )
        }
    }
}

@Composable
fun ListaPostos(irParaAdicionarPosto: () -> Unit) {
    val context = LocalContext.current
    var lista by remember { mutableStateOf(carregarListaPosto(context)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        lista.forEachIndexed {index, posto ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("${posto.nome}")
                Button(
                    onClick = {
                        deletarPosto(context, index)
                        lista = carregarListaPosto(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Deletar")
                }
            }
        }
        Button(
            onClick = irParaAdicionarPosto
        ) {
            Text("Adicionar novo posto")
        }
    }
}

@Composable
fun AlcoolOuGasolinaApp(voltar: () -> Unit) {

    // Shared Preferences

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("MEUS_DADOS", Context.MODE_PRIVATE)

    var precoAlcool by rememberSaveable { mutableStateOf("") }
    var precoGasolina by rememberSaveable { mutableStateOf("") }
    var posto by rememberSaveable { mutableStateOf("") }
    var usarSetentaECinco by rememberSaveable { mutableStateOf(
        prefs.getBoolean("usar75", false)
    ) }
    var resultado by remember { mutableStateOf("") }

    val percentual = if (usarSetentaECinco) 0.75 else 0.70

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = precoAlcool,
            onValueChange = { precoAlcool = it },
            label = { Text("Preço do Álcool (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = precoGasolina,
            onValueChange = { precoGasolina = it },
            label = { Text("Preço da Gasolina (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = posto,
            onValueChange = { posto = it },
            label = { Text("Nome do Posto (Opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${if (usarSetentaECinco) "75%" else "70%"}")
            Switch(
                checked = usarSetentaECinco,
                // onCheckedChange = { usarSetentaECinco = it }
                onCheckedChange = { novoValor ->
                    usarSetentaECinco = novoValor
                    prefs.edit().putBoolean("usar75", novoValor).apply()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val alcool = precoAlcool.toDoubleOrNull()
                val gasolina = precoGasolina.toDoubleOrNull()

                if (alcool != null && gasolina != null && gasolina > 0) {
                    val limite = gasolina * percentual
                    resultado = if (alcool <= limite) {
                        "Melhor usar Álcool"
                    } else {
                        "Melhor usar Gasolina"
                    }
                } else {
                    resultado = "Preencha os valores corretamente!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resultado,
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = {
                if (precoGasolina.isNotBlank() && precoAlcool.isNotBlank() && posto.isNotBlank()) {
                    val novoPosto = Posto(
                        nome = posto,
                        alcool = precoAlcool,
                        gasolina = precoGasolina,
                        usar75 = usarSetentaECinco
                    )

                    salvarPostoJSONEmLista(context, novoPosto)
                    /*val sp: SharedPreferences = context.getSharedPreferences("POSTOS", Context.MODE_PRIVATE)
                    val json = sp.getString("lista", "[]") ?: "[]"
                    resultado = json*/ // Para saber se criou mesmo a lista
                    val lista  = carregarListaPosto(context)

                    resultado = "Posto adicionado! \n" + lista.joinToString()
                    voltar()
                } else {
                    resultado = "Preencha todos os campos necessários!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar posto")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    Tarefa2ProgMobileTheme {
        AlcoolOuGasolinaApp({})
    }
}
