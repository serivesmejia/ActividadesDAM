package org.deltacv.myapplication

import android.app.Activity
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

<<<<<<< HEAD
        val sessionManager =
            SessionManager(applicationContext)

        val cloudRepository =
            CloudRepository(applicationContext)

        setContent {

            var isDarkTheme by remember {
                mutableStateOf(false)
            }

            var currentUser by remember {
                mutableStateOf<User?>(null)
            }

            var mostrarLogin by remember {
                mutableStateOf(true)
            }

            // AUTO LOGIN

=======
        val sessionManager = SessionManager(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentUserId by remember { mutableStateOf<String?>(null) }
            var mostrarLogin by remember { mutableStateOf(true) }

            // Escuchar el estado del usuario activo en tiempo real mediante Flow
            val currentUserState by FirestoreManager.obtenerUsuarioPorUid(currentUserId ?: "").collectAsState(initial = null)

            // Auto-login automático si existen credenciales guardadas en el dispositivo
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            LaunchedEffect(Unit) {

<<<<<<< HEAD
                if (sessionManager.hasActiveSession()) {

                    val savedUserId =
                        sessionManager.getSavedUserId()

                    val savedUserOrEmail =
                        sessionManager.getSavedUserOrEmail()

                    val savedCum =
                        sessionManager.getSavedCum()

                    val savedPass =
                        sessionManager.getSavedPass()

                    val users =
                        cloudRepository.getUsers()

                    val matchedUser =
                        users.find { user ->

                            user.id == savedUserId ||

                                    (
                                            (
                                                    user.correo == savedUserOrEmail ||
                                                            user.usuario == savedUserOrEmail
                                                    ) &&

                                                    user.cum == savedCum &&

                                                    user.contrasena == savedPass
                                            )
                        }

                    if (matchedUser != null) {

                        currentUser = matchedUser

=======
                    if (!savedUserId.isNullOrBlank()) {
                        currentUserId = savedUserId
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
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

<<<<<<< HEAD
            MyApplicationTheme(
                darkTheme = isDarkTheme
            ) {

                if (mostrarLogin || currentUser == null) {

=======
            MyApplicationTheme(darkTheme = isDarkTheme) {
                if (mostrarLogin || currentUserState == null) {
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    LoginScreen(
                        sessionManager = sessionManager,

                        onLoginSuccess = { user ->
<<<<<<< HEAD

                            currentUser = user
=======
                            currentUserId = user.uid
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                            mostrarLogin = false
                        }
                    )

                } else {

                    MainScreen(
                        currentUser = currentUserState!!,
                        sessionManager = sessionManager,
                        isDarkTheme = isDarkTheme,

                        onToggleDarkTheme = {
                            isDarkTheme = !isDarkTheme
                        },

                        onLogout = {

                            sessionManager.clearSession()
<<<<<<< HEAD

                            currentUser = null
                            mostrarLogin = true
                        },

                        onUserUpdated = { updatedUser ->

                            cloudRepository.saveUser(updatedUser)

                            if (updatedUser.id == currentUser?.id) {
                                currentUser = updatedUser
                            }
=======
                            currentUserId = null
                            mostrarLogin = true
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                        }
                    )
                }
            }
        }
    }
}

// LOGIN

@Composable
fun LoginScreen(
    sessionManager: SessionManager,
    onLoginSuccess: (Usuario) -> Unit
) {
<<<<<<< HEAD

    var correoUsuario by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var cum by remember {
        mutableStateOf("")
    }

    var recordarme by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var mostrarRegistro by remember {
        mutableStateOf(false)
    }


    if (mostrarRegistro) {

        RegisterScreen(
            cloudRepository = cloudRepository,
            sessionManager = sessionManager,

            onRegisterSuccess = { user ->
                onLoginSuccess(user)
            },

            onBackClick = {
                mostrarRegistro = false
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            }
        )

        return
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Bienvenido",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 15.sp,
<<<<<<< HEAD
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
=======
                    color = Color.Gray
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                )


                errorMessage?.let {

                    Text(
                        text = it,
                        color =
                            MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }

<<<<<<< HEAD

=======
                // CORREO / NOMBRE DE USUARIO
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                OutlinedTextField(
                    value = correoUsuario,

                    onValueChange = {
                        correoUsuario = it
                    },

                    modifier = Modifier.fillMaxWidth(),
<<<<<<< HEAD

                    label = {
                        Text(
                            "Correo o Nombre de Usuario"
                        )
                    },

=======
                    label = { Text("Correo / nombre de usuario") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Usuario") },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // CUM
                OutlinedTextField(
                    value = cum,
<<<<<<< HEAD

                    onValueChange = {
                        cum = it
                    },

=======
                    onValueChange = { nuevoTexto ->
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            cum = nuevoTexto
                        }
                    },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("CUM")
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // CONTRASEÑA
                OutlinedTextField(
                    value = contrasena,

                    onValueChange = {
                        contrasena = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("Contraseña")
                    },

                    singleLine = true,

                    visualTransformation =
                        PasswordVisualTransformation(),

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )

<<<<<<< HEAD

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Checkbox(
                        checked = recordarme,

                        onCheckedChange = {
                            recordarme = it
                        }
                    )

                    Text(
                        text = "Recordarme",

                        fontSize = 14.sp,

                        color =
                            MaterialTheme.colorScheme.onSurface,

                        modifier =
                            Modifier.clickable {
                                recordarme = !recordarme
                            }
                    )
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Button(
                    onClick = {

                        val users =
                            cloudRepository.getUsers()

                        val matchedUser =
                            users.find { u ->

                                (
                                        u.correo.equals(
                                            correoUsuario,
                                            ignoreCase = true
                                        ) ||

                                                u.usuario.equals(
                                                    correoUsuario,
                                                    ignoreCase = true
                                                )
                                        ) &&

                                        u.cum.equals(
                                            cum,
                                            ignoreCase = true
                                        ) &&

                                        u.contrasena == contrasena
                            }


                        if (matchedUser != null) {

                            if (recordarme) {

                                sessionManager.saveSession(
                                    userId =
                                        matchedUser.id,

                                    userOrEmail =
                                        correoUsuario,

                                    cum = cum,

                                    pass =
                                        contrasena
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                                )
                            } else {
                                errorMessage = "Por favor completa todos los campos."
                            }
<<<<<<< HEAD

                            onLoginSuccess(
                                matchedUser
                            )

                        } else {

                            errorMessage =
                                "Credenciales incorrectas. " +
                                        "Verifica correo/usuario, CUM y contraseña."
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.primary
                        )
                ) {

                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        color = Color.White
                    )
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Row(
                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
<<<<<<< HEAD

                    Text(
                        text = "¿No tienes cuenta? ",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

=======
                    Text(text = "¿No tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    Text(
                        text = "Regístrate",

                        color =
                            MaterialTheme.colorScheme.primary,

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.clickable {
                                mostrarRegistro = true
                            }
                    )
                }
            }
        }
    }
}


// REGISTRO

@Composable
fun RegistroScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onRegistroSuccess: (Usuario) -> Unit = {}
) {
<<<<<<< HEAD

    var nombre by remember {
        mutableStateOf("")
    }

    var usuario by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var telefono by remember {
        mutableStateOf("")
    }

    var cargo by remember {
        mutableStateOf("Rover")
    }

    var cum by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var confirmarContrasena by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            shape =
                RoundedCornerShape(20.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                errorMessage?.let {

                    Text(
                        text = it,

                        color =
                            MaterialTheme.colorScheme.error,

                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }


                OutlinedTextField(
                    value = nombre,

                    onValueChange = {
                        nombre = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Nombre completo")
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                OutlinedTextField(
                    value = usuario,
<<<<<<< HEAD

                    onValueChange = {
                        usuario = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Nombre de usuario (@...)"
                        )
                    },

=======
                    onValueChange = {
                        usuario = it
                        errorUsuarioMsg = null
                    },
                    isError = errorUsuarioMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de usuario") },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
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


                OutlinedTextField(
                    value = correo,
<<<<<<< HEAD

                    onValueChange = {
                        correo = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Correo electrónico")
                    },

=======
                    onValueChange = {
                        correo = it
                        errorCorreoMsg = null
                    },
                    isError = errorCorreoMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )
<<<<<<< HEAD


                OutlinedTextField(
                    value = telefono,

                    onValueChange = {
                        telefono = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Teléfono de contacto"
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                OutlinedTextField(
                    value = cargo,

                    onValueChange = {
                        cargo = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Cargo / Rol (Ej. Rover, Scouter)"
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810


                OutlinedTextField(
                    value = cum,
<<<<<<< HEAD

                    onValueChange = {
                        cum = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("CUM")
                    },

=======
                    onValueChange = { nuevoTexto ->
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            cum = nuevoTexto
                            errorCumMsg = null
                        }
                    },
                    isError = errorCumMsg != null,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CUM") },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
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


                OutlinedTextField(
                    value = contrasena,

                    onValueChange = {
                        contrasena = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Contraseña")
                    },

                    singleLine = true,

                    visualTransformation =
                        PasswordVisualTransformation(),

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                OutlinedTextField(
                    value = confirmarContrasena,
<<<<<<< HEAD

                    onValueChange = {
                        confirmarContrasena = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Confirmar contraseña"
                        )
                    },

=======
                    onValueChange = { confirmarContrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmación de contraseña") },
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    singleLine = true,

                    visualTransformation =
                        PasswordVisualTransformation(),

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


<<<<<<< HEAD
                Button(
                    onClick = {

                        if (
                            nombre.isBlank() ||
                            usuario.isBlank() ||
                            correo.isBlank() ||
                            cum.isBlank() ||
                            contrasena.isBlank()
                        ) {

                            errorMessage =
                                "Por favor completa todos los campos obligatorios."

                            return@Button
                        }


                        if (
                            contrasena !=
                            confirmarContrasena
                        ) {

                            errorMessage =
                                "Las contraseñas no coinciden."

                            return@Button
                        }


                        val existingUsers =
                            cloudRepository.getUsers()


                        if (
                            existingUsers.any {

                                it.usuario.equals(
                                    usuario,
                                    ignoreCase = true
                                ) ||

                                        it.correo.equals(
                                            correo,
                                            ignoreCase = true
                                        )
                            }
                        ) {

                            errorMessage =
                                "El nombre de usuario o correo ya está registrado."

                            return@Button
                        }


                        val newUser =
                            User(

                                id =
                                    "user_${System.currentTimeMillis()}",

                                nombre =
                                    nombre.trim(),

                                usuario =
                                    usuario.trim()
                                        .removePrefix("@"),

                                correo =
                                    correo.trim(),

                                telefono =
                                    telefono.trim(),

                                cargo =
                                    cargo.trim(),

                                cum =
                                    cum.trim(),

                                contrasena =
                                    contrasena
                            )


                        cloudRepository.saveUser(
                            newUser
                        )


                        sessionManager.saveSession(

                            userId =
                                newUser.id,

                            userOrEmail =
                                newUser.correo,

                            cum =
                                newUser.cum,

                            pass =
                                newUser.contrasena
                        )


                        onRegisterSuccess(
                            newUser
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.primary
                        )
                ) {

                    Text(
                        text = "Registrarse",
                        fontSize = 16.sp,
                        color = Color.White
                    )
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Row(
                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
<<<<<<< HEAD

                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 14.sp
                    )

=======
                    Text(text = "¿Ya tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                    Text(
                        text = "Inicia sesión",

                        color =
                            MaterialTheme.colorScheme.primary,

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.clickable {
                                onBackClick()
                            }
                    )
                }
            }
        }
    }
}

<<<<<<< HEAD
=======
// PANTALLA PRINCIPAL CON NAVEGADOR SUPERIOR Y ESTADO FIRESTORE EN TIEMPO REAL
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810

// MAIN SCREEN - NAVEGACIÓN ADAPTABLE

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun MainScreen(
    currentUser: Usuario,
    sessionManager: SessionManager,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onLogout: () -> Unit
) {
<<<<<<< HEAD

    // OBTENER TAMAÑO DE LA VENTANA

    val context = LocalContext.current

    val activity = context as? Activity

    if (activity == null) {
        return
    }

    val windowSize =
        calculateWindowSizeClass(activity)

    val widthSizeClass =
        windowSize.widthSizeClass

    // ESTADO

    var selectedTab by remember {
        mutableStateOf(0)
    }

    var selectedUserForProfile by remember {
        mutableStateOf<User?>(null)
    }

    var selectedProjectForDetail by remember {
        mutableStateOf<ProyectoData?>(null)
    }


    var usersList by remember {
        mutableStateOf(
            cloudRepository.getUsers()
        )
    }

    var projectsList by remember {
        mutableStateOf(
            cloudRepository.getProjects()
        )
    }


    fun refreshData() {

        usersList =
            cloudRepository.getUsers()

        projectsList =
            cloudRepository.getProjects()
    }
=======
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Inicio (Usuarios), 1: Proyectos
    var selectedUserForProfile by remember { mutableStateOf<Usuario?>(null) }
    var selectedProjectForDetail by remember { mutableStateOf<Proyecto?>(null) }
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810

    // PERFIL

<<<<<<< HEAD
    if (selectedUserForProfile != null) {

        val userToShow =
            selectedUserForProfile!!

        val userOwnProjects =
            projectsList.filter {
                it.creadorId == userToShow.id
            }

        val userVolunteerProjects =
            projectsList.filter {
                it.participantesIds.contains(
                    userToShow.id
                )
            }


        ProfileScreen(

            user = userToShow,

            currentUser = currentUser,

            userProjects =
                userOwnProjects,

            volunteerProjects =
                userVolunteerProjects,

            onBackClick = {
                selectedUserForProfile = null
            },

            onEditProfile = { updatedUser ->

                cloudRepository.saveUser(
                    updatedUser
                )

                onUserUpdated(
                    updatedUser
                )

                refreshData()

                selectedUserForProfile =
                    updatedUser
            },

            onCreateProject = { newProj ->

                cloudRepository.saveProject(
                    newProj
                )

                refreshData()
            },

=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            onSelectProject = { proj ->

                selectedProjectForDetail =
                    proj
            }
        )

        return
    }

    // DETALLE DEL PROYECTO

    if (selectedProjectForDetail != null) {

        val proj =
            selectedProjectForDetail!!


        ProjectDetailScreen(

            proyecto = proj,

            currentUser = currentUser,
<<<<<<< HEAD

            onBackClick = {
                selectedProjectForDetail = null
            },

            onToggleVolunteer = {

                cloudRepository.toggleVolunteer(
                    proj.id,
                    currentUser.id
                )

                refreshData()

                selectedProjectForDetail =
                    cloudRepository
                        .getProjects()
                        .find {
                            it.id == proj.id
                        }
            },

            onEditProject = { updatedProj ->

                cloudRepository.saveProject(
                    updatedProj
                )

                refreshData()

                selectedProjectForDetail =
                    updatedProj
            },

            onDeleteProject = { projId ->

                cloudRepository.deleteProject(
                    projId
                )

                refreshData()

                selectedProjectForDetail =
                    null
            }
=======
            onBackClick = { selectedProjectForDetail = null }
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
        )

        return
    }

<<<<<<< HEAD
    // NAVEGACIÓN ADAPTABLE

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        // NAVEGACIÓN LATERAL
        // SOLO TABLET / PANTALLAS GRANDES

        if (
            widthSizeClass !=
            WindowWidthSizeClass.Compact
        ) {

            NavigationRail(

                containerColor =
                    MaterialTheme.colorScheme.secondary,

                contentColor = Color.White
            ) {


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                NavigationRailItem(

                    selected =
                        selectedTab == 0,

                    onClick = {

                        selectedTab = 0

                        refreshData()
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Filled.FilterHdr,

                            contentDescription =
                                "Inicio"
                        )
                    },

                    label = {
                        Text("Inicio")
                    },

                    colors =
                        NavigationRailItemDefaults.colors(

                            selectedIconColor =
                                Color.White,

                            unselectedIconColor =
                                Color.White.copy(
                                    alpha = 0.7f
                                ),

                            selectedTextColor =
                                Color.White,

                            unselectedTextColor =
                                Color.White.copy(
                                    alpha = 0.7f
                                ),

                            indicatorColor =
                                MaterialTheme.colorScheme
                                    .secondaryContainer
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                NavigationRailItem(

                    selected =
                        selectedTab == 1,

                    onClick = {

                        selectedTab = 1

                        refreshData()
                    },

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Filled.Campaign,

                            contentDescription =
                                "Proyectos"
                        )
                    },

                    label = {
                        Text("Proyectos")
                    },

                    colors =
                        NavigationRailItemDefaults.colors(

                            selectedIconColor =
                                Color.White,

                            unselectedIconColor =
                                Color.White.copy(
                                    alpha = 0.7f
                                ),

                            selectedTextColor =
                                Color.White,

                            unselectedTextColor =
                                Color.White.copy(
                                    alpha = 0.7f
                                ),

                            indicatorColor =
                                MaterialTheme.colorScheme
                                    .secondaryContainer
                        )
                )
            }
        }

        // CONTENIDO PRINCIPAL

        Scaffold(

            modifier =
                Modifier.weight(1f),

            // TOP BAR

            topBar = {

                TopAppBar(

                    title = {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Filled.FilterHdr,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFF5CA1CD),

                                modifier =
                                    Modifier.size(28.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(

                                text =
                                    "RoverAcción",

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize =
                                    22.sp
                            )
                        }
                    },


                    actions = {

                        // MODO OSCURO

                        IconButton(

                            onClick =
                                onToggleDarkTheme
                        ) {

                            Icon(

                                imageVector =

                                    if (isDarkTheme) {
                                        Icons.Filled.LightMode
                                    } else {
                                        Icons.Filled.DarkMode
                                    },

                                contentDescription =

                                    if (isDarkTheme) {
                                        "Modo claro"
                                    } else {
                                        "Modo oscuro"
                                    },

                                tint =
                                    Color.White
                            )
                        }

                        // PERFIL

                        IconButton(

                            onClick = {

                                selectedUserForProfile =
                                    currentUser
                            }
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Filled.Person,

                                contentDescription =
                                    "Mi Perfil",

                                tint =
                                    Color.White
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                            )
                        }

                        // CERRAR SESIÓN

                        IconButton(

                            onClick =
                                onLogout
                        ) {

                            Icon(

                                imageVector =
                                    Icons.AutoMirrored.Filled.ExitToApp,

                                contentDescription =
                                    "Cerrar sesión",

                                tint =
                                    Color.White
                            )
                        }
                    },


                    colors =
                        TopAppBarDefaults.topAppBarColors(

                            containerColor =
                                MaterialTheme.colorScheme.primary,

                            titleContentColor =
                                Color.White
                        )
                )
            },

            // NAVEGACIÓN INFERIOR
            bottomBar = {

                if (
                    widthSizeClass ==
                    WindowWidthSizeClass.Compact
                ) {

                    NavigationBar(

                        containerColor =
                            MaterialTheme.colorScheme.secondary
                    ) {

                        // INICIO

                        NavigationBarItem(

                            selected =
                                selectedTab == 0,

                            onClick = {

                                selectedTab = 0

                                refreshData()
                            },

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Filled.FilterHdr,

                                    contentDescription =
                                        "Inicio"
                                )
                            },

                            label = {

                                Text(
                                    "Inicio"
                                )
                            },

                            colors =
                                NavigationBarItemDefaults.colors(

                                    selectedTextColor =
                                        Color.White,

                                    unselectedTextColor =
                                        Color.White.copy(
                                            alpha = 0.7f
                                        ),

                                    selectedIconColor =
                                        Color.White,

                                    unselectedIconColor =
                                        Color.White.copy(
                                            alpha = 0.7f
                                        ),

                                    indicatorColor =
                                        MaterialTheme.colorScheme
                                            .secondaryContainer
                                )
                        )

                        // PROYECTOS

                        NavigationBarItem(

                            selected =
                                selectedTab == 1,

                            onClick = {

                                selectedTab = 1

                                refreshData()
                            },

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Filled.Campaign,

                                    contentDescription =
                                        "Proyectos"
                                )
                            },

                            label = {

                                Text(
                                    "Proyectos"
                                )
                            },

                            colors =
                                NavigationBarItemDefaults.colors(

                                    selectedTextColor =
                                        Color.White,

                                    unselectedTextColor =
                                        Color.White.copy(
                                            alpha = 0.7f
                                        ),

                                    selectedIconColor =
                                        Color.White,

                                    unselectedIconColor =
                                        Color.White.copy(
                                            alpha = 0.7f
                                        ),

                                    indicatorColor =
                                        MaterialTheme.colorScheme
                                            .secondaryContainer
                                )
                        )
                    }
                }
            }

<<<<<<< HEAD
        ) { innerPadding ->

            // CONTENIDO

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {


                when (selectedTab) {

                    // INICIO

                    0 -> {

                        LazyColumn(

                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme
                                        .background
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                        ) {


                            item {

                                Text(

                                    text =
                                        "Usuarios Registrados",

                                    fontSize =
                                        22.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        MaterialTheme.colorScheme
                                            .onBackground,

                                    modifier =
                                        Modifier.padding(
                                            bottom = 12.dp,
                                            top = 8.dp
                                        )
                                )
                            }


                            itemsIndexed(
                                usersList
                            ) { index, user ->

                                ProfileCard(

                                    user = user,

                                    index = index,

                                    isDark =
                                        isDarkTheme,

                                    onClick = {

                                        selectedUserForProfile =
                                            user
                                    }
                                )
                            }
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                        }
                    }
                    // PROYECTOS

                    1 -> {

                        LazyColumn(

                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme
                                        .background
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                        ) {


                            item {

                                Text(

                                    text =
                                        "Lista de Proyectos",

                                    fontSize =
                                        22.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        MaterialTheme.colorScheme
                                            .onBackground,

                                    modifier =
                                        Modifier.padding(
                                            bottom = 12.dp,
                                            top = 8.dp
                                        )
                                )
                            }


                            items(
                                projectsList
                            ) { project ->

                                ProjectCard(

                                    project =
                                        project,

                                    onClick = {

                                        selectedProjectForDetail =
                                            project
                                    }
                                )
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
            .padding(
                vertical = 8.dp
            )
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            Text(

                text =
                    project.titulo,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme.colorScheme
                        .onSurface
            )

<<<<<<< HEAD

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(

                text =
                    project.ods,

                fontSize =
                    15.sp,

                color =
                    MaterialTheme.colorScheme
                        .secondary
            )


            Text(

                text =
                    "Creado por: ${project.creadorNombre}",

                fontSize =
                    14.sp,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    "Objetivo: ${project.objetivoPrincipal}",

                fontSize =
                    14.sp,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,

                lineHeight =
                    20.sp
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
=======
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Objetivo: ${project.objetivoGeneral}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
<<<<<<< HEAD

                    text =
                        "Voluntarios: " +
                                project.participantesIds.size,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        MaterialTheme.colorScheme
                            .primary
=======
                    text = "Voluntarios: ${project.voluntariosIds.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                )


                Button(
<<<<<<< HEAD

                    onClick =
                        onClick,

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .primaryContainer
                        ),

                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp,
                            vertical = 6.dp
                        )
                ) {

                    Text(

                        text =
                            "Ver detalles",

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            13.sp,

                        color =
                            MaterialTheme.colorScheme
                                .onPrimaryContainer
                    )
=======
                    onClick = onClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(text = "Ver detalles", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
                }
            }
        }
    }
}

// TARJETA DE USUARIO

fun String?.orSN(): String =
    if (this.isNullOrBlank()) {
        "S/N"
    } else {
        this
    }

<<<<<<< HEAD

=======
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
@Composable
fun ProfileCard(
    user: Usuario,
    nameColor: Color,
    onClick: () -> Unit
) {
<<<<<<< HEAD

    val cardScheme =
        getCardColorScheme(
            index,
            isDark
        )


=======
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
    Card(

        modifier = Modifier
            .fillMaxWidth()
<<<<<<< HEAD
            .padding(
                vertical = 8.dp
            )
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    cardScheme.cardBg
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {


        Box(

=======
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
<<<<<<< HEAD

            // DECORACIÓN

            if (index % 3 == 1) {

                Canvas(

                    modifier = Modifier
                        .size(
                            width = 110.dp,
                            height = 90.dp
                        )
                        .align(
                            Alignment.BottomEnd
                        )
                ) {

                    val path1 =
                        Path().apply {

                            moveTo(
                                size.width * 0.2f,
                                size.height
                            )

                            lineTo(
                                size.width * 0.65f,
                                size.height * 0.25f
                            )

                            lineTo(
                                size.width * 1.1f,
                                size.height
                            )

                            close()
                        }


                    drawPath(

                        path1,

                        color =
                            cardScheme.decorationColor
                                .copy(
                                    alpha = 0.5f
                                )
                    )


                    val path2 =
                        Path().apply {

                            moveTo(
                                0f,
                                size.height
                            )

                            lineTo(
                                size.width * 0.45f,
                                size.height * 0.45f
                            )

                            lineTo(
                                size.width * 0.9f,
                                size.height
                            )

                            close()
                        }


                    drawPath(

                        path2,

                        color =
                            cardScheme.decorationColor
                                .copy(
                                    alpha = 0.8f
                                )
                    )
                }

            } else {

                Canvas(

                    modifier = Modifier
                        .size(
                            width = 70.dp,
                            height = 90.dp
                        )
                        .align(
                            Alignment.CenterEnd
                        )
                ) {

                    val stemStart =
                        androidx.compose.ui.geometry.Offset(
                            size.width * 0.7f,
                            size.height * 0.95f
                        )

                    val stemEnd =
                        androidx.compose.ui.geometry.Offset(
                            size.width * 0.3f,
                            size.height * 0.05f
                        )


                    drawLine(

                        color =
                            cardScheme.decorationColor,

                        start =
                            stemStart,

                        end =
                            stemEnd,

                        strokeWidth =
                            3.dp.toPx()
                    )


                    drawOval(

                        color =
                            cardScheme.decorationColor,

                        topLeft =
                            androidx.compose.ui.geometry.Offset(
                                size.width * 0.1f,
                                size.height * 0.15f
                            ),

                        size =
                            androidx.compose.ui.geometry.Size(
                                30.dp.toPx(),
                                16.dp.toPx()
                            )
                    )


                    drawOval(

                        color =
                            cardScheme.decorationColor,

                        topLeft =
                            androidx.compose.ui.geometry.Offset(
                                size.width * 0.45f,
                                size.height * 0.3f
                            ),

                        size =
                            androidx.compose.ui.geometry.Size(
                                28.dp.toPx(),
                                15.dp.toPx()
                            )
                    )


                    drawOval(

                        color =
                            cardScheme.decorationColor,

                        topLeft =
                            androidx.compose.ui.geometry.Offset(
                                size.width * 0.2f,
                                size.height * 0.5f
                            ),

                        size =
                            androidx.compose.ui.geometry.Size(
                                28.dp.toPx(),
                                15.dp.toPx()
                            )
                    )


                    drawOval(

                        color =
                            cardScheme.decorationColor,

                        topLeft =
                            androidx.compose.ui.geometry.Offset(
                                size.width * 0.5f,
                                size.height * 0.65f
                            ),

                        size =
                            androidx.compose.ui.geometry.Size(
                                26.dp.toPx(),
                                14.dp.toPx()
                            )
                    )
                }
            }
            // INFORMACIÓN
            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Box(

                    modifier = Modifier
                        .size(66.dp)
                        .clip(
                            CircleShape
                        )
                        .background(
                            cardScheme.avatarBg
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Filled.Person,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(42.dp),

                        tint =
                            cardScheme.avatarIcon
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                Column {


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(

                            text =
                                user.nombre.orSN(),

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                cardScheme.textColor
                        )


                        if (
                            user.cargo.orSN().isNotBlank() &&
                            user.cargo.orSN() != "S/N"
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )


                            Box(

                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            8.dp
                                        )
                                    )
                                    .background(
                                        cardScheme.badgeBg
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 2.dp
                                    )
                            ) {

                                Text(

                                    text =
                                        user.cargo.orSN(),

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.SemiBold,

                                    color =
                                        cardScheme.badgeText
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            "Usuario: @${user.usuario.orSN()}",

                        fontSize =
                            13.sp,

                        color =
                            cardScheme.textColor
                    )


                    Text(

                        text =
                            "CUM: ${user.cum.orSN()}",

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            cardScheme.textColor
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    ContactItem(

                        icon =
                            Icons.Filled.Phone,

                        text =
                            user.telefono.orSN(),

                        color =
                            cardScheme.iconTint,

                        textColor =
                            cardScheme.textColor
                    )


                    ContactItem(

                        icon =
                            Icons.Filled.Email,

                        text =
                            user.correo.orSN(),

                        color =
                            cardScheme.iconTint,

                        textColor =
                            cardScheme.textColor
                    )
                }
=======
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
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
            }
        }
    }
}

// CONTACTO

@Composable
fun ContactItem(
    icon: ImageVector,
    text: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
<<<<<<< HEAD

            imageVector =
                icon,

            contentDescription =
                null,

            modifier =
                Modifier.size(15.dp),

            tint =
                color
=======
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
        )


        Spacer(
            modifier =
                Modifier.width(6.dp)
        )


        Text(
<<<<<<< HEAD

            text =
                text,

            fontSize =
                13.sp,

            color =
                textColor
=======
            text = text,
            fontSize = 13.sp,
            color = Color.Gray
>>>>>>> 57c89af53c192489f15c93102d4755365d1ab810
        )
    }
}
