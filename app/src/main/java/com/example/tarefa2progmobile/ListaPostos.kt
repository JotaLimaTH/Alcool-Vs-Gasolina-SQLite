package com.example.tarefa2progmobile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPostos(navController: NavController) {
    val context = LocalContext.current
    var lista by remember { mutableStateOf(carregarListaPosto(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postos de Combustível") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("adicionarPosto")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Posto")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum posto cadastrado.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(lista) { index, posto ->
                        PostoCard(
                            posto = posto,
                            onDelete = {
                                deletarPosto(context, index)
                                lista = carregarListaPosto(context) // Recarrega a lista para a UI
                            },
                            onEdit = {
                                navController.navigate("editarPosto/$index")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostoCard(posto: Posto, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = posto.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Gasolina: R$ ${posto.gasolina}")
            Text(text = "Álcool: R$ ${posto.alcool}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Registrado em: ${posto.dataRegistro}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Text("EDITAR")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("DELETAR")
                }
            }
        }
    }
}
