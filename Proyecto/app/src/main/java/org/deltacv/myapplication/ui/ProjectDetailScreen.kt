package org.deltacv.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.deltacv.myapplication.Proyecto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    proyecto: Proyecto,
    onBackClick: () -> Unit
) {
    val backgroundColor = Color(0xFFE9D4C3)
    val titleColor = Color(0xFFFF6347)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de proyecto - Sebastian", fontSize = 18.sp) },
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
            ProjectField("Título", proyecto.titulo, titleColor)
            ProjectField("ODS que está trabajando", proyecto.ods, titleColor)
            ProjectField("Objetivo principal", proyecto.objetivoPrincipal, titleColor)
            ProjectField("Antecedentes", proyecto.antecedentes, titleColor)
            ProjectField("Justificación", proyecto.justificacion, titleColor)
            ProjectField("Objetivos específicos", proyecto.objetivosEspecificos, titleColor)
            ProjectField("Alcance", proyecto.alcance, titleColor)
            ProjectField("Descripción general del proyecto", proyecto.descripcionGeneral, titleColor)
            ProjectField("Recursos necesarios", proyecto.recursosNecesarios, titleColor)
            ProjectField("Cronograma", proyecto.cronograma, titleColor)
            ProjectField("Resultados esperados", proyecto.resultadosEsperados, titleColor)
            ProjectField("Responsables y formas de contacto", proyecto.responsables, titleColor)

            Spacer(modifier = Modifier.height(16.dp))

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
fun ProjectField(label: String, value: String, labelColor: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = labelColor
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray)
    }
}
