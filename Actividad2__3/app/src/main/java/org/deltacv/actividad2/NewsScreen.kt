package org.deltacv.actividad2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.deltacv.actividad2.ui.theme.Actividad2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onArticleClick: (NewsArticle) -> Unit
) {

    Scaffold(

        modifier = Modifier.fillMaxSize(),

        // Barra superior
        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Noticias Dinamita"
                    )
                },

                actions = {

                    // Botón para cambiar el modo
                    IconButton(
                        onClick = onToggleDarkMode
                    ) {

                        Text(
                            text = if (isDarkMode) {
                                "☀️"
                            } else {
                                "🌙"
                            }
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            contentPadding = PaddingValues(
                bottom = 16.dp
            )
        ) {

            // Encabezado
            item {
                HeroHeader()
            }

            // Lista de noticias
            items(articles) { article ->

                NewsCard(
                    article = article,

                    onClick = {
                        onArticleClick(article)
                    }
                )
            }
        }
    }
}


@Composable
fun HeroHeader() {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),

        contentAlignment = Alignment.BottomStart
    ) {

        // Espacio decorativo
        Box(

            modifier = Modifier
                .fillMaxHeight(0.2f)
                .background(
                    MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.2f
                    )
                )
        )

        Text(

            text = "Noticias Dinamita",

            style = MaterialTheme.typography.displaySmall,

            fontStyle = FontStyle.Italic,

            modifier = Modifier.padding(24.dp),

            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@Composable
fun NewsCard(
    article: NewsArticle,
    onClick: () -> Unit
) {

    Card(

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        shape = RoundedCornerShape(12.dp)
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // Imagen de la noticia
            Image(

                painter = painterResource(
                    id = article.imageRes
                ),

                contentDescription = null,

                modifier = Modifier
                    .size(100.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),

                contentScale = ContentScale.Crop
            )

            Column(

                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {

                // Título
                Text(

                    text = article.title,

                    style = MaterialTheme.typography.titleMedium,

                    maxLines = 2,

                    overflow = TextOverflow.Ellipsis,

                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // Descripción
                Text(

                    text = article.body,

                    style = MaterialTheme.typography.bodySmall,

                    maxLines = 3,

                    overflow = TextOverflow.Ellipsis,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
fun NewsScreenPreview() {

    Actividad2Theme {

        NewsScreen(

            isDarkMode = false,

            onToggleDarkMode = {},

            onArticleClick = {}
        )
    }
}