package com.example.joyeriadinamita

data class Product(
    val id: String,
    val title: String,
    val price: String,
    val description: String = "Esta es una descripción detallada del producto que ocupa espacio en la pantalla."
)

data class Comment(
    val title: String,
    val content: String
)

val sampleProducts = listOf(
    Product("1", "Anillo de Oro", "$1,200"),
    Product("2", "Collar de Plata", "$850"),
    Product("3", "Pulsera de Diamantes", "$3,500"),
    Product("4", "Reloj Elegante", "$2,100"),
    Product("5", "Pendientes de Perlas", "$600"),
    Product("6", "Brazalete de Cuero", "$450")
)
