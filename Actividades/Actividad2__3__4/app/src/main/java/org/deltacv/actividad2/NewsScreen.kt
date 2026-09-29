package org.deltacv.actividad2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val commentDao = remember { AppDatabase.getDatabase(context).commentDao() }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (selectedTab == 0) "Noticias Dinamita" else "Favoritos")
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Text(text = if (isDarkMode) "☀️" else "🌙")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoStories, contentDescription = null)
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = Color.Red
                            )
                        }
                    },
                    label = { Text("Favoritos") }
                )
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    HeroHeader()
                }
                items(articles) { article ->
                    val isLiked = remember(article.id) { 
                        mutableStateOf(commentDao.isArticleLiked(article.id) ?: false)
                    }
                    
                    NewsCard(
                        article = article,
                        isLiked = isLiked.value,
                        onLikeToggle = {
                            val nextState = !isLiked.value
                            isLiked.value = nextState
                            commentDao.insertOrUpdateLike(NewsLike(article.id, nextState))
                        },
                        onClick = {
                            onArticleClick(article)
                        }
                    )
                }
            }
        } else {
            // Pantalla de Favoritos
            var updateTrigger by remember { mutableIntStateOf(0) }
            val likedArticles = remember(selectedTab, updateTrigger) {
                articles.filter { article ->
                    commentDao.isArticleLiked(article.id) == true
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (likedArticles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no tienes noticias favoritas",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(likedArticles, key = { it.id }) { article ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                                    commentDao.insertOrUpdateLike(NewsLike(article.id, false))
                                    updateTrigger++
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color.Red.copy(alpha = 0.2f)
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                                val alignment = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .background(color, shape = RoundedCornerShape(12.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    if (dismissState.dismissDirection != SwipeToDismissBoxValue.Settled) {
                                        Text(
                                            text = "💔",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }
                        ) {
                            NewsCard(
                                article = article,
                                isLiked = true,
                                onLikeToggle = {
                                    commentDao.insertOrUpdateLike(NewsLike(article.id, false))
                                    updateTrigger++
                                },
                                onClick = {
                                    onArticleClick(article)
                                }
                            )
                        }
                    }
                }
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
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
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
    isLiked: Boolean,
    onLikeToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = article.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Column {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.body,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onLikeToggle) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else LocalContentColor.current
                )
            }
        }
    }
}

@Preview(showBackground = true)
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
