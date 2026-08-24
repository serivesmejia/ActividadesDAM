package org.deltacv.actividad2

data class NewsArticle(val id: Int, val title: String, val body: String, val imageRes: Int)

val articles = listOf(
    NewsArticle(
        1,
        "\"SPIDER-MAN: UN NUEVΟ DIA\" ΜΑΝΤΙΕΝΕ SU ÉXITO Y ACUMULA MÁS DE 850 MDD A UN MES DE SU ESTRENO",
        """
            "Spider-Man: Un Nuevo Día" se impuso a una oleada de recién llegadas, entre ellas una comedia, una franquicia de terror, una película de acción de Jason Statham y una cinta familiar, para mantener el primer puesto en taquilla en su cuarto fin de semana en salas.

            "La Odisea" también se convirtió en la película más taquillera de Christopher Nolan hasta la fecha, tanto en el mercado nacional como en el internacional.

            En lo que va del año, "La Odisea" sólo está por detrás de "Spider-Man: Un Nuevo Día", que sumó 39 millones de dólares en 4 mil 006 salas de Estados Unidos y Canadá, según estimaciones de los estudios publicadas el domingo.

            La nueva película de Tom Holland, que ya ha recaudado más de 854.9 millones de dólares en Norteamérica, pronto superará a "Avengers: Endgame" para convertirse en el segundo estreno nacional más taquillero de todos los tiempos.

            El récord lo ostenta actualmente "Star Wars: El despertar de la fuerza" (con 936.7 millones de dólares).
            Sony también tuvo la segunda película más grande del fin de semana con "Noche del demonio: está entre nosotros". La sexta entrega de la franquicia, producida por Blumhouse Atomic Monster, recaudó 25.3 millones de dólares en 3 mil 303 salas.

            En el mercado internacional, obtuvo 35 millones de dólares. Su presupuesto de producción de 18 millones de dólares la convierte en la película más cara de la serie.

            El tercer lugar fue para "La Odisea", que a principios de esta semana se convirtió en la película con clasificación R más taquillera de todos los tiempos, al superar a "Deadpool & Wolverine".

            Sumó 19.5 millones de dólares en su sexto fin de semana en salas de Norteamérica, donde sigue exhibiéndose en más de 3 mil ubicaciones, lo que elevó su total nacional a poco menos de 539.1 millones de dólares. Eso la impulsa por encima de los 534,9 millones de dólares de "The Dark Knight" -un anterior punto máximo para Nolan- sin ajustar por inflación.

            En el mercado internacional, añadió 68 millones de dólares, impulsada en gran medida por China y Corea, lo que lleva su total mundial a mil 400 millones de dólares.

            Las otras grandes novedades quedaron más abajo en la lista. "Mutiny", protagonizada por Statham como un hombre acusado injustamente de asesinar a su jefe multimillonario, debutó en el sexto puesto con 7.5 millones de dólares en 2 mil 703 ubicaciones.

            El estreno de Lionsgate (una adquisición) estaba pensado para ser exclusivamente en cines, pero unos días antes de su lanzamiento estuvo disponible por error y gratis en Prime Video durante unas horas, hasta que se corrigió la equivocación.

            El fallo pudo haber afectado sus ingresos del fin de semana de estreno, pero la película también tuvo en contra algunas críticas bastante malas.

            Con reseñas aún peores quedó "Spa Weekend", una comedia de viaje de amigas de los hombres detrás de "The Hangover" y "Bad Moms", protagonizada por Isla Fisher, Leslie Mann, Anna Faris y Michelle Buteau. Recaudó unos 3.1 millones de dólares en 2 mil 009 ubicaciones.

            Las mujeres representaron alrededor del 84% de los compradores de boletos durante el fin de semana de estreno, según PostTrak. El reestreno por el 25° aniversario de "Rápido y Furiosos" recaudó ligeramente más.
            "The Magic Faraway Tree", protagonizada por Andrew Garfield, se estrenó fuera del top 10 con 1.4 millones de dólares en mil 11 pantallas.

            La película sobre el joven Anthony Bourdain, "Tony", sin embargo, sigue funcionando bastante bien, al recaudar 5 millones de dólares en mil 652 pantallas.

            La temporada de verano, que se extiende hasta el Día del Trabajo, ya ha generado una cifra estimada de 4 mil 400 millones de dólares en ventas de boletos en Norteamérica, lo que la sitúa entre los cuatro veranos más taquilleros de la historia.

            "Estamos en la recta final de lo que creo que pasará a la historia de la taquilla como quizá una de las temporadas de cine de verano más clave y decisivas de todas", manifestó Paul Dergarabedian, responsable de tendencias del mercado en Rentrak. "No sé cómo se puede replicar este verano. Los estudios lo intentarán".

            Michael O'Leary, presidente y director ejecutivo de la organización comercial de salas de cine Cinema United, destacó la amplia variedad de películas en el mercado, que ha contribuido a impulsar un gran verano.

            Parte de ello, le explicó a The Associated Press, es que el flujo de producciones de la industria por fin se está recuperando de los efectos de las huelgas de Hollywood en 2023.

            "Lo que más me entusiasma, francamente, es el entusiasmo de los cinéfilos", expresó O'Leary. "Lo sientes cuando vas al cine. Hay una energía en estar en la sala que no estaba presente en el pasado reciente".

            El año en general también ha superado los 7 mil millones de dólares, lo que supone un aumento de alrededor del 20.4% respecto de 2025, pero está un 7.1% por detrás de 2019.
        """.trimIndent(),
        R.drawable.spiderman
    ),

    NewsArticle(2, "Jornada Nacional de Reforestación", """
        🌳 Desde #CiudadMadero nos sumamos a la Jornada Nacional de Reforestación, convocada por nuestra presidenta, Dra. Claudia Sheinbaum Pardo, y nuestro gobernador, Dr. Américo Villarreal Anaya.

        Junto a mi esposa, Dunia Marón, participamos en esta jornada, porque con la siembra de árboles contribuimos al cuidado del medio ambiente y, al mismo tiempo, sembramos futuro para las nuevas generaciones.

        🌱 Sumemos esfuerzos para cuidar, proteger y dar seguimiento a cada árbol que hoy plantamos. ¡Juntos construimos un mejor futuro para nuestra ciudad!
    """.trimIndent(), R.drawable.reforestar),

    NewsArticle(3, "OPERADOR CONDUCE ENTRE HUMO Y FUEGO PARA CONTENER UN INCENDIO QUE AMENAZA VIVIENDAS", """
        Un operador de maquinaria pesada abrió un cortafuegos entre un espeso humo naranja y llamas a pocos metros, en un intento por proteger zonas residenciales pese al riesgo evidente
        La escena, registrada en video, ocurre mientras un incendio forestal avanza este domingo en las afueras de Reno, Nevada (EE.UU.), ya consumió más de 4.250 hectáreas y obligó a emitir órdenes de evacuación ante la amenaza a cientos de hogares.
    """.trimIndent(), R.drawable.humoyfuego)
)
