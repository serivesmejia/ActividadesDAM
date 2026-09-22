package org.deltacv.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val usuario: String,
    val correo: String,
    val cum: String,
    val contrasena: String,
    val telefono: String = "S/N",
    val cargo: String = "S/N",
    val avatarUrl: String? = null
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("nombre", nombre)
            put("usuario", usuario)
            put("correo", correo)
            put("cum", cum)
            put("contrasena", contrasena)
            put("telefono", telefono)
            put("cargo", cargo)
            put("avatarUrl", avatarUrl ?: "")
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): User {
            return User(
                id = json.optString("id", UUID.randomUUID().toString()),
                nombre = json.optString("nombre", "S/N"),
                usuario = json.optString("usuario", "S/N"),
                correo = json.optString("correo", "S/N"),
                cum = json.optString("cum", "S/N"),
                contrasena = json.optString("contrasena", ""),
                telefono = json.optString("telefono", "S/N"),
                cargo = json.optString("cargo", "S/N"),
                avatarUrl = json.optString("avatarUrl", "").ifBlank { null }
            )
        }
    }
}

data class TareaCronogramaData(
    val nombre: String,
    val mesInicio: Int,
    val duracion: Int
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("nombre", nombre)
            put("mesInicio", mesInicio)
            put("duracion", duracion)
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): TareaCronogramaData {
            return TareaCronogramaData(
                nombre = json.optString("nombre", ""),
                mesInicio = json.optInt("mesInicio", 0),
                duracion = json.optInt("duracion", 1)
            )
        }
    }
}

