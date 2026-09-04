package com.example.joyeriadinamita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joyeriadinamita.ui.theme.JoyeriaDinamitaTheme

// Paleta de colores lujosa
private val GoldPrimary = Color(0xFFC5A059)
private val GoldLight = Color(0xFFE8D5B5)
private val DarkLuxury = Color(0xFF1C1A17)
private val BackgroundWarm = Color(0xFFFAF8F5)
private val CardBackground = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF6E6A63)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            JoyeriaDinamitaTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                
                // Colores calculados según el modo
                val background = if (isDarkMode) Color(0xFF121212) else BackgroundWarm
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    AppNavigation(
                        navController = navController,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { isDarkMode = !isDarkMode }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    NavHost(navController = navController, startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onProductClick = { productId ->
                    navController.navigate("details/$productId")
                }
            )
        }
        composable("details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = sampleProducts.find { it.id == productId }
            if (product != null) {
                DetailsScreen(
                    product = product,
                    isDarkMode = isDarkMode,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onProductClick: (String) -> Unit
) {
    // Colores dinámicos
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else BackgroundWarm
    val textColor = if (isDarkMode) Color(0xFFFAF8F5) else DarkLuxury
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else CardBackground

    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "themeRotation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDarkMode) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "themeScale"
    )

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Joyería Dinamita",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.2.sp,
                        color = textColor
                    )
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Cambiar tema",
                            tint = if (isDarkMode) GoldPrimary else DarkLuxury,
                            modifier = Modifier.graphicsLayer(
                                rotationZ = rotation,
                                scaleX = scale,
                                scaleY = scale
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
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
                ProductCard(
                    product = product,
                    cardColor = cardColor,
                    textColor = textColor,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    cardColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = product.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.price,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    product: Product,
    isDarkMode: Boolean,
    onBack: () -> Unit
) {
    var inputTitle by remember { mutableStateOf("") }
    var inputContent by remember { mutableStateOf("") }
    
    // Colores dinámicos
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else BackgroundWarm
    val textColor = if (isDarkMode) Color(0xFFFAF8F5) else DarkLuxury
    val secondaryTextColor = if (isDarkMode) Color(0xFFA09C96) else TextSecondary
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else CardBackground
    
    // Estado del botón de favorito con animación
    var isFavorite by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favoriteScale"
    )

    val comments = remember(product.id) {
        mutableStateListOf<Comment>().apply { addAll(product.comments) }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                },
                title = {
                    Text(
                        text = product.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else GoldPrimary,
                            modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = product.title,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = secondaryTextColor,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Input fields estilizados
                OutlinedTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedLabelColor = GoldPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = inputContent,
                    onValueChange = { inputContent = it },
                    label = { Text("Comentario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedLabelColor = GoldPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
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
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Enviar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (comments.isNotEmpty()) {
                    Text(
                        text = "Reseñas y Comentarios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(comments) { comment ->
                CommentItem(comment, cardColor, textColor, secondaryTextColor)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    cardColor: Color,
    textColor: Color,
    secondaryTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .padding(14.dp)
    ) {
        Text(
            text = comment.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comment.content,
            fontSize = 13.sp,
            color = secondaryTextColor,
            lineHeight = 18.sp
        )
    }
}