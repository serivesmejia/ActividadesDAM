package org.deltacv.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
fun ProfileScreen(
    user: Usuario,
    currentUser: Usuario,
    onBackClick: () -> Unit,
    onSelectProject: (Proyecto) -> Unit
) {
    val isOwnProfile = user.uid == currentUser.uid
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Proyectos propios, 1: Voluntariado
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }

    // Consumo de Flow en tiempo real desde FirestoreManager
    val userProjects by FirestoreManager.obtenerProyectosPorResponsable(user.uid).collectAsState(initial = emptyList())
    val volunteerProjects by FirestoreManager.obtenerProyectosPorVoluntario(user.uid).collectAsState(initial = emptyList())

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "Mi Perfil" else "Perfil de ${user.nombreCompleto}", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
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
        ) {
            // ENCABEZADO DE PERFIL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Foto del lado izquierdo
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Columna de datos del lado derecho
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = user.nombreCompleto.ifBlank { "S/N" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@${user.usuario.ifBlank { "S/N" }}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = user.correo.ifBlank { "S/N" }, fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = user.telefono?.ifBlank { "S/N" } ?: "S/N", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    // Botones de Edición y Botón + (Crear proyecto)
                    if (isOwnProfile) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { showEditProfileDialog = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar perfil", tint = primaryColor)
                            }
                            IconButton(onClick = { showCreateProjectDialog = true }) {
                                Icon(Icons.Filled.Add, contentDescription = "Crear proyecto", tint = primaryColor)
                            }
                        }
                    }
                }
            }

            // BARRA DE NAVEGACIÓN INTERNA
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Proyectos propios (${userProjects.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Voluntariado (${volunteerProjects.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LISTA DE PROYECTOS SEGÚN LA PESTAÑA SELECCIONADA
            val projectsToShow = if (selectedTab == 0) userProjects else volunteerProjects

            if (projectsToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "No hay proyectos propios publicados" else "No se ha registrado en ningún voluntariado",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(projectsToShow) { project ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onSelectProject(project) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = project.titulo,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.ods,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Voluntarios: ${project.voluntariosIds.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA EDITAR PERFIL
    if (showEditProfileDialog) {
        var editedNombre by remember { mutableStateOf(user.nombreCompleto) }
        var editedUsuario by remember { mutableStateOf(user.usuario) }
        var editedCorreo by remember { mutableStateOf(user.correo) }
        var editedTelefono by remember { mutableStateOf(user.telefono ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Editar Perfil Personal") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = editedNombre,
                        onValueChange = { editedNombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedUsuario,
                        onValueChange = { editedUsuario = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedCorreo,
                        onValueChange = { editedCorreo = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedTelefono,
                        onValueChange = { editedTelefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedNombre.isNotBlank() && editedUsuario.isNotBlank() && editedCorreo.isNotBlank()) {
                            val updatedUser = user.copy(
                                nombreCompleto = editedNombre,
                                usuario = editedUsuario,
                                correo = editedCorreo,
                                telefono = editedTelefono
                            )
                            FirestoreManager.registrarUsuario(updatedUser, onSuccess = {}, onError = {})
                            showEditProfileDialog = false
                        }
                    }
                ) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO PARA CREAR PROYECTO
    if (showCreateProjectDialog) {
        var titulo by remember { mutableStateOf("") }
        var ods by remember { mutableStateOf("") }
        var objetivoGeneral by remember { mutableStateOf("") }
        var antecedentes by remember { mutableStateOf("") }
        var justificacion by remember { mutableStateOf("") }
        var objetivosEspecificos by remember { mutableStateOf("") }
        var alcance by remember { mutableStateOf("") }
        var descripcionGen by remember { mutableStateOf("") }
        var recursos by remember { mutableStateOf("") }
        var resultados by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCreateProjectDialog = false },
            title = { Text("Crear Nuevo Proyecto") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título del proyecto *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = ods, onValueChange = { ods = it }, label = { Text("ODS *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = objetivoGeneral, onValueChange = { objetivoGeneral = it }, label = { Text("Objetivo General *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = antecedentes, onValueChange = { antecedentes = it }, label = { Text("Antecedentes *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = justificacion, onValueChange = { justificacion = it }, label = { Text("Justificación *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = objetivosEspecificos, onValueChange = { objetivosEspecificos = it }, label = { Text("Objetivos Específicos *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = alcance, onValueChange = { alcance = it }, label = { Text("Alcance *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = descripcionGen, onValueChange = { descripcionGen = it }, label = { Text("Descripción General *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = recursos, onValueChange = { recursos = it }, label = { Text("Recursos Necesarios *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = resultados, onValueChange = { resultados = it }, label = { Text("Resultados Esperados *") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (
                            titulo.isBlank() || ods.isBlank() || objetivoGeneral.isBlank() ||
                            antecedentes.isBlank() || justificacion.isBlank() || objetivosEspecificos.isBlank() ||
                            alcance.isBlank() || descripcionGen.isBlank() || recursos.isBlank() ||
                            resultados.isBlank()
                        ) {
                            errorMessage = "Por favor completa todos los campos requeridos."
                        } else {
                            val newProject = Proyecto(
                                titulo = titulo,
                                ods = ods,
                                objetivoGeneral = objetivoGeneral,
                                antecedentes = antecedentes,
                                justificacion = justificacion,
                                objetivosEspecificos = objetivosEspecificos,
                                alcance = alcance,
                                descripcionGen = descripcionGen,
                                recursos = recursos,
                                resultados = resultados,
                                responsablesIds = listOf(user.uid),
                                voluntariosIds = emptyList()
                            )
                            FirestoreManager.crearProyecto(
                                proyecto = newProject,
                                responsableUid = user.uid,
                                onSuccess = { showCreateProjectDialog = false },
                                onError = { errorMessage = "Error al crear el proyecto: ${it.localizedMessage}" }
                            )
                        }
                    }
                ) {
                    Text("Publicar proyecto")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProjectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
