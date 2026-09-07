package org.deltacv.actividad2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

            // Variable que controla el modo oscuro
            var isDarkMode by remember {
                mutableStateOf(false)
            }

            Actividad2Theme(
                darkTheme = isDarkMode
            ) {

                val navController = rememberNavController()

                val scope = rememberCoroutineScope()

                val currentBackStackEntry by
                navController.currentBackStackEntryAsState()

                // Color de fondo
                val background = if (isDarkMode) {
                    Color(0xFF121212)
                } else {
                    MaterialTheme.colorScheme.background
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {

                    NavHost(
                        navController = navController,
                        startDestination = NewsListRoute,

                        modifier = Modifier
                            .fillMaxSize()
                            .background(background),

                        // Animación al entrar
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(
                                    durationMillis = 400
                                )
                            )
                        },

                        // Animación al salir
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(
                                    durationMillis = 400
                                )
                            )
                        },

                        // Animación al regresar
                        popEnterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(
                                    durationMillis = 400
                                )
                            )
                        },

                        // Animación al salir regresando
                        popExitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(
                                    durationMillis = 400
                                )
                            )
                        }

                    ) {

                        // Pantalla principal
                        composable<NewsListRoute> {

                            NewsScreen(

                                // Enviamos el estado del modo oscuro
                                isDarkMode = isDarkMode,

                                // Cambiar entre claro y oscuro
                                onToggleDarkMode = {
                                    isDarkMode = !isDarkMode
                                },

                                // Cuando seleccionamos una noticia
                                onArticleClick = { article ->

                                    // Evitar múltiples navegaciones
                                    val isAtRoot =
                                        currentBackStackEntry
                                            ?.destination
                                            ?.route
                                            ?.contains("NewsListRoute") == true

                                    if (isAtRoot) {

                                        scope.launch {

                                            // Pequeña pausa para mostrar el tap
                                            delay(150)

                                            // Comprobar nuevamente antes de navegar
                                            if (
                                                navController
                                                    .currentBackStackEntry
                                                    ?.destination
                                                    ?.route
                                                    ?.contains("NewsListRoute") == true
                                            ) {

                                                navController.navigate(
                                                    NewsDetailRoute(article.id)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // Pantalla de detalle
                        composable<NewsDetailRoute> { backStackEntry ->

                            val route: NewsDetailRoute =
                                backStackEntry.toRoute()

                            val article =
                                articles.find {
                                    it.id == route.articleId
                                }

                            if (article != null) {

                                NewsDetailScreen(
                                    article = article,

                                    onBack = {

                                        if (
                                            currentBackStackEntry
                                                ?.destination
                                                ?.route
                                                ?.contains("NewsDetailRoute") == true
                                        ) {

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
}