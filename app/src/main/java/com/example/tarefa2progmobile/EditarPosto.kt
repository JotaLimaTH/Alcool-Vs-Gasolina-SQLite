package com.example.tarefa2progmobile

import android.content.Context
import android.provider.Settings.Global.getString
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
import androidx.navigation.NavController

@Composable
fun EditarPosto(index: Int, navController: NavController) {

    val context = LocalContext.current
    val lista = carregarListaPosto(context)

    val postoOriginal = lista[index]

    var precoAlcool by rememberSaveable { mutableStateOf(postoOriginal.alcool) }
    var precoGasolina by rememberSaveable { mutableStateOf(postoOriginal.gasolina) }
    var posto by rememberSaveable { mutableStateOf(postoOriginal.nome) }
    var usarSetentaECinco by rememberSaveable { mutableStateOf(postoOriginal.usar75 ) }

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
            label = { Text(context.getString(R.string.preco_alcool)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = precoGasolina,
            onValueChange = { precoGasolina = it },
            label = { Text(context.getString(R.string.preco_gasolina)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = posto,
            onValueChange = { posto = it },
            label = { Text(context.getString(R.string.nome_posto)) },
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
                        context.getString(R.string.melhor_alcool)
                    } else {
                        context.getString(R.string.melhor_gasolina)
                    }
                } else {
                    resultado = context.getString(R.string.erro_preenchimento)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(context.getString(R.string.calcular))
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
                        usar75 = usarSetentaECinco,
                        id = postoOriginal.id,
                        dataRegistro = postoOriginal.dataRegistro,
                        latitude = postoOriginal.latitude,
                        longitude = postoOriginal.longitude
                    )

                    editarPosto(context, index, novoPosto)

                    /*val sp: SharedPreferences = context.getSharedPreferences("POSTOS", Context.MODE_PRIVATE)
                    val json = sp.getString("lista", "[]") ?: "[]"
                    resultado = json*/ // Para saber se criou mesmo a lista

                    resultado = "${context.getString(R.string.posto_modificado)}! \n"
                    navController.popBackStack()
                } else {
                    resultado = context.getString(R.string.erro_preenchimento)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar posto")
        }
    }
}
