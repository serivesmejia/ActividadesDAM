package org.deltacv.myapplication

import android.app.Activity
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import org.deltacv.myapplication.ui.theme.getCardColorScheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            LaunchedEffect(Unit) {

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

                        mostrarLogin = false
                    }
                }
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme
            ) {

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

                        onToggleDarkTheme = {
                            isDarkTheme = !isDarkTheme
                        },

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

// LOGIN

@Composable
fun LoginScreen(
    cloudRepository: CloudRepository,
    sessionManager: SessionManager,
    onLoginSuccess: (User) -> Unit
) {

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
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
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
                    value = correoUsuario,

                    onValueChange = {
                        correoUsuario = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Correo o Nombre de Usuario"
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
                    value = cum,

                    onValueChange = {
                        cum = it
                    },

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
                                )
                            }

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

                    Text(
                        text = "¿No tienes cuenta? ",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

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
fun RegisterScreen(
    cloudRepository: CloudRepository,
    sessionManager: SessionManager,
    onRegisterSuccess: (User) -> Unit,
    onBackClick: () -> Unit
) {

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

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                OutlinedTextField(
                    value = correo,

                    onValueChange = {
                        correo = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Correo electrónico")
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


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


                OutlinedTextField(
                    value = cum,

                    onValueChange = {
                        cum = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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

                    singleLine = true,

                    visualTransformation =
                        PasswordVisualTransformation(),

                    shape =
                        RoundedCornerShape(12.dp)
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


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

                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 14.sp
                    )

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


// MAIN SCREEN - NAVEGACIÓN ADAPTABLE

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
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

    // PERFIL

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
        )

        return
    }

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
    project: ProyectoData,
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
                )


                Button(

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


@Composable
fun ProfileCard(
    user: User,
    index: Int,
    isDark: Boolean,
    onClick: () -> Unit
) {

    val cardScheme =
        getCardColorScheme(
            index,
            isDark
        )


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

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

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
            }
        }
    }
}

// CONTACTO

@Composable
fun ContactItem(
    icon: ImageVector,
    text: String,
    color: Color = Color.Gray,
    textColor: Color = Color.Gray
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(

            imageVector =
                icon,

            contentDescription =
                null,

            modifier =
                Modifier.size(15.dp),

            tint =
                color
        )


        Spacer(
            modifier =
                Modifier.width(6.dp)
        )


        Text(

            text =
                text,

            fontSize =
                13.sp,

            color =
                textColor
        )
    }
}
