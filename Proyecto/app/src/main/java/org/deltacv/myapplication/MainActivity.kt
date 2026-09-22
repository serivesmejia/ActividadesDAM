package org.deltacv.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.deltacv.myapplication.data.CloudRepository
import org.deltacv.myapplication.data.ProyectoData
import org.deltacv.myapplication.data.SessionManager
import org.deltacv.myapplication.data.User
import org.deltacv.myapplication.ui.ProfileScreen
import org.deltacv.myapplication.ui.ProjectDetailScreen
import org.deltacv.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val cloudRepository = CloudRepository(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentUser by remember { mutableStateOf<User?>(null) }
            var mostrarLogin by remember { mutableStateOf(true) }

            // Auto-login check on app startup
            LaunchedEffect(Unit) {
                if (sessionManager.hasActiveSession()) {
                    val savedUserId = sessionManager.getSavedUserId()
                    val savedUserOrEmail = sessionManager.getSavedUserOrEmail()
                    val savedCum = sessionManager.getSavedCum()
                    val savedPass = sessionManager.getSavedPass()

                    val users = cloudRepository.getUsers()
                    val matchedUser = users.find { user ->
                        user.id == savedUserId ||
                        ((user.correo == savedUserOrEmail || user.usuario == savedUserOrEmail) &&
                         user.cum == savedCum && user.contrasena == savedPass)
                    }

                    if (matchedUser != null) {
                        currentUser = matchedUser
                        mostrarLogin = false
                    }
                }
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                if (mostrarLogin || currentUser == null) {
                    LoginScreen(
                        cloudRepository = cloudRepository,
                        sessionManager = sessionManager,
                        onLoginSuccess = { user ->
                            currentUser = user
                            mostrarLogin = false
                        }
                    )
                } else {
                    MainScreen(
                        currentUser = currentUser!!,
                        cloudRepository = cloudRepository,
                        sessionManager = sessionManager,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                        onLogout = {
                            sessionManager.clearSession()
                            currentUser = null
                            mostrarLogin = true
                        },
                        onUserUpdated = { updatedUser ->
                            cloudRepository.saveUser(updatedUser)
                            if (updatedUser.id == currentUser?.id) {
                                currentUser = updatedUser
                            }
                        }
                    )
                }
            }
        }
    }
}

// PANTALLA DE LOGIN

@Composable
fun LoginScreen(
    cloudRepository: CloudRepository,
    sessionManager: SessionManager,
    onLoginSuccess: (User) -> Unit
) {
    var correoUsuario by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarRegistro by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (mostrarRegistro) {
        RegistroScreen(
            cloudRepository = cloudRepository,
            sessionManager = sessionManager,
            onBackClick = { mostrarRegistro = false },
            onRegistroSuccess = { newUser ->
                onLoginSuccess(newUser)
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // CORREO / NOMBRE DE USUARIO
                OutlinedTextField(
                    value = correoUsuario,
                    onValueChange = { correoUsuario = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo / nombre de usuario") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Usuario") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CUM
                OutlinedTextField(
                    value = cum,
                    onValueChange = { nuevoTexto ->
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            cum = nuevoTexto
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CUM") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CONTRASEÑA
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // BOTÓN INICIAR SESIÓN
                Button(
                    onClick = {
                        if (correoUsuario.isNotBlank() && cum.isNotBlank() && contrasena.isNotBlank()) {
                            val users = cloudRepository.getUsers()
                            val matched = users.find { user ->
                                (user.correo == correoUsuario || user.usuario == correoUsuario) &&
                                user.cum == cum && user.contrasena == contrasena
                            }

                            if (matched != null) {
                                sessionManager.saveSession(correoUsuario, cum, contrasena, matched.id)
                                onLoginSuccess(matched)
                            } else {
                                errorMessage = "Credenciales incorrectas o usuario no registrado."
                            }
                        } else {
                            errorMessage = "Por favor completa todos los campos."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // REGISTRO
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿No tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { mostrarRegistro = true }
                    )
                }
            }
        }
    }
}

// PANTALLA DE REGISTRO

@Composable
fun RegistroScreen(
    cloudRepository: CloudRepository,
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onRegistroSuccess: (User) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cum,
                    onValueChange = { nuevoTexto ->
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            cum = nuevoTexto
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CUM") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmación de contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (
                            nombre.isNotBlank() && usuario.isNotBlank() && correo.isNotBlank() &&
                            cum.isNotBlank() && contrasena.isNotBlank() && confirmarContrasena.isNotBlank()
                        ) {
                            if (contrasena != confirmarContrasena) {
                                errorMessage = "Las contraseñas no coinciden."
                            } else {
                                val newUser = User(
                                    nombre = nombre,
                                    usuario = usuario,
                                    correo = correo,
                                    cum = cum,
                                    contrasena = contrasena
                                )
                                cloudRepository.saveUser(newUser)
                                sessionManager.saveSession(usuario, cum, contrasena, newUser.id)
                                onRegistroSuccess(newUser)
                            }
                        } else {
                            errorMessage = "Por favor completa todos los campos."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Crear cuenta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿Ya tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "Inicia sesión",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onBackClick() }
                    )
                }
            }
        }
    }
}

