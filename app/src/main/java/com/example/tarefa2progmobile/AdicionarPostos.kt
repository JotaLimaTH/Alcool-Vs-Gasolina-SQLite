package com.example.tarefa2progmobile

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // <- MUDANÇA 1: Importação nova

@Composable
fun AdicionarPosto(navController: NavController) { // <- MUDANÇA 2: Recebe NavController

    // Shared Preferences
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("MEUS_DADOS", Context.MODE_PRIVATE)

    var precoAlcool by rememberSaveable { mutableStateOf("") }
    var precoGasolina by rememberSaveable { mutableStateOf("") }
    var posto by rememberSaveable { mutableStateOf("") }
    var usarSetentaECinco by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("usar75", false)
        )
    }
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

                    resultado = "Posto adicionado! \n"
                    navController.popBackStack() // <- MUDANÇA 3: Usa o NavController para voltar
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
