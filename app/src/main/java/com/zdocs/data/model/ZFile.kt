package com.zdocs.data.model

import java.io.File
import java.io.Serializable

/**
 * Represents a file managed by ZDocs.
 */
data class ZFile(
    val name: String,
    val path: String,
    val type: FileType,
    val size: Long,
    val lastModified: Long,
    val isFavorite: Boolean = false,
    val isFromExternal: Boolean = false
) : Serializable {

    val extension: String
        get() = name.substringAfterLast('.', "")

    val displayName: String
        get() = name.substringBeforeLast('.', name)

    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "%.1f KB".format(size / 1024.0)
            else -> "%.1f MB".format(size / (1024.0 * 1024.0))
        }

    fun toFile(): File = File(path)

    companion object {
        fun fromFile(file: File, isFavorite: Boolean = false): ZFile {
            return ZFile(
                name = file.name,
                path = file.absolutePath,
                type = FileType.fromFileName(file.name),
                size = file.length(),
                lastModified = file.lastModified(),
                isFavorite = isFavorite
            )
        }
    }
}
