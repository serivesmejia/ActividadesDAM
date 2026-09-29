package com.example.joyeriadinamita

data class Product(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val image: Int,
    val comments: MutableList<Comment> = mutableListOf()
)

data class Comment(
    val title: String,
    val content: String
)

val sampleProducts = listOf(
    Product("1", "Anillo de Oro", "$1,200",
        "Anillo elaborado en oro puro con un acabado pulido y brillante, ideal para ocasiones especiales o como un símbolo de elegancia atemporal.",
        R.drawable.anillo),
    Product("2", "Collar de Plata", "$850",
        "Diseño sofisticado en plata de ley 925, perfecto para realzar cualquier atuendo diario con un toque de brillo sutil y moderno.",
        R.drawable.collar),

    Product("3", "Pulsera de Diamantes", "$3,500",
        "Exclusiva pieza fina incrustada con diamantes de talla brillante, diseñada para destacar con un lujo incomparable en eventos de gala.",
        R.drawable.pulsera_diamantes_en_oro_blanco__scaled),

    Product("4", "Reloj Elegante", "$2,100",
        "Reloj de precisión con correa de alta calidad y carátula minimalista, la combinación perfecta entre funcionalidad y distinción.",
        R.drawable.reloj),

    Product("5", "Pendientes de Perlas", "$600",
        "Pendientes clásicos con perlas cultivadas de tono nácar radiante, que aportan dulzura, frescura y elegancia al rostro.",
        R.drawable.pendientes),

    Product("6", "Brazalete de Cuero", "$450",
        "Brazalete de cuero genuino con detalles en metal, pensado para un estilo urbano, moderno y de gran durabilidad.",
        R.drawable.brazalete)
)
