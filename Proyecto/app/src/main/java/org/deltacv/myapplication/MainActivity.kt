package org.deltacv.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.deltacv.myapplication.ui.ProjectDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

// Modelo de datos
data class Persona(
    val nombre: String,
    val cargo: String,
    val telefono: String,
    val correo: String
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
    val cronograma: String,
    val resultadosEsperados: String,
    val responsables: String
)

// Lista de ejemplo con 5 personas
val listaPersonas = listOf(
    Persona("Ana Torres", "Scouter", "+52 614 123 4567", "ana.torres@example.com"),
    Persona("Luis Ramírez", "Rover", "+52 614 234 5678", "luis.ramirez@example.com"),
    Persona("Sofía Herrera", "Scouter", "+52 614 345 6789", "sofia.herrera@example.com"),
    Persona("Carlos Mendoza", "Rover", "+52 614 456 7890", "carlos.mendoza@example.com"),
    Persona("Valeria Nuñez", "Scouter", "+52 614 567 8901", "valeria.nunez@example.com")
)

val proyectoEjemplo = Proyecto(
    titulo = "Limpieza del Río Chuvíscar",
    ods = "13. Acción por el clima",
    objetivoPrincipal = "Reducir la contaminación por plásticos en el lecho del río.",
    antecedentes = "Aumento de residuos sólidos observado en las últimas lluvias.",
    justificacion = "La contaminación afecta la fauna local y la imagen urbana.",
    objetivosEspecificos = "- Recolectar 500kg de basura\n- Concientizar a 200 vecinos",
    alcance = "Tramo entre la calle 10 y la calle 20.",
    descripcionGeneral = "Jornadas de limpieza los fines de semana con voluntarios.",
    recursosNecesarios = "Bolsas, guantes, rastrillos, camión de basura.",
    cronograma = "Septiembre - Noviembre 2026",
    resultadosEsperados = "Río libre de basura visible y mayor compromiso vecinal.",
    responsables = "Sebastian (Coordinador) - contacto@ejemplo.com"
)

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableStateOf(0) }
    val backgroundColor = Color(0xFFE9D4C3)
    val backgroundNav = Color(0xFFBD0000)
    val titleColor = Color(0xFFFF6347)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = backgroundNav) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
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
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
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
            when (selectedItem) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(listaPersonas) { persona ->
                            ProfileCard(persona, titleColor)
                        }
                    }
                }
                1 -> {
                    ProjectDetailScreen(
                        proyecto = proyectoEjemplo,
                        onBackClick = { selectedItem = 0 }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileCard(persona: Persona, nameColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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

            Spacer(modifier = Modifier.width(16.dp))

            // Columna derecha: nombre, cargo, contacto
            Column {
                Row{
                    Text(
                        text = persona.nombre,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = persona.cargo,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ContactItem(icon = Icons.Filled.Phone, text = persona.telefono)
                ContactItem(icon = Icons.Filled.Email, text = persona.correo)
            }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = Color.DarkGray)
    }
}