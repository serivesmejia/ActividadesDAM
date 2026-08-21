package org.deltacv.actividad2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.deltacv.actividad2.ui.theme.Actividad2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Actividad2Theme {
                NewsScreen()
            }
        }
    }
}

data class NewsArticle(val id: Int, val title: String)

@Composable
fun NewsScreen() {
    val articles = listOf(
        NewsArticle(1, "Modern Android Development: What's New?"),
        NewsArticle(2, "Jetpack Compose: Building Beautiful UIs"),
        NewsArticle(3, "The Future of Kotlin in 2024"),
        NewsArticle(4, "Mastering State Management in Compose"),
        NewsArticle(5, "Top 10 Android Libraries for Developers"),
        NewsArticle(6, "The Rise of Multiplatform Development"),
        NewsArticle(7, "Advanced Coroutines: Tips and Tricks")
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                NewsCard(article)
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
        // Placeholder for a high-quality Hero Image
        Box(
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        )
        Text(
            text = "Daily News Roundup",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(24.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun NewsCard(article: NewsArticle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image Placeholder - In a real app, use AsyncImage from Coil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            )
            // Title at the bottom of the card
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    Actividad2Theme {
        NewsScreen()
    }
}