data class ProyectoData(
    val id: String = UUID.randomUUID().toString(),
    val creadorId: String,
    val creadorNombre: String,
    val titulo: String,
    val ods: String,
    val objetivoPrincipal: String,
    val antecedentes: String,
    val justificacion: String,
    val objetivosEspecificos: String,
    val alcance: String,
    val descripcionGeneral: String,
    val recursosNecesarios: String,
    val cronograma: List<TareaCronogramaData>,
    val resultadosEsperados: String,
    val responsables: String,
    val participantesIds: List<String> = emptyList()
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("creadorId", creadorId)
            put("creadorNombre", creadorNombre)
            put("titulo", titulo)
            put("ods", ods)
            put("objetivoPrincipal", objetivoPrincipal)
            put("antecedentes", antecedentes)
            put("justificacion", justificacion)
            put("objetivosEspecificos", objetivosEspecificos)
            put("alcance", alcance)
            put("descripcionGeneral", descripcionGeneral)
            put("recursosNecesarios", recursosNecesarios)
            put("cronograma", JSONArray().apply { cronograma.forEach { put(it.toJsonObject()) } })
            put("resultadosEsperados", resultadosEsperados)
            put("responsables", responsables)
            put("participantesIds", JSONArray(participantesIds))
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): ProyectoData {
            val cronoArray = json.optJSONArray("cronograma") ?: JSONArray()
            val cronoList = mutableListOf<TareaCronogramaData>()
            for (i in 0 until cronoArray.length()) {
                cronoArray.optJSONObject(i)?.let { cronoList.add(TareaCronogramaData.fromJsonObject(it)) }
            }

            val partArray = json.optJSONArray("participantesIds") ?: JSONArray()
            val partList = mutableListOf<String>()
            for (i in 0 until partArray.length()) {
                partList.add(partArray.optString(i))
            }

            return ProyectoData(
                id = json.optString("id", UUID.randomUUID().toString()),
                creadorId = json.optString("creadorId", ""),
                creadorNombre = json.optString("creadorNombre", ""),
                titulo = json.optString("titulo", ""),
                ods = json.optString("ods", ""),
                objetivoPrincipal = json.optString("objetivoPrincipal", ""),
                antecedentes = json.optString("antecedentes", ""),
                justificacion = json.optString("justificacion", ""),
                objetivosEspecificos = json.optString("objetivosEspecificos", ""),
                alcance = json.optString("alcance", ""),
                descripcionGeneral = json.optString("descripcionGeneral", ""),
                recursosNecesarios = json.optString("recursosNecesarios", ""),
                cronograma = cronoList,
                resultadosEsperados = json.optString("resultadosEsperados", ""),
                responsables = json.optString("responsables", ""),
                participantesIds = partList
            )
        }
    }
}

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rover_session_prefs", Context.MODE_PRIVATE)

    fun saveSession(userOrEmail: String, cum: String, pass: String, userId: String) {
        prefs.edit()
            .putString("session_user_email", userOrEmail)
            .putString("session_cum", cum)
            .putString("session_pass", pass)
            .putString("session_user_id", userId)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun hasActiveSession(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getSavedUserId(): String? = prefs.getString("session_user_id", null)
    fun getSavedUserOrEmail(): String? = prefs.getString("session_user_email", null)
    fun getSavedCum(): String? = prefs.getString("session_cum", null)
    fun getSavedPass(): String? = prefs.getString("session_pass", null)
}

class CloudRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rover_cloud_db", Context.MODE_PRIVATE)

    init {
        if (!prefs.contains("users")) {
            val defaultUsers = listOf(
                User(id = "user_1", nombre = "Ana Torres", usuario = "ana.torres", correo = "ana.torres@example.com", cum = "CUM001", contrasena = "1234", telefono = "+52 614 123 4567", cargo = "Scouter"),
                User(id = "user_2", nombre = "Luis Ramírez", usuario = "luis.ramirez", correo = "luis.ramirez@example.com", cum = "CUM002", contrasena = "1234", telefono = "+52 614 234 5678", cargo = "Rover"),
                User(id = "user_3", nombre = "Sofía Herrera", usuario = "sofia.h", correo = "sofia.herrera@example.com", cum = "CUM003", contrasena = "1234", telefono = "+52 614 345 6789", cargo = "Scouter"),
                User(id = "user_4", nombre = "Carlos Mendoza", usuario = "carlos.m", correo = "carlos.mendoza@example.com", cum = "CUM004", contrasena = "1234", telefono = "+52 614 456 7890", cargo = "Rover"),
                User(id = "user_5", nombre = "Valeria Nuñez", usuario = "valeria.n", correo = "valeria.nunez@example.com", cum = "CUM005", contrasena = "1234", telefono = "+52 614 567 8901", cargo = "Scouter")
            )
            saveUsers(defaultUsers)
        }

        if (!prefs.contains("projects")) {
            val defaultProjects = listOf(
                ProyectoData(
                    id = "proj_1",
                    creadorId = "user_2",
                    creadorNombre = "Luis Ramírez",
                    titulo = "Reforestación Comunitaria",
                    ods = "ODS 13: Acción por el clima",
                    objetivoPrincipal = "Promover la reforestación urbana y el cuidado activo de las áreas verdes como una estrategia clave para combatir el cambio climático desde nuestro entorno local.",
                    antecedentes = "Deterioro de las áreas verdes urbanas y aumento de islas de calor en la ciudad.",
                    justificacion = "La reforestación mejora la calidad del aire y crea refugios naturales para la fauna local.",
                    objetivosEspecificos = "- Organizar jornadas comunitarias de plantación\n- Restauración de parques locales\n- Talleres de educación ambiental para familias",
                    alcance = "Zonas urbanas y parques públicos de la comunidad local.",
                    descripcionGeneral = "Plantación de árboles nativos y mantenimiento continuo de espacios públicos con participación vecinal.",
                    recursosNecesarios = "Árboles nativos, herramientas, abono, agua y voluntarios comprometidos.",
                    cronograma = listOf(
                        TareaCronogramaData("Selección de sitios", 0, 1),
                        TareaCronogramaData("Gestión de árboles", 0, 1),
                        TareaCronogramaData("Jornada de plantación", 1, 1),
                        TareaCronogramaData("Seguimiento", 2, 1)
                    ),
                    resultadosEsperados = "Reducción de temperatura ambiental y mayor conciencia ecológica en los vecinos.",
                    responsables = "Luis Ramírez (Responsable)",
                    participantesIds = listOf("user_1", "user_4")
                ),
                ProyectoData(
                    id = "proj_2",
                    creadorId = "user_5",
                    creadorNombre = "Valeria Nuñez",
                    titulo = "Proyecto impulso para herramientas del futuro",
                    ods = "ODS 4: Educación de calidad / ODS 3: Salud y bienestar",
                    objetivoPrincipal = "Fortalecer las habilidades personales, laborales y de autocuidado de los participantes mediante conferencias formativas.",
                    antecedentes = "Niños y jóvenes no tienen acceso a espacios para desarrollar liderazgo y organización del tiempo.",
                    justificacion = "El liderazgo y la organización son pilares para el éxito académico y laboral. Incluye prevención de salud.",
                    objetivosEspecificos = "- Concientizar y prevenir el diabetes\n- Fomentar metodologías para gestión del tiempo\n- Identificar y saber llenar una solicitud de trabajo",
                    alcance = "25 personas (12-40 años) en el Albergue San Vicente de Chihuahua. 2 días, 8 horas.",
                    descripcionGeneral = "Conferencias sobre gestión del tiempo, liderazgo, empleabilidad y prevención de la diabetes.",
                    recursosNecesarios = "Conferencistas (CELIP, Akam surá), Voluntarios, Proyector, Laptop, Papelería.",
                    cronograma = listOf(
                        TareaCronogramaData("Primer contacto", 0, 1),
                        TareaCronogramaData("Organización", 0, 2),
                        TareaCronogramaData("Conferencias", 1, 1),
                        TareaCronogramaData("Evaluación", 1, 1)
                    ),
                    resultadosEsperados = "Participantes con herramientas de empleabilidad y mayor conciencia de salud.",
                    responsables = "Dafne Pineda, Hiro Ogawa",
                    participantesIds = listOf("user_2", "user_3")
                )
            )
            saveProjects(defaultProjects)
        }
    }

    fun getUsers(): List<User> {
        val jsonStr = prefs.getString("users", "[]") ?: "[]"
        val array = JSONArray(jsonStr)
        val list = mutableListOf<User>()
        for (i in 0 until array.length()) {
            array.optJSONObject(i)?.let { list.add(User.fromJsonObject(it)) }
        }
        return list
    }

    fun saveUsers(users: List<User>) {
        val array = JSONArray()
        users.forEach { array.put(it.toJsonObject()) }
        prefs.edit().putString("users", array.toString()).apply()
    }

    fun saveUser(user: User) {
        val users = getUsers().toMutableList()
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = user
        } else {
            users.add(user)
        }
        saveUsers(users)
    }

    fun getProjects(): List<ProyectoData> {
        val jsonStr = prefs.getString("projects", "[]") ?: "[]"
        val array = JSONArray(jsonStr)
        val list = mutableListOf<ProyectoData>()
        for (i in 0 until array.length()) {
            array.optJSONObject(i)?.let { list.add(ProyectoData.fromJsonObject(it)) }
        }
        return list
    }

    fun saveProjects(projects: List<ProyectoData>) {
        val array = JSONArray()
        projects.forEach { array.put(it.toJsonObject()) }
        prefs.edit().putString("projects", array.toString()).apply()
    }

    fun saveProject(project: ProyectoData) {
        val projects = getProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            projects[index] = project
        } else {
            projects.add(project)
        }
        saveProjects(projects)
    }

    fun deleteProject(projectId: String) {
        val projects = getProjects().filterNot { it.id == projectId }
        saveProjects(projects)
    }

    fun toggleVolunteer(projectId: String, userId: String) {
        val projects = getProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == projectId }
        if (index >= 0) {
            val proj = projects[index]
            val participants = proj.participantesIds.toMutableList()
            if (participants.contains(userId)) {
                participants.remove(userId)
            } else {
                participants.add(userId)
            }
            projects[index] = proj.copy(participantesIds = participants)
            saveProjects(projects)
        }
    }
}
