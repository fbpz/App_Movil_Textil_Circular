package com.example.textilcircular.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Gestiona las fotos de productos en el almacenamiento interno de la app
 * (no requiere permisos de almacenamiento y los archivos son privados).
 */
class ImageStorage(private val context: Context) {

    private val carpeta: File
        get() = File(context.filesDir, "images").apply { mkdirs() }

    /** Crea un archivo vacío y su Uri segura (FileProvider) para que la cámara escriba la foto. */
    fun crearArchivoParaCamara(): Pair<File, Uri> {
        val archivo = File(carpeta, "producto_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)
        return archivo to uri
    }

    /** Copia una imagen elegida desde la galería al almacenamiento interno. */
    suspend fun copiarDesdeGaleria(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val destino = File(carpeta, "galeria_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            } ?: return@runCatching null
            destino.absolutePath
        }.getOrNull()
    }

    fun eliminar(path: String?) {
        if (path == null) return
        val archivo = File(path)
        // Solo se borran archivos propios de la app
        if (archivo.parentFile?.absolutePath == carpeta.absolutePath) archivo.delete()
    }
}
