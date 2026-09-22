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
import org.deltacv.myapplication.data.ProyectoData
import org.deltacv.myapplication.data.TareaCronogramaData
import org.deltacv.myapplication.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    proyecto: ProyectoData,
    currentUser: User,
    onBackClick: () -> Unit,
    onToggleVolunteer: () -> Unit,
    onEditProject: (ProyectoData) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    val isOwner = proyecto.creadorId == currentUser.id
    val isVolunteer = proyecto.participantesIds.contains(currentUser.id)

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
                        text = "Participantes registrados: ${proyecto.participantesIds.size}",
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
                ProjectField("Objetivo principal", proyecto.objetivoPrincipal)
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
                ProjectField("Descripción general", proyecto.descripcionGeneral)
                ProjectField("Recursos necesarios", proyecto.recursosNecesarios)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cronograma",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF555555)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ScheduleChart(tareas = proyecto.cronograma)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 4: Resultados y Contacto
            CollapsibleSection(
                title = "Resultados y Contacto",
                backgroundColor = Color(0xFFD5E8D4)
            ) {
                ProjectField("Resultados esperados", proyecto.resultadosEsperados)
                ProjectField("Responsables y formas de contacto", proyecto.responsables)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Voluntariado (para terceros)
            if (!isOwner) {
                Button(
                    onClick = onToggleVolunteer,
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
        var editedObjetivoPrincipal by remember { mutableStateOf(proyecto.objetivoPrincipal) }
        var editedAntecedentes by remember { mutableStateOf(proyecto.antecedentes) }
        var editedJustificacion by remember { mutableStateOf(proyecto.justificacion) }
        var editedObjetivosEspecificos by remember { mutableStateOf(proyecto.objetivosEspecificos) }
        var editedAlcance by remember { mutableStateOf(proyecto.alcance) }
        var editedDescripcionGeneral by remember { mutableStateOf(proyecto.descripcionGeneral) }
        var editedRecursosNecesarios by remember { mutableStateOf(proyecto.recursosNecesarios) }
        var editedResultadosEsperados by remember { mutableStateOf(proyecto.resultadosEsperados) }
        var editedResponsables by remember { mutableStateOf(proyecto.responsables) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Proyecto") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editedTitulo, onValueChange = { editedTitulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedOds, onValueChange = { editedOds = it }, label = { Text("ODS") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedObjetivoPrincipal, onValueChange = { editedObjetivoPrincipal = it }, label = { Text("Objetivo Principal") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedAntecedentes, onValueChange = { editedAntecedentes = it }, label = { Text("Antecedentes") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedJustificacion, onValueChange = { editedJustificacion = it }, label = { Text("Justificación") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedObjetivosEspecificos, onValueChange = { editedObjetivosEspecificos = it }, label = { Text("Objetivos Específicos") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedAlcance, onValueChange = { editedAlcance = it }, label = { Text("Alcance") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedDescripcionGeneral, onValueChange = { editedDescripcionGeneral = it }, label = { Text("Descripción General") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedRecursosNecesarios, onValueChange = { editedRecursosNecesarios = it }, label = { Text("Recursos Necesarios") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedResultadosEsperados, onValueChange = { editedResultadosEsperados = it }, label = { Text("Resultados Esperados") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedResponsables, onValueChange = { editedResponsables = it }, label = { Text("Responsables") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedProj = proyecto.copy(
                            titulo = editedTitulo,
                            ods = editedOds,
                            objetivoPrincipal = editedObjetivoPrincipal,
                            antecedentes = editedAntecedentes,
                            justificacion = editedJustificacion,
                            objetivosEspecificos = editedObjetivosEspecificos,
                            alcance = editedAlcance,
                            descripcionGeneral = editedDescripcionGeneral,
                            recursosNecesarios = editedRecursosNecesarios,
                            resultadosEsperados = editedResultadosEsperados,
                            responsables = editedResponsables
                        )
                        onEditProject(updatedProj)
                        showEditDialog = false
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
                        onDeleteProject(proyecto.id)
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
fun ScheduleChart(tareas: List<TareaCronogramaData>) {
    val maxMes = tareas.maxOfOrNull { it.mesInicio + it.duracion } ?: 3
    val meses = if (maxMes <= 2) listOf("Jul", "Ago") else listOf("Sep", "Oct", "Nov")
    val numColumnas = meses.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 100.dp)
        ) {
            meses.forEach { mes ->
                Text(
                    text = mes,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        tareas.forEach { tarea ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = tarea.nombre,
                    modifier = Modifier.width(100.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .weight(numColumnas.toFloat())
                        .height(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        repeat(numColumnas) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color.Gray.copy(alpha = 0.2f))
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        if (tarea.mesInicio > 0) {
                            Spacer(modifier = Modifier.weight(tarea.mesInicio.toFloat()))
                        }
                        Box(
                            modifier = Modifier
                                .weight(tarea.duracion.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        )
                        val restante = numColumnas - (tarea.mesInicio + tarea.duracion)
                        if (restante > 0) {
                            Spacer(modifier = Modifier.weight(restante.toFloat()))
                        }
                    }
                }
            }
        }
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
