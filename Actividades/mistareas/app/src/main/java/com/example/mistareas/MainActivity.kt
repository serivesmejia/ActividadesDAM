package com.example.mistareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TodoScreen()
            }
        }
    }

data class Task(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(id = 1, text = "Bañar al perro", isCompleted = true),
                Task(id = 2, text = "Comprar comestibles para la cena", isCompleted = false),
                Task(id = 3, text = "Llamar a mamá", isCompleted = false)
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var editingTaskId by remember { mutableStateOf<Long?>(null) }

    val onSaveTask = {
        if (inputText.isNotBlank()) {
            if (editingTaskId != null) {
                tasks = tasks.map { if (it.id == editingTaskId) it.copy(text = inputText) else it }
                editingTaskId = null
            } else {
                tasks = tasks + Task(text = inputText)
            }
            inputText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MIS TAREAS", style = MaterialTheme.typography.headlineMedium) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(if (editingTaskId != null) "Editar tarea..." else "Escribir tarea...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onSaveTask() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = if (editingTaskId != null) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Guardar"
                    )
                }
            }

            Text(
                text = "<- DESLIZAR PARA ELIMINAR",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                tasks = tasks.filter { it.id != task.id }
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    else -> Color.Transparent
                                }, label = "backgroundColor"
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color, shape = RoundedCornerShape(12.dp))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        },
                        content = {
                            TaskRow(
                                task = task,
                                onToggleComplete = {
                                    tasks = tasks.map {
                                        if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it
                                    }
                                },
                                onEdit = {
                                    editingTaskId = task.id
                                    inputText = task.text
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            Text(
                text = task.text,
                modifier = Modifier.weight(1f),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted) Color.Gray else Color.Unspecified
            )
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.Gray
                )
            }
        }
    }
}}
