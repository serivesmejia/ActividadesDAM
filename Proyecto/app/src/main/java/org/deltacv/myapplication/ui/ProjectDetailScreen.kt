package org.deltacv.myapplication.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.deltacv.myapplication.data.FirestoreManager
import org.deltacv.myapplication.data.Proyecto
import org.deltacv.myapplication.data.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    proyecto: Proyecto,
    currentUser: Usuario,
    onBackClick: () -> Unit
) {
    val isOwner = proyecto.responsablesIds.contains(currentUser.uid)
    val isVolunteer = proyecto.voluntariosIds.contains(currentUser.uid)

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de proyecto", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar proyecto", tint = Color.White)
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar proyecto", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = proyecto.titulo,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.weight(1f)
                )

                if (isOwner) {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = primaryColor)
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Participantes / Voluntarios
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.People, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voluntarios registrados: ${proyecto.voluntariosIds.size}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 1: ODS y Objetivos
            CollapsibleSection(
                title = "ODS y Objetivos",
                backgroundColor = Color(0xFFD1E8E2)
            ) {
                ProjectField("ODS que está trabajando", proyecto.ods)
                ProjectField("Objetivo principal", proyecto.objetivoGeneral)
                ProjectField("Objetivos específicos", proyecto.objetivosEspecificos)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 2: Contexto
            CollapsibleSection(
                title = "Contexto",
                backgroundColor = Color(0xFFFFF2CC)
            ) {
                ProjectField("Antecedentes", proyecto.antecedentes)
                ProjectField("Justificación", proyecto.justificacion)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 3: Planificación
            CollapsibleSection(
                title = "Planificación",
                backgroundColor = Color(0xFFE1D5E7)
            ) {
                ProjectField("Alcance", proyecto.alcance)
                ProjectField("Descripción general", proyecto.descripcionGen)
                ProjectField("Recursos necesarios", proyecto.recursos)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 4: Resultados
            CollapsibleSection(
                title = "Resultados",
                backgroundColor = Color(0xFFD5E8D4)
            ) {
                ProjectField("Resultados esperados", proyecto.resultados)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Voluntariado (para usuarios no creadores)
            if (!isOwner) {
                Button(
                    onClick = {
                        FirestoreManager.agregarVoluntarioAProyecto(proyecto.id, currentUser.uid)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVolunteer) MaterialTheme.colorScheme.error else primaryColor
                    )
                ) {
                    Text(
                        text = if (isVolunteer) "Salir del voluntariado" else "Unirse como voluntario",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Regresar a la lista de proyectos", color = Color.White)
            }
        }
    }

    // DIÁLOGO PARA EDITAR PROYECTO
    if (showEditDialog) {
        var editedTitulo by remember { mutableStateOf(proyecto.titulo) }
        var editedOds by remember { mutableStateOf(proyecto.ods) }
        var editedObjetivoGeneral by remember { mutableStateOf(proyecto.objetivoGeneral) }
        var editedAntecedentes by remember { mutableStateOf(proyecto.antecedentes) }
        var editedJustificacion by remember { mutableStateOf(proyecto.justificacion) }
        var editedObjetivosEspecificos by remember { mutableStateOf(proyecto.objetivosEspecificos) }
        var editedAlcance by remember { mutableStateOf(proyecto.alcance) }
        var editedDescripcionGen by remember { mutableStateOf(proyecto.descripcionGen) }
        var editedRecursos by remember { mutableStateOf(proyecto.recursos) }
        var editedResultados by remember { mutableStateOf(proyecto.resultados) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Proyecto") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editedTitulo, onValueChange = { editedTitulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedOds, onValueChange = { editedOds = it }, label = { Text("ODS") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedObjetivoGeneral, onValueChange = { editedObjetivoGeneral = it }, label = { Text("Objetivo General") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedAntecedentes, onValueChange = { editedAntecedentes = it }, label = { Text("Antecedentes") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedJustificacion, onValueChange = { editedJustificacion = it }, label = { Text("Justificación") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedObjetivosEspecificos, onValueChange = { editedObjetivosEspecificos = it }, label = { Text("Objetivos Específicos") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedAlcance, onValueChange = { editedAlcance = it }, label = { Text("Alcance") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedDescripcionGen, onValueChange = { editedDescripcionGen = it }, label = { Text("Descripción General") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedRecursos, onValueChange = { editedRecursos = it }, label = { Text("Recursos Necesarios") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedResultados, onValueChange = { editedResultados = it }, label = { Text("Resultados Esperados") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mapUpdates = mapOf<String, Any>(
                            "titulo" to editedTitulo,
                            "ods" to editedOds,
                            "objetivoGeneral" to editedObjetivoGeneral,
                            "antecedentes" to editedAntecedentes,
                            "justificacion" to editedJustificacion,
                            "objetivosEspecificos" to editedObjetivosEspecificos,
                            "alcance" to editedAlcance,
                            "descripcionGen" to editedDescripcionGen,
                            "recursos" to editedRecursos,
                            "resultados" to editedResultados
                        )
                        FirestoreManager.editarProyecto(proyecto.id, mapUpdates, onSuccess = { showEditDialog = false }, onError = {})
                    }
                ) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO DE VERIFICACIÓN PARA BORRAR PROYECTO
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este proyecto? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        FirestoreManager.eliminarProyecto(proyecto.id, onSuccess = { onBackClick() }, onError = {})
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = Color.DarkGray
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun ProjectField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF555555)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}
