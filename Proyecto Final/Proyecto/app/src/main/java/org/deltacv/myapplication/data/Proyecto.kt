package org.deltacv.myapplication.data

data class Proyecto(
    val id: String = "",
    val titulo: String = "",
    val ods: String = "",
    val objetivoGeneral: String = "",
    val antecedentes: String = "",
    val justificacion: String = "",
    val objetivosEspecificos: String = "",
    val alcance: String = "",
    val descripcionGen: String = "",
    val recursos: String = "",
    val resultados: String = "",
    val responsablesIds: List<String> = emptyList(),
    val voluntariosIds: List<String> = emptyList()
)
