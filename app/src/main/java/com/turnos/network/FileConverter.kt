package com.turnos.network

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

object FileConverter {

    // Nombre del campo en el formulario Multipart (debe coincidir con Spring Boot)
    private const val FILE_PART_NAME = "image"

    /**
     * Convierte una Uri de Android en un MultipartBody.Part para Retrofit.
     * Requiere el Context para acceder a los bytes del archivo a través del ContentResolver.
     */
    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        // 1. Obtener el Content Resolver
        val contentResolver = context.contentResolver

        // 2. Obtener el tipo MIME (ej. image/jpeg) y el nombre de archivo (si es posible)
        val mimeType = contentResolver.getType(uri) ?: "image/*"
        val fileName = getFileName(context, uri) ?: "profile_image"

        // 3. Leer los bytes del archivo (necesita ser en un bloque de ejecución seguro)
        val inputStream: InputStream? = try {
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (inputStream == null) return null

        // 4. Leer todos los bytes del InputStream
        val imageBytes = inputStream.readBytes()
        inputStream.close()

        // 5. Crear el RequestBody con los bytes y el tipo MIME
        val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())

        // 6. Crear y devolver el MultipartBody.Part (la parte que Retrofit envía)
        return MultipartBody.Part.createFormData(
            FILE_PART_NAME, // Nombre del campo (clave en Spring: @RequestPart("image"))
            fileName,       // Nombre del archivo original
            requestBody     // Los datos binarios
        )
    }

    // Función auxiliar para intentar obtener el nombre de archivo (simplificado)
    private fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex("_display_name")
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }
}