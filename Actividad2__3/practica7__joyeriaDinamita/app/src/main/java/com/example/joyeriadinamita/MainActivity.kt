package com.example.joyeriadinamita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joyeriadinamita.ui.theme.JoyeriaDinamitaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoyeriaDinamitaTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen(onProductClick = { productId ->
                navController.navigate("details/$productId")
            })
        }
        composable("details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = sampleProducts.find { it.id == productId }
            if (product != null) {
                DetailsScreen(product)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onProductClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Joyería Dinamita", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleProducts) { product ->
                ProductCard(product, onClick = { onProductClick(product.id) })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = product.price, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(product: Product) {
    var inputTitle by remember { mutableStateOf("") }
    var inputContent by remember { mutableStateOf("") }
    val comments = remember(product.id) {
        mutableStateListOf<Comment>().apply { addAll(product.comments) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(product.title) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = product.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = product.description, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // Input fields
                OutlinedTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputContent,
                    onValueChange = { inputContent = it },
                    label = { Text("Comentario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Double height roughly
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (inputTitle.isNotBlank() && inputContent.isNotBlank()) {
                            val newComment = Comment(inputTitle, inputContent)
                            comments.add(0, newComment)
                            product.comments.add(0, newComment)
                            inputTitle = ""
                            inputContent = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Enviar", color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(comments) { comment ->
                CommentItem(comment)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0))
            .padding(12.dp)
    ) {
        Text(text = comment.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = comment.content, fontSize = 14.sp)
    }
}
