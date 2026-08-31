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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.deltacv.myapplication.Proyecto
import org.deltacv.myapplication.TareaCronograma

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    proyecto: Proyecto,
    onBackClick: () -> Unit
) {
    val backgroundColor = Color(0xFFFDF6F0) // Beige más suave
    val titleColor = Color(0xFFBD0000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de proyecto", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFBD0000),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = proyecto.titulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sección 1: ODS y Objetivos
            CollapsibleSection(
                title = "ODS y Objetivos",
                backgroundColor = Color(0xFFD1E8E2) // Verde/Azul suave
            ) {
                ProjectField("ODS que está trabajando", proyecto.ods)
                ProjectField("Objetivo principal", proyecto.objetivoPrincipal)
                ProjectField("Objetivos específicos", proyecto.objetivosEspecificos)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 2: Contexto
            CollapsibleSection(
                title = "Contexto",
                backgroundColor = Color(0xFFFFF2CC) // Amarillo suave
            ) {
                ProjectField("Antecedentes", proyecto.antecedentes)
                ProjectField("Justificación", proyecto.justificacion)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 3: Planificación
            CollapsibleSection(
                title = "Planificación",
                backgroundColor = Color(0xFFE1D5E7) // Morado suave
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
                backgroundColor = Color(0xFFD5E8D4) // Verde suave
            ) {
                ProjectField("Resultados esperados", proyecto.resultadosEsperados)
                ProjectField("Responsables y formas de contacto", proyecto.responsables)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBD0000))
            ) {
                Text("Regresar a la lista de proyectos", color = Color.White)
            }
        }
    }
}

@Composable
fun ScheduleChart(tareas: List<TareaCronograma>) {
    // Determinamos los labels basándonos en si hay tareas en meses posteriores
    val maxMes = tareas.maxOfOrNull { it.mesInicio + it.duracion } ?: 3
    val meses = if (maxMes <= 2) listOf("Jul", "Ago") else listOf("Sep", "Oct", "Nov")
    val numColumnas = meses.size
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Cabecera de meses
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

        // Filas de tareas
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
                    color = Color.Black
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
                                    .background(Color.White.copy(alpha = 0.3f))
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
                                .background(Color(0xFFBD0000).copy(alpha = 0.6f))
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
