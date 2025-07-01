package com.company.gestion.data.repository

import com.company.gestion.data.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    
    // Colecciones
    private val clientesRef = db.collection("clientes")
    private val prestamosRef = db.collection("prestamos")
    private val abonosRef = db.collection("abonos")
    private val activosRef = db.collection("activos")

    // Clientes
    suspend fun guardarCliente(cliente: Cliente): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (cliente.id.isEmpty()) clientesRef.document() else clientesRef.document(cliente.id)
            val clienteConId = cliente.copy(id = docRef.id)
            docRef.set(clienteConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerClientes(): Flow<List<Cliente>> = flow {
        try {
            val snapshot = clientesRef.orderBy("nombre").get().await()
            val clientes = snapshot.documents.mapNotNull { it.toObject(Cliente::class.java) }
            emit(clientes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Préstamos
    suspend fun guardarPrestamo(prestamo: Prestamo): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (prestamo.id.isEmpty()) prestamosRef.document() else prestamosRef.document(prestamo.id)
            val prestamoConId = prestamo.copy(id = docRef.id)
            docRef.set(prestamoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerPrestamos(clienteId: String? = null): Flow<List<Prestamo>> = flow {
        try {
            val query = if (clienteId != null) {
                prestamosRef.whereEqualTo("clienteId", clienteId)
            } else {
                prestamosRef
            }
            val snapshot = query.orderBy("fechaCreacion", Query.Direction.DESCENDING).get().await()
            val prestamos = snapshot.documents.mapNotNull { it.toObject(Prestamo::class.java) }
            emit(prestamos)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Abonos
    suspend fun guardarAbono(abono: Abono): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (abono.id.isEmpty()) abonosRef.document() else abonosRef.document(abono.id)
            val abonoConId = abono.copy(id = docRef.id)
            docRef.set(abonoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerAbonos(prestamoId: String): Flow<List<Abono>> = flow {
        try {
            val snapshot = abonosRef
                .whereEqualTo("prestamoId", prestamoId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            val abonos = snapshot.documents.mapNotNull { it.toObject(Abono::class.java) }
            emit(abonos)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Activos
    suspend fun guardarActivo(activo: Activo): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (activo.id.isEmpty()) activosRef.document() else activosRef.document(activo.id)
            val activoConId = activo.copy(id = docRef.id)
            docRef.set(activoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerActivos(): Flow<List<Activo>> = flow {
        try {
            val snapshot = activosRef
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            val activos = snapshot.documents.mapNotNull { it.toObject(Activo::class.java) }
            emit(activos)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Eliminación
    suspend fun eliminarCliente(clienteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            clientesRef.document(clienteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPrestamo(prestamoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            prestamosRef.document(prestamoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarActivo(activoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            activosRef.document(activoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
