package org.deltacv.myapplication.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreManager {
    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    fun obtenerUsuarioPorUid(uid: String): Flow<Usuario?> = callbackFlow {
        if (uid.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = db.collection("usuarios").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(Usuario::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    fun buscarUsuarioParaLogin(
        identificador: String,
        cum: String,
        contrasena: String,
        onResult: (Usuario?) -> Unit
    ) {
        db.collection("usuarios")
            .whereEqualTo("cum", cum)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val user = querySnapshot.documents
                    .mapNotNull { it.toObject(Usuario::class.java) }
                    .find { u: Usuario ->
                        (u.correo.equals(identificador, ignoreCase = true) ||
                         u.usuario.equals(identificador, ignoreCase = true)) &&
                         u.contrasena == contrasena
                    }
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun verificarYRegistrarUsuario(
        usuario: Usuario,
        onSuccess: (Usuario) -> Unit,
        onConflict: (usuarioExiste: Boolean, correoExiste: Boolean, cumExiste: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val usuariosList = querySnapshot.documents
                    .mapNotNull { it.toObject(Usuario::class.java) }
                    .filter { it.uid != usuario.uid }

                val usuarioDuplicado = usuariosList.any {
                    it.usuario.trim().equals(usuario.usuario.trim(), ignoreCase = true)
                }
                val correoDuplicado = usuariosList.any {
                    it.correo.trim().equals(usuario.correo.trim(), ignoreCase = true)
                }
                val cumDuplicado = usuariosList.any {
                    it.cum.trim().equals(usuario.cum.trim(), ignoreCase = true)
                }

                if (usuarioDuplicado || correoDuplicado || cumDuplicado) {
                    onConflict(usuarioDuplicado, correoDuplicado, cumDuplicado)
                } else {
                    val docRef = db.collection("usuarios").document()
                    val newUser = usuario.copy(uid = docRef.id)
                    docRef.set(newUser)
                        .addOnSuccessListener { onSuccess(newUser) }
                        .addOnFailureListener { e: Exception -> onError(e.localizedMessage ?: "Error al registrar") }
                }
            }
            .addOnFailureListener { e: Exception ->
                onError(e.localizedMessage ?: "Error al consultar la base de datos")
            }
    }

    fun registrarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (usuario.uid.isBlank()) {
            val docRef = db.collection("usuarios").document()
            val newUser = usuario.copy(uid = docRef.id)
            docRef.set(newUser)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        } else {
            db.collection("usuarios").document(usuario.uid)
                .set(usuario)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        }
    }

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> = callbackFlow {
        val listener = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Usuario::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun obtenerTodosLosProyectos(): Flow<List<Proyecto>> = callbackFlow {
        val listener = db.collection("proyectos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Proyecto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun obtenerProyectosPorResponsable(uid: String): Flow<List<Proyecto>> = callbackFlow {
        val listener = db.collection("proyectos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(Proyecto::class.java) }
                    ?.filter { it.responsablesIds.contains(uid) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun obtenerProyectosPorVoluntario(uid: String): Flow<List<Proyecto>> = callbackFlow {
        val listener = db.collection("proyectos")
            .whereArrayContains("voluntariosIds", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Proyecto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun crearProyecto(
        proyecto: Proyecto,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val docRef = db.collection("proyectos").document()
        val newProj = proyecto.copy(id = docRef.id)
        docRef.set(newProj)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error al crear proyecto") }
    }

    fun editarProyecto(
        id: String,
        mapUpdates: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("proyectos").document(id)
            .update(mapUpdates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error al actualizar") }
    }

    fun eliminarProyecto(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("proyectos").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error al eliminar") }
    }

    fun agregarVoluntarioAProyecto(idProyecto: String, uidUsuario: String) {
        db.collection("proyectos").document(idProyecto)
            .update("voluntariosIds", FieldValue.arrayUnion(uidUsuario))
    }

    fun quitarVoluntarioDeProyecto(idProyecto: String, uidUsuario: String) {
        db.collection("proyectos").document(idProyecto)
            .update("voluntariosIds", FieldValue.arrayRemove(uidUsuario))
    }
}
