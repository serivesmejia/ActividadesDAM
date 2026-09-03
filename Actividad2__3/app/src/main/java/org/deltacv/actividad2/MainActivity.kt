package org.deltacv.actividad2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.deltacv.actividad2.ui.theme.Actividad2Theme

@Serializable
object NewsListRoute

@Serializable
data class NewsDetailRoute(val articleId: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Actividad2Theme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()

                NavHost(
                    navController = navController,
                    startDestination = NewsListRoute,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(durationMillis = 400)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(durationMillis = 400)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(durationMillis = 400)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(durationMillis = 400)
                        )
                    }
                ) {
                    composable<NewsListRoute> {
                        NewsScreen(onArticleClick = { article ->
                            // Evitar múltiples navegaciones
                            val isAtRoot = currentBackStackEntry?.destination?.route?.contains("NewsListRoute") == true
                            if (isAtRoot) {
                                scope.launch {
                                    delay(150) // Pausa para ver el tap (ripple)
                                    // Volvemos a checar antes de navegar por si acaso
                                    if (navController.currentBackStackEntry?.destination?.route?.contains("NewsListRoute") == true) {
                                        navController.navigate(NewsDetailRoute(article.id))
                                    }
                                }
                            }
                        })
                    }
                    composable<NewsDetailRoute> { backStackEntry ->
                        val route: NewsDetailRoute = backStackEntry.toRoute()
                        val article = articles.find { it.id == route.articleId }
                        if (article != null) {
                            NewsDetailScreen(
                                article = article,
                                onBack = {
                                    if (currentBackStackEntry?.destination?.route?.contains("NewsDetailRoute") == true) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
