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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
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
import org.deltacv.myapplication.data.FirestoreManager
import org.deltacv.myapplication.data.Proyecto
import org.deltacv.myapplication.data.SessionManager
import org.deltacv.myapplication.data.Usuario
import org.deltacv.myapplication.ui.ProfileScreen
import org.deltacv.myapplication.ui.ProjectDetailScreen
import org.deltacv.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentUserId by remember { mutableStateOf<String?>(null) }
            var mostrarLogin by remember { mutableStateOf(true) }

            // Escuchar el estado del usuario activo en tiempo real mediante Flow
            val currentUserState by FirestoreManager.obtenerUsuarioPorUid(currentUserId ?: "").collectAsState(initial = null)

            // Auto-login automático si existen credenciales guardadas en el dispositivo
            LaunchedEffect(Unit) {
                if (sessionManager.hasActiveSession()) {
                    val savedUserId = sessionManager.getSavedUserId()
                    val savedUserOrEmail = sessionManager.getSavedUserOrEmail()
                    val savedCum = sessionManager.getSavedCum()
                    val savedPass = sessionManager.getSavedPass()

                    if (!savedUserId.isNullOrBlank()) {
                        currentUserId = savedUserId
                        mostrarLogin = false
                    } else if (!savedUserOrEmail.isNullOrBlank() && !savedCum.isNullOrBlank() && !savedPass.isNullOrBlank()) {
                        FirestoreManager.buscarUsuarioParaLogin(
                            identificador = savedUserOrEmail,
                            cum = savedCum,
                            contrasena = savedPass,
                            onSuccess = { user ->
                                if (user != null) {
                                    currentUserId = user.uid
                                    sessionManager.saveSession(savedUserOrEmail, savedCum, savedPass, user.uid)
                                    mostrarLogin = false
                                } else {
                                    sessionManager.clearSession()
                                }
                            },
                            onError = {
                                sessionManager.clearSession()
                            }
                        )
                    }
                }
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                if (mostrarLogin || currentUserState == null) {
                    LoginScreen(
                        sessionManager = sessionManager,
                        onLoginSuccess = { user ->
                            currentUserId = user.uid
                            mostrarLogin = false
                        }
                    )
                } else {
                    MainScreen(
                        currentUser = currentUserState!!,
                        sessionManager = sessionManager,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                        onLogout = {
                            sessionManager.clearSession()
                            currentUserId = null
                            mostrarLogin = true
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
    sessionManager: SessionManager,
    onLoginSuccess: (Usuario) -> Unit
) {
    var correoUsuario by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarRegistro by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    if (mostrarRegistro) {
        RegistroScreen(
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

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            if (correoUsuario.isNotBlank() && cum.isNotBlank() && contrasena.isNotBlank()) {
                                isLoading = true
                                errorMessage = null
                                FirestoreManager.buscarUsuarioParaLogin(
                                    identificador = correoUsuario,
                                    cum = cum,
                                    contrasena = contrasena,
                                    onSuccess = { matched ->
                                        isLoading = false
                                        if (matched != null) {
                                            sessionManager.saveSession(correoUsuario, cum, contrasena, matched.uid)
                                            onLoginSuccess(matched)
                                        } else {
                                            errorMessage = "Credenciales incorrectas o usuario no registrado."
                                        }
                                    },
                                    onError = { e ->
                                        isLoading = false
                                        errorMessage = "Error de conexión: ${e.localizedMessage}"
                                    }
                                )
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
                }

                Spacer(modifier = Modifier.height(16.dp))

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
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onRegistroSuccess: (Usuario) -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorUsuarioMsg by remember { mutableStateOf<String?>(null) }
    var errorCorreoMsg by remember { mutableStateOf<String?>(null) }
    var errorCumMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                    onValueChange = {
                        usuario = it
                        errorUsuarioMsg = null
                    },
                    isError = errorUsuarioMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                errorUsuarioMsg?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        errorCorreoMsg = null
                    },
                    isError = errorCorreoMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                errorCorreoMsg?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cum,
                    onValueChange = { nuevoTexto ->
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            cum = nuevoTexto
                            errorCumMsg = null
                        }
                    },
                    isError = errorCumMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CUM") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                errorCumMsg?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 2.dp)
                    )
                }

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

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            errorUsuarioMsg = null
                            errorCorreoMsg = null
                            errorCumMsg = null
                            errorMessage = null

                            if (
                                nombre.isNotBlank() && usuario.isNotBlank() && correo.isNotBlank() &&
                                cum.isNotBlank() && contrasena.isNotBlank() && confirmarContrasena.isNotBlank()
                            ) {
                                if (contrasena != confirmarContrasena) {
                                    errorMessage = "Las contraseñas no coinciden."
                                } else {
                                    isLoading = true
                                    val newUser = Usuario(
                                        nombreCompleto = nombre,
                                        usuario = usuario,
                                        correo = correo,
                                        cum = cum,
                                        contrasena = contrasena
                                    )
                                    FirestoreManager.verificarYRegistrarUsuario(
                                        usuario = newUser,
                                        onSuccess = {
                                            isLoading = false
                                            onBackClick()
                                        },
                                        onConflict = { usuarioExiste, correoExiste, cumExiste ->
                                            isLoading = false
                                            if (usuarioExiste) {
                                                errorUsuarioMsg = "Nombre de usuario inválido"
                                            }
                                            if (correoExiste) {
                                                errorCorreoMsg = "Correo electrónico inválido"
                                            }
                                            if (cumExiste) {
                                                errorCumMsg = "CUM inválido"
                                            }
                                        },
                                        onError = { e ->
                                            isLoading = false
                                            errorMessage = "Error al registrar: ${e.localizedMessage}"
                                        }
                                    )
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

// PANTALLA PRINCIPAL CON NAVEGADOR SUPERIOR Y ESTADO FIRESTORE EN TIEMPO REAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: Usuario,
    sessionManager: SessionManager,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Inicio (Usuarios), 1: Proyectos
    var selectedUserForProfile by remember { mutableStateOf<Usuario?>(null) }
    var selectedProjectForDetail by remember { mutableStateOf<Proyecto?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Consumo del estado en tiempo real de Firestore mediante Flow.collectAsState()
    val usersList by FirestoreManager.obtenerTodosLosUsuarios().collectAsState(initial = emptyList())
    val projectsList by FirestoreManager.obtenerTodosLosProyectos().collectAsState(initial = emptyList())

    // Sub-screen: Detalle de perfil
    if (selectedUserForProfile != null) {
        val userToDisplay = selectedUserForProfile!!

        ProfileScreen(
            user = userToDisplay,
            currentUser = currentUser,
            onBackClick = { selectedUserForProfile = null },
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
            onBackClick = { selectedProjectForDetail = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RoverAcción", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    // Botón Modo Oscuro
                    IconButton(onClick = onToggleDarkTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Modo Oscuro",
                            tint = Color.White
                        )
                    }
                    // Botón para ver perfil personal
                    IconButton(onClick = { selectedUserForProfile = currentUser }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Mi Perfil",
                            tint = Color.White
                        )
                    }
                    // Botón para salir de la cuenta
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
                    onClick = { selectedTab = 0 },
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
                    onClick = { selectedTab = 1 },
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
                // PÁGINA DE INICIO (USUARIOS REGISTRADOS EN TIEMPO REAL DESDE FIRESTORE)
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
                                    selectedUserForProfile = user
                                }
                            )
                        }
                    }
                }

                // PROYECTOS REGISTRADOS EN TIEMPO REAL DESDE FIRESTORE
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
    project: Proyecto,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Objetivo: ${project.objetivoGeneral}",
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
                    text = "Voluntarios: ${project.voluntariosIds.size}",
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

@Composable
fun ProfileCard(
    user: Usuario,
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
                Text(
                    text = user.nombreCompleto.ifBlank { "S/N" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = nameColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Usuario: @${user.usuario.ifBlank { "S/N" }}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "CUM: ${user.cum.ifBlank { "S/N" }}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                ContactItem(icon = Icons.Filled.Phone, text = user.telefono?.ifBlank { "S/N" } ?: "S/N")
                ContactItem(icon = Icons.Filled.Email, text = user.correo.ifBlank { "S/N" })
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