// PANTALLA PRINCIPAL CON TOPBAR NAVEGADOR SUPERIOR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: User,
    cloudRepository: CloudRepository,
    sessionManager: SessionManager,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Inicio (Usuarios), 1: Proyectos
    var selectedUserForProfile by remember { mutableStateOf<User?>(null) }
    var selectedProjectForDetail by remember { mutableStateOf<ProyectoData?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Refresh states from cloud
    var usersList by remember { mutableStateOf(cloudRepository.getUsers()) }
    var projectsList by remember { mutableStateOf(cloudRepository.getProjects()) }

    fun refreshData() {
        usersList = cloudRepository.getUsers()
        projectsList = cloudRepository.getProjects()
    }

    // Sub-screeen: Detalle de perfil
    if (selectedUserForProfile != null) {
        val userToDisplay = selectedUserForProfile!!
        val userOwnProjects = projectsList.filter { it.creadorId == userToDisplay.id }
        val userVolunteerProjects = projectsList.filter { it.participantesIds.contains(userToDisplay.id) }

        ProfileScreen(
            user = userToDisplay,
            currentUser = currentUser,
            userProjects = userOwnProjects,
            volunteerProjects = userVolunteerProjects,
            onBackClick = { selectedUserForProfile = null },
            onEditProfile = { updatedUser ->
                onUserUpdated(updatedUser)
                refreshData()
                selectedUserForProfile = updatedUser
            },
            onCreateProject = { newProj ->
                cloudRepository.saveProject(newProj)
                refreshData()
            },
            onSelectProject = { proj ->
                selectedProjectForDetail = proj
            }
        )
        return
    }

    // Sub-screen: Detalle de proyecto
    if (selectedProjectForDetail != null) {
        val proj = selectedProjectForDetail!!
        ProjectDetailScreen(
            proyecto = proj,
            currentUser = currentUser,
            onBackClick = { selectedProjectForDetail = null },
            onToggleVolunteer = {
                cloudRepository.toggleVolunteer(proj.id, currentUser.id)
                refreshData()
                selectedProjectForDetail = cloudRepository.getProjects().find { it.id == proj.id }
            },
            onEditProject = { updatedProj ->
                cloudRepository.saveProject(updatedProj)
                refreshData()
                selectedProjectForDetail = updatedProj
            },
            onDeleteProject = { projId ->
                cloudRepository.deleteProject(projId)
                refreshData()
                selectedProjectForDetail = null
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RoverAcción", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    // 5a. Botón Modo Oscuro
                    IconButton(onClick = onToggleDarkTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Modo Oscuro",
                            tint = Color.White
                        )
                    }
                    // 5a. Botón para ver perfil personal
                    IconButton(onClick = { selectedUserForProfile = currentUser }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Mi Perfil",
                            tint = Color.White
                        )
                    }
                    // 5b. Botón para salir de la cuenta
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = primaryColor) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        refreshData()
                    },
                    icon = { Icon(Icons.Filled.Book, contentDescription = "Inicio") },
                    label = { Text("Inicio", color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        refreshData()
                    },
                    icon = { Icon(Icons.Filled.Campaign, contentDescription = "Proyectos") },
                    label = { Text("Proyectos", color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                // 4. PÁGINA DE INICIO (USUARIOS REGISTRADOS)
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "Usuarios Registrados",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                            )
                        }

                        items(usersList) { user ->
                            ProfileCard(
                                user = user,
                                nameColor = primaryColor,
                                onClick = {
                                    // 4b. Ver perfil de usuario (lectura si es tercero)
                                    selectedUserForProfile = user
                                }
                            )
                        }
                    }
                }

                // PROYECTOS REGISTRADOS
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "Lista de Proyectos",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                            )
                        }

                        items(projectsList) { project ->
                            ProjectCard(project = project) {
                                selectedProjectForDetail = project
                            }
                        }
                    }
                }
            }
        }
    }
}

// TARJETA DE PROYECTO

@Composable
fun ProjectCard(
    project: ProyectoData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = project.titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = project.ods,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Creado por: ${project.creadorNombre}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Objetivo: ${project.objetivoPrincipal}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voluntarios: ${project.participantesIds.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(text = "Ver detalles", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

// TARJETA DE PERSONA / USUARIO

fun String?.orSN(): String = if (this.isNullOrBlank()) "S/N" else this

@Composable
fun ProfileCard(
    user: User,
    nameColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.nombre.orSN(),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = user.cargo.orSN(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Usuario: @${user.usuario.orSN()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "CUM: ${user.cum.orSN()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                ContactItem(icon = Icons.Filled.Phone, text = user.telefono.orSN())
                ContactItem(icon = Icons.Filled.Email, text = user.correo.orSN())
            }
        }
    }
}

// CONTACTO ITEM

@Composable
fun ContactItem(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}
