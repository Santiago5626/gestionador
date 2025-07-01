package com.gestionador.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gestionador.data.models.*
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    
    // Clientes
    suspend fun addCliente(cliente: Cliente): Result<String> {
        return try {
            val docRef = db.collection("clientes").add(cliente).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getClientes(): Result<List<Cliente>> {
        return try {
            val snapshot = db.collection("clientes")
                .orderBy("nombre")
                .get()
                .await()
            val clientes = snapshot.documents.map { doc ->
                doc.toObject(Cliente::class.java)?.copy(id = doc.id) ?: Cliente()
            }
            Result.success(clientes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCliente(cliente: Cliente): Result<Unit> {
        return try {
            db.collection("clientes").document(cliente.id).set(cliente).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCliente(clienteId: String): Result<Unit> {
        return try {
            db.collection("clientes").document(clienteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Préstamos
    suspend fun addPrestamo(prestamo: Prestamo): Result<String> {
        return try {
            val docRef = db.collection("prestamos").add(prestamo).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPrestamos(): Result<List<Prestamo>> {
        return try {
            val snapshot = db.collection("prestamos")
                .orderBy("fechaInicial", Query.Direction.DESCENDING)
                .get()
                .await()
            val prestamos = snapshot.documents.map { doc ->
                doc.toObject(Prestamo::class.java)?.copy(id = doc.id) ?: Prestamo()
            }
            Result.success(prestamos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPrestamosByCliente(clienteId: String): Result<List<Prestamo>> {
        return try {
            val snapshot = db.collection("prestamos")
                .whereEqualTo("clienteId", clienteId)
                .orderBy("fechaInicial", Query.Direction.DESCENDING)
                .get()
                .await()
            val prestamos = snapshot.documents.map { doc ->
                doc.toObject(Prestamo::class.java)?.copy(id = doc.id) ?: Prestamo()
            }
            Result.success(prestamos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePrestamo(prestamo: Prestamo): Result<Unit> {
        return try {
            db.collection("prestamos").document(prestamo.id).set(prestamo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Abonos
    suspend fun addAbono(abono: Abono): Result<String> {
        return try {
            val docRef = db.collection("abonos").add(abono).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAbonosByPrestamo(prestamoId: String): Result<List<Abono>> {
        return try {
            val snapshot = db.collection("abonos")
                .whereEqualTo("prestamoId", prestamoId)
                .orderBy("fechaAbono", Query.Direction.DESCENDING)
                .get()
                .await()
            val abonos = snapshot.documents.map { doc ->
                doc.toObject(Abono::class.java)?.copy(id = doc.id) ?: Abono()
            }
            Result.success(abonos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Activos
    suspend fun addActivo(activo: Activo): Result<String> {
        return try {
            val docRef = db.collection("activos").add(activo).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getActivos(): Result<List<Activo>> {
        return try {
            val snapshot = db.collection("activos")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            val activos = snapshot.documents.map { doc ->
                doc.toObject(Activo::class.java)?.copy(id = doc.id) ?: Activo()
            }
            Result.success(activos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateActivo(activo: Activo): Result<Unit> {
        return try {
            db.collection("activos").document(activo.id).set(activo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteActivo(activoId: String): Result<Unit> {
        return try {
            db.collection("activos").document(activoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
