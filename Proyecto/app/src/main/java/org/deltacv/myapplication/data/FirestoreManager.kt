package org.deltacv.myapplication.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreManager {
    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    // 1. USUARIOS
    fun registrarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val collectionRef = db.collection("usuarios")
        val docRef = if (usuario.uid.isNotBlank()) collectionRef.document(usuario.uid) else collectionRef.document()
        val finalUsuario = usuario.copy(uid = docRef.id)
        docRef.set(finalUsuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun verificarYRegistrarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onConflict: (usuarioExiste: Boolean, correoExiste: Boolean, cumExiste: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { snapshot ->
                val usuariosList = snapshot.toObjects(Usuario::class.java)

                val otrosUsuarios = usuariosList.filter { it.uid != usuario.uid }

                val usuarioDuplicado = otrosUsuarios.any {
                    it.usuario.trim().equals(usuario.usuario.trim(), ignoreCase = true)
                }
                val correoDuplicado = otrosUsuarios.any {
                    it.correo.trim().equals(usuario.correo.trim(), ignoreCase = true)
                }
                val cumDuplicado = otrosUsuarios.any {
                    it.cum.trim().equals(usuario.cum.trim(), ignoreCase = true)
                }

                if (usuarioDuplicado || correoDuplicado || cumDuplicado) {
                    onConflict(usuarioDuplicado, correoDuplicado, cumDuplicado)
                } else {
                    registrarUsuario(usuario, onSuccess, onError)
                }
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun obtenerUsuarioPorUid(uid: String): Flow<Usuario?> = callbackFlow {
        if (uid.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = db.collection("usuarios").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(Usuario::class.java)
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> = callbackFlow {
        val listener = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val usuarios = snapshot.toObjects(Usuario::class.java)
                    trySend(usuarios)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun buscarUsuarioParaLogin(
        identificador: String,
        cum: String,
        contrasena: String,
        onSuccess: (Usuario?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { snapshot ->
                val usuarios = snapshot.toObjects(Usuario::class.java)
                val matched = usuarios.find { user ->
                    (user.correo.equals(identificador, ignoreCase = true) || user.usuario.equals(identificador, ignoreCase = true)) &&
                    user.cum.equals(cum, ignoreCase = true) &&
                    user.contrasena == contrasena
                }
                onSuccess(matched)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    // 2. PROYECTOS
    fun crearProyecto(
        proyecto: Proyecto,
        responsableUid: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val collectionRef = db.collection("proyectos")
        val docRef = if (proyecto.id.isNotBlank()) collectionRef.document(proyecto.id) else collectionRef.document()
        val responsablesList = if (proyecto.responsablesIds.contains(responsableUid)) {
            proyecto.responsablesIds
        } else {
            proyecto.responsablesIds + responsableUid
        }
        val finalProyecto = proyecto.copy(id = docRef.id, responsablesIds = responsablesList)
        docRef.set(finalProyecto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun obtenerTodosLosProyectos(): Flow<List<Proyecto>> = callbackFlow {
        val listener = db.collection("proyectos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val proyectos = snapshot.toObjects(Proyecto::class.java)
                    trySend(proyectos)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun obtenerProyectosPorResponsable(usuarioUid: String): Flow<List<Proyecto>> = callbackFlow {
        if (usuarioUid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("proyectos")
            .whereArrayContains("responsablesIds", usuarioUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val proyectos = snapshot.toObjects(Proyecto::class.java)
                    trySend(proyectos)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun obtenerProyectosPorVoluntario(usuarioUid: String): Flow<List<Proyecto>> = callbackFlow {
        if (usuarioUid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("proyectos")
            .whereArrayContains("voluntariosIds", usuarioUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val proyectos = snapshot.toObjects(Proyecto::class.java)
                    trySend(proyectos)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun agregarVoluntarioAProyecto(
        proyectoId: String,
        usuarioUid: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val docRef = db.collection("proyectos").document(proyectoId)
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val proj = snapshot.toObject(Proyecto::class.java)
                if (proj != null) {
                    val updateTask = if (proj.voluntariosIds.contains(usuarioUid)) {
                        docRef.update("voluntariosIds", FieldValue.arrayRemove(usuarioUid))
                    } else {
                        docRef.update("voluntariosIds", FieldValue.arrayUnion(usuarioUid))
                    }
                    updateTask.addOnSuccessListener { onSuccess() }.addOnFailureListener { onError(it) }
                }
            }
        }.addOnFailureListener { onError(it) }
    }

    fun agregarResponsableAProyecto(
        proyectoId: String,
        usuarioUid: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        db.collection("proyectos").document(proyectoId)
            .update("responsablesIds", FieldValue.arrayUnion(usuarioUid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun editarProyecto(
        proyectoId: String,
        camposActualizados: Map<String, Any>,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        db.collection("proyectos").document(proyectoId)
            .update(camposActualizados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun eliminarProyecto(
        proyectoId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        db.collection("proyectos").document(proyectoId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
