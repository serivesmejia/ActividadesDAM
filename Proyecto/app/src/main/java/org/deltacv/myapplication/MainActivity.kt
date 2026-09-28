package org.deltacv.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import org.deltacv.myapplication.ui.theme.getCardColorScheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentUserId by remember { mutableStateOf<String?>(null) }
            var mostrarLogin by remember { mutableStateOf(true) }

            val currentUserState by FirestoreManager.obtenerUsuarioPorUid(currentUserId ?: "").collectAsState(initial = null)

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
                            onResult = { user ->
                                if (user != null) {
                                    currentUserId = user.uid
                                    sessionManager.saveSession(
                                        userOrEmail = user.correo,
                                        cum = user.cum,
                                        pass = user.contrasena,
                                        userId = user.uid
                                    )
                                    mostrarLogin = false
                                }
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

// LOGIN SCREEN

@Composable
fun LoginScreen(
    sessionManager: SessionManager,
    onLoginSuccess: (Usuario) -> Unit
) {
    var correoUsuario by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(false) }
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = correoUsuario,
                    onValueChange = { correoUsuario = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo o Usuario") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cum,
                    onValueChange = { cum = it },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = recordarme,
                        onCheckedChange = { recordarme = it }
                    )
                    Text(
                        text = "Recordarme",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { recordarme = !recordarme }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (correoUsuario.isBlank() || cum.isBlank() || contrasena.isBlank()) {
                            errorMessage = "Por favor completa todos los campos."
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        FirestoreManager.buscarUsuarioParaLogin(
                            identificador = correoUsuario.trim(),
                            cum = cum.trim(),
                            contrasena = contrasena,
                            onResult = { user ->
                                isLoading = false
                                if (user != null) {
                                    if (recordarme) {
                                        sessionManager.saveSession(
                                            userOrEmail = user.correo,
                                            cum = user.cum,
                                            pass = user.contrasena,
                                            userId = user.uid
                                        )
                                    }
                                    onLoginSuccess(user)
                                } else {
                                    errorMessage = "Credenciales incorrectas. Verifica correo/usuario, CUM y contraseña."
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Iniciar Sesión", fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿No tienes cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
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

// REGISTRO SCREEN

@Composable
fun RegistroScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onRegistroSuccess: (Usuario) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var cum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
                    onValueChange = { cum = it },
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
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || usuario.isBlank() || correo.isBlank() || cum.isBlank() || contrasena.isBlank()) {
                            errorMessage = "Por favor completa todos los campos."
                            return@Button
                        }
                        if (contrasena != confirmarContrasena) {
                            errorMessage = "Las contraseñas no coinciden."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        val newUser = Usuario(
                            uid = "",
                            nombreCompleto = nombre.trim(),
                            usuario = usuario.trim().removePrefix("@"),
                            correo = correo.trim(),
                            cum = cum.trim(),
                            contrasena = contrasena
                        )

                        FirestoreManager.verificarYRegistrarUsuario(
                            usuario = newUser,
                            onSuccess = { createdUser ->
                                isLoading = false
                                sessionManager.saveSession(
                                    userOrEmail = createdUser.correo,
                                    cum = createdUser.cum,
                                    pass = createdUser.contrasena,
                                    userId = createdUser.uid
                                )
                                onRegistroSuccess(createdUser)
                            },
                            onError = { err ->
                                isLoading = false
                                errorMessage = err
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Registrarse", fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿Ya tienes cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
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

// MAIN SCREEN

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

    val usersList by FirestoreManager.obtenerTodosLosUsuarios().collectAsState(initial = emptyList())
    val projectsList by FirestoreManager.obtenerTodosLosProyectos().collectAsState(initial = emptyList())

    val primaryColor = MaterialTheme.colorScheme.primary

    if (selectedUserForProfile != null) {
        ProfileScreen(
            user = selectedUserForProfile!!,
            currentUser = currentUser,
            onBackClick = { selectedUserForProfile = null },
            onSelectProject = { proj -> selectedProjectForDetail = proj }
        )
        return
    }

    if (selectedProjectForDetail != null) {
        ProjectDetailScreen(
            proyecto = selectedProjectForDetail!!,
            currentUser = currentUser,
            onBackClick = { selectedProjectForDetail = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.FilterHdr,
                            contentDescription = null,
                            tint = Color(0xFF5CA1CD),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RoverAcción", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDarkTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Modo Oscuro",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { selectedUserForProfile = currentUser }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Mi Perfil",
                            tint = Color.White
                        )
                    }
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
            NavigationBar(containerColor = MaterialTheme.colorScheme.secondary) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.FilterHdr, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White.copy(alpha = 0.7f),
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Campaign, contentDescription = "Proyectos") },
                    label = { Text("Proyectos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White.copy(alpha = 0.7f),
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
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
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                            )
                        }

                        itemsIndexed(usersList) { index, user ->
                            ProfileCard(
                                user = user,
                                index = index,
                                isDark = isDarkTheme,
                                onClick = {
                                    selectedUserForProfile = user
                                }
                            )
                        }
                    }
                }

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
                                color = MaterialTheme.colorScheme.onBackground,
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

// PROJECT CARD

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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ver detalles",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// PROFILE CARD

fun String?.orSN(): String = if (this.isNullOrBlank()) "S/N" else this

@Composable
fun ProfileCard(
    user: Usuario,
    index: Int,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val cardScheme = getCardColorScheme(index, isDark)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardScheme.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (index % 3 == 1) {
                Canvas(
                    modifier = Modifier
                        .size(width = 110.dp, height = 90.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    val path1 = Path().apply {
                        moveTo(size.width * 0.2f, size.height)
                        lineTo(size.width * 0.65f, size.height * 0.25f)
                        lineTo(size.width * 1.1f, size.height)
                        close()
                    }
                    drawPath(path1, color = cardScheme.decorationColor.copy(alpha = 0.5f))

                    val path2 = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width * 0.45f, size.height * 0.45f)
                        lineTo(size.width * 0.9f, size.height)
                        close()
                    }
                    drawPath(path2, color = cardScheme.decorationColor.copy(alpha = 0.8f))
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .size(width = 70.dp, height = 90.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    val stemStart = Offset(size.width * 0.7f, size.height * 0.95f)
                    val stemEnd = Offset(size.width * 0.3f, size.height * 0.05f)
                    drawLine(
                        color = cardScheme.decorationColor,
                        start = stemStart,
                        end = stemEnd,
                        strokeWidth = 3.dp.toPx()
                    )
                    drawOval(
                        color = cardScheme.decorationColor,
                        topLeft = Offset(size.width * 0.1f, size.height * 0.15f),
                        size = Size(30.dp.toPx(), 16.dp.toPx())
                    )
                    drawOval(
                        color = cardScheme.decorationColor,
                        topLeft = Offset(size.width * 0.45f, size.height * 0.3f),
                        size = Size(28.dp.toPx(), 15.dp.toPx())
                    )
                    drawOval(
                        color = cardScheme.decorationColor,
                        topLeft = Offset(size.width * 0.2f, size.height * 0.5f),
                        size = Size(28.dp.toPx(), 15.dp.toPx())
                    )
                    drawOval(
                        color = cardScheme.decorationColor,
                        topLeft = Offset(size.width * 0.5f, size.height * 0.65f),
                        size = Size(26.dp.toPx(), 14.dp.toPx())
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(cardScheme.avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = cardScheme.avatarIcon
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.nombreCompleto.orSN(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = cardScheme.textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Usuario: @${user.usuario.orSN()}",
                        fontSize = 13.sp,
                        color = cardScheme.textColor
                    )

                    Text(
                        text = "CUM: ${user.cum.orSN()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = cardScheme.textColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    user.telefono?.takeIf { it.isNotBlank() }?.let { tel ->
                        ContactItem(
                            icon = Icons.Filled.Phone,
                            text = tel,
                            color = cardScheme.iconTint,
                            textColor = cardScheme.textColor
                        )
                    }
                    ContactItem(
                        icon = Icons.Filled.Email,
                        text = user.correo.orSN(),
                        color = cardScheme.iconTint,
                        textColor = cardScheme.textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    text: String,
    color: Color = Color.Gray,
    textColor: Color = Color.Gray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = textColor
        )
    }
}
