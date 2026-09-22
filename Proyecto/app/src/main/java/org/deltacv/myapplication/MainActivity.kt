package org.deltacv.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Email
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
import org.deltacv.myapplication.ui.ProjectDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var mostrarLogin by remember { mutableStateOf(true) }

            if (mostrarLogin) {
                LoginScreen(
                    onLoginSuccess = {
                        mostrarLogin = false
                    }
                )
            } else {
                MainScreen()
            }
        }
    }
}


// PANTALLA DE LOGIN


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var correoUsuario by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarRegistro by remember { mutableStateOf(false) }

    if (mostrarRegistro) {
        RegistroScreen(
            onBackClick = {
                mostrarRegistro = false
            }
        )
        return
    }

    val backgroundColor = Color(0xFFE9D4C3)
    val primaryColor = Color(0xFFBD0000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Bienvenido",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CORREO / NOMBRE DE USUARIO
                OutlinedTextField(
                    value = correoUsuario,
                    onValueChange = {
                        correoUsuario = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Correo / nombre de usuario")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Usuario"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // MATRÍCULA
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { nuevoTexto ->

                        // Permite letras y números
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            matricula = nuevoTexto
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Matrícula")
                    },
                    placeholder = {
                        Text("Ej. A01234567")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // BOTÓN INICIAR SESIÓN
                Button(
                    onClick = {

                        if (
                            correoUsuario.isNotBlank() &&
                            matricula.isNotBlank() &&
                            contrasena.isNotBlank()
                        ) {
                            onLoginSuccess()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
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

                    Text(
                        text = "¿No tienes cuenta? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Regístrate",
                        color = primaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            mostrarRegistro = true
                        }
                    )
                }
            }
        }
    }
}


// PANTALLA DE REGISTRO


@Composable
fun RegistroScreen(
    onBackClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFE9D4C3)
    val primaryColor = Color(0xFFBD0000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                // NOMBRE
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre completo")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CORREO
                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Correo electrónico")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // MATRÍCULA
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { nuevoTexto ->

                        // Solo letras y números
                        if (nuevoTexto.all { it.isLetterOrDigit() }) {
                            matricula = nuevoTexto
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Matrícula")
                    },
                    placeholder = {
                        Text("Ej. A01234567")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // CREAR CUENTA
                Button(
                    onClick = {

                        if (
                            nombre.isNotBlank() &&
                            correo.isNotBlank() &&
                            matricula.isNotBlank() &&
                            contrasena.isNotBlank()
                        ) {
                            onBackClick()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
                ) {

                    Text(
                        text = "Crear cuenta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onBackClick
                ) {

                    Text(
                        text = "Regresar al inicio de sesión",
                        color = primaryColor
                    )
                }
            }
        }
    }
}


// MODELOS DE DATOS


data class Persona(
    val nombre: String,
    val cargo: String,
    val telefono: String,
    val correo: String,
    val proyectoAsociado: String? = null
)

data class TareaCronograma(
    val nombre: String,
    val mesInicio: Int,
    val duracion: Int
)

data class Proyecto(
    val titulo: String,
    val ods: String,
    val objetivoPrincipal: String,
    val antecedentes: String,
    val justificacion: String,
    val objetivosEspecificos: String,
    val alcance: String,
    val descripcionGeneral: String,
    val recursosNecesarios: String,
    val cronograma: List<TareaCronograma>,
    val resultadosEsperados: String,
    val responsables: String
)

// LISTA DE PERSONAS


val listaPersonas = listOf(

    Persona(
        "Ana Torres",
        "Scouter",
        "+52 614 123 4567",
        "ana.torres@example.com"
    ),

    Persona(
        "Luis Ramírez",
        "Rover",
        "+52 614 234 5678",
        "luis.ramirez@example.com",
        "Reforestación Comunitaria"
    ),

    Persona(
        "Sofía Herrera",
        "Scouter",
        "+52 614 345 6789",
        "sofia.herrera@example.com"
    ),

    Persona(
        "Carlos Mendoza",
        "Rover",
        "+52 614 456 7890",
        "carlos.mendoza@example.com"
    ),

    Persona(
        "Valeria Nuñez",
        "Scouter",
        "+52 614 567 8901",
        "valeria.nunez@example.com",
        "Proyecto impulso para herramientas del futuro"
    )
)


// LISTA DE PROYECTOS


val listaProyectos = listOf(

    Proyecto(
        titulo = "Reforestación Comunitaria",

        ods = "ODS 13: Acción por el clima",

        objetivoPrincipal =
            "Promover la reforestación urbana y el cuidado activo de las áreas verdes como una estrategia clave para combatir el cambio climático desde nuestro entorno local.",

        antecedentes =
            "Deterioro de las áreas verdes urbanas y aumento de islas de calor en la ciudad.",

        justificacion =
            "La reforestación mejora la calidad del aire y crea refugios naturales para la fauna local.",

        objetivosEspecificos =
            "- Organizar jornadas comunitarias de plantación\n" +
                    "- Restauración de parques locales\n" +
                    "- Talleres de educación ambiental para familias",

        alcance =
            "Zonas urbanas y parques públicos de la comunidad local.",

        descripcionGeneral =
            "Plantación de árboles nativos y mantenimiento continuo de espacios públicos con participación vecinal.",

        recursosNecesarios =
            "Árboles nativos, herramientas, abono, agua y voluntarios comprometidos.",

        cronograma = listOf(
            TareaCronograma("Selección de sitios", 0, 1),
            TareaCronograma("Gestión de árboles", 0, 1),
            TareaCronograma("Jornada de plantación", 1, 1),
            TareaCronograma("Seguimiento", 2, 1)
        ),

        resultadosEsperados =
            "Reducción de temperatura ambiental y mayor conciencia ecológica en los vecinos.",

        responsables =
            "Luis Ramírez (Responsable)"
    ),

    Proyecto(
        titulo = "Proyecto impulso para herramientas del futuro",

        ods = "ODS 4: Educación de calidad / ODS 3: Salud y bienestar",

        objetivoPrincipal =
            "Fortalecer las habilidades personales, laborales y de autocuidado de los participantes mediante conferencias formativas.",

        antecedentes =
            "Niños y jóvenes no tienen acceso a espacios para desarrollar liderazgo y organización del tiempo.",

        justificacion =
            "El liderazgo y la organización son pilares para el éxito académico y laboral. Incluye prevención de salud.",

        objetivosEspecificos =
            "- Concientizar y prevenir el diabetes\n" +
                    "- Fomentar metodologías para gestión del tiempo\n" +
                    "- Identificar y saber llenar una solicitud de trabajo",

        alcance =
            "25 personas (12-40 años) en el Albergue San Vicente de Chihuahua. 2 días, 8 horas.",

        descripcionGeneral =
            "Conferencias sobre gestión del tiempo, liderazgo, empleabilidad y prevención de la diabetes.",

        recursosNecesarios =
            "Conferencistas (CELIP, Akam surá), Voluntarios, Proyector, Laptop, Papelería.",

        cronograma = listOf(
            TareaCronograma("Primer contacto", 0, 1),
            TareaCronograma("Organización", 0, 2),
            TareaCronograma("Conferencias", 1, 1),
            TareaCronograma("Evaluación", 1, 1)
        ),

        resultadosEsperados =
            "Participantes con herramientas de empleabilidad y mayor conciencia de salud.",

        responsables =
            "Dafne Pineda, Hiro Ogawa"
    )
)



// PANTALLA PRINCIPAL


@Composable
fun MainScreen() {

    var selectedTab by remember { mutableStateOf(0) }

    var selectedProject by remember {
        mutableStateOf<Proyecto?>(null)
    }

    val backgroundColor = Color(0xFFE9D4C3)
    val backgroundNav = Color(0xFFBD0000)
    val titleColor = Color(0xFFFF6347)

    Scaffold(

        bottomBar = {

            NavigationBar(
                containerColor = backgroundNav
            ) {

                NavigationBarItem(

                    selected = selectedTab == 0,

                    onClick = {
                        selectedTab = 0
                        selectedProject = null
                    },

                    icon = {
                        Icon(
                            Icons.Filled.Book,
                            contentDescription = "Inicio"
                        )
                    },

                    label = {
                        Text(
                            "Inicio",
                            color = Color.White
                        )
                    },

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
                        selectedProject = null
                    },

                    icon = {
                        Icon(
                            Icons.Filled.Campaign,
                            contentDescription = "Proyectos"
                        )
                    },

                    label = {
                        Text(
                            "Proyectos",
                            color = Color.White
                        )
                    },

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

        Box(
            modifier = Modifier.padding(innerPadding)
        ) {

            when (selectedTab) {


                // INICIO


                0 -> {

                    LazyColumn(

                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )

                    ) {

                        items(listaPersonas) { persona ->

                            ProfileCard(

                                persona = persona,

                                nameColor = titleColor,

                                onProjectClick = { proyectoTitulo ->

                                    val proyecto =
                                        listaProyectos.find {
                                            it.titulo == proyectoTitulo
                                        }

                                    if (proyecto != null) {

                                        selectedProject = proyecto
                                        selectedTab = 1

                                    }
                                }
                            )
                        }
                    }
                }



                // PROYECTOS

                1 -> {

                    if (selectedProject == null) {

                        LazyColumn(

                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundColor)
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )

                        ) {

                            item {

                                Text(
                                    text = "Lista de proyectos",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBD0000),
                                    modifier = Modifier.padding(
                                        bottom = 16.dp,
                                        top = 8.dp
                                    )
                                )
                            }

                            items(listaProyectos) { proyecto ->

                                ProjectCard(proyecto) {

                                    selectedProject = proyecto

                                }
                            }
                        }

                    } else {

                        ProjectDetailScreen(

                            proyecto = selectedProject!!,

                            onBackClick = {
                                selectedProject = null
                            }
                        )
                    }
                }
            }
        }
    }
}



// TARJETA DE PROYECTO


@Composable
fun ProjectCard(
    proyecto: Proyecto,
    onClick: () -> Unit
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = proyecto.titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = proyecto.ods,
                fontSize = 15.sp,
                color = Color(0xFF666666)
            )

            Text(
                text = "Responsable: ${proyecto.responsables}",
                fontSize = 15.sp,
                color = Color(0xFF666666)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Objetivo: ${proyecto.objetivoPrincipal}",
                fontSize = 15.sp,
                color = Color(0xFF444444),
                lineHeight = 20.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(

                onClick = onClick,

                shape = RoundedCornerShape(20.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5B091),
                    contentColor = Color(0xFF4A2C2A)
                ),

                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 8.dp
                )

            ) {

                Text(
                    text = "Ver detalles",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// TARJETA DE PERSONA


@Composable
fun ProfileCard(
    persona: Persona,
    nameColor: Color,
    onProjectClick: (String) -> Unit = {}
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
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
                    .background(Color.Gray),

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    imageVector = Icons.Filled.Person,

                    contentDescription = null,

                    modifier = Modifier.size(40.dp),

                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column {

                Row {

                    Text(
                        text = persona.nombre,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = persona.cargo,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ContactItem(
                    icon = Icons.Filled.Phone,
                    text = persona.telefono
                )

                ContactItem(
                    icon = Icons.Filled.Email,
                    text = persona.correo
                )

                persona.proyectoAsociado?.let { proyecto ->

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(

                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier.clickable {
                            onProjectClick(proyecto)
                        }

                    ) {

                        Icon(

                            imageVector = Icons.Filled.Campaign,

                            contentDescription = null,

                            modifier = Modifier.size(16.dp),

                            tint = Color(0xFFBD0000)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(

                            text = "Proyecto: $proyecto",

                            fontSize = 13.sp,

                            color = Color(0xFFBD0000),

                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            modifier = Modifier.size(16.dp),

            tint = Color.DarkGray
        )

        Spacer(
            modifier = Modifier.width(6.dp)
        )

        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.DarkGray
        )
    }
}