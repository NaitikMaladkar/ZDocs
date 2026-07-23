package com.zdocs.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.zdocs.data.model.FileType
import com.zdocs.data.model.ZFile
import com.zdocs.util.StorageHelper
import java.io.InputStream

/**
 * Repository for managing ZDocs files.
 * Single source of truth for file operations and favorites.
 */
class FileRepository(context: Context) {

    private val storageHelper = StorageHelper(context)
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "zdocs_prefs", Context.MODE_PRIVATE
    )

    fun initDirectories() = storageHelper.initDirectories()
    fun getBaseDir() = storageHelper.getBaseDir()

    fun createFile(fileName: String, fileType: FileType, content: String = ""): ZFile? {
        return storageHelper.createFile(fileName, fileType, content)
    }

    fun getAllFiles(): List<ZFile> {
        val favorites = getFavoritePaths()
        return storageHelper.getAllFiles().map { file ->
            file.copy(isFavorite = favorites.contains(file.path))
        }
    }

    fun getFilesByType(fileType: FileType): List<ZFile> {
        val favorites = getFavoritePaths()
        return storageHelper.getFilesByType(fileType).map { file ->
            file.copy(isFavorite = favorites.contains(file.path))
        }
    }

    fun getFileCount(fileType: FileType): Int = storageHelper.getFileCount(fileType)

    fun deleteFile(zFile: ZFile): Boolean {
        val result = storageHelper.deleteFile(zFile)
        if (result && zFile.isFavorite) {
            removeFavoritePath(zFile.path)
        }
        return result
    }

    fun renameFile(zFile: ZFile, newName: String): ZFile? {
        val oldPath = zFile.path
        val result = storageHelper.renameFile(zFile, newName)
        if (result != null && zFile.isFavorite) {
            removeFavoritePath(oldPath)
            addFavoritePath(result.path)
        }
        return result
    }

    fun readFileContent(zFile: ZFile): String? = storageHelper.readFileContent(zFile)

    fun writeFileContent(zFile: ZFile, content: String): Boolean =
        storageHelper.writeFileContent(zFile, content)

    fun saveExternalFile(inputStream: InputStream, fileName: String): ZFile? =
        storageHelper.saveExternalFile(inputStream, fileName)

    fun getFavoriteFiles(): List<ZFile> {
        return getAllFiles().filter { it.isFavorite }
    }

    /**
     * Get files modified in the last 7 days (for Recents tab).
     * Auto-clears by only showing recent files.
     */
    fun getRecentFiles(): List<ZFile> {
        val sevenDaysMillis = 7 * 24 * 60 * 60 * 1000L
        val cutoff = System.currentTimeMillis() - sevenDaysMillis
        return getAllFiles().filter { it.lastModified >= cutoff }
    }

    fun toggleFavorite(zFile: ZFile): Boolean {
        return if (zFile.isFavorite) {
            removeFavoritePath(zFile.path)
            false
        } else {
            addFavoritePath(zFile.path)
            true
        }
    }

    private fun getFavoritePaths(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    private fun addFavoritePath(path: String) {
        val favorites = getFavoritePaths().toMutableSet()
        favorites.add(path)
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    private fun removeFavoritePath(path: String) {
        val favorites = getFavoritePaths().toMutableSet()
        favorites.remove(path)
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    fun getTotalStorageUsed(): Long = storageHelper.getTotalStorageUsed()
    fun clearCache() = storageHelper.clearCache()

    // --- CSV Parsing Utility ---

    /**
     * Parse CSV content into a 2D list of strings.
     */
    fun parseCsv(content: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val lines = content.lines()
        for (line in lines) {
            if (line.isBlank() && rows.isEmpty()) continue
            val row = parseCsvLine(line)
            rows.add(row)
        }
        // Ensure at least one row
        if (rows.isEmpty()) rows.add(listOf(""))
        return rows
    }

    /**
     * Parse a single CSV line respecting quotes.
     */
    private fun parseCsvLine(line: String): List<String> {
        val cells = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                inQuotes && ch == '"' -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i += 2
                    } else {
                        inQuotes = false
                        i++
                    }
                }
                ch == '"' -> {
                    inQuotes = true
                    i++
                }
                ch == ',' && !inQuotes -> {
                    cells.add(current.toString().trim())
                    current = StringBuilder()
                    i++
                }
                else -> {
                    current.append(ch)
                    i++
                }
            }
        }
        cells.add(current.toString().trim())
        return cells
    }

    /**
     * Convert 2D list back to CSV string.
     */
    fun serializeCsv(data: List<List<String>>): String {
        return data.joinToString("\n") { row ->
            row.joinToString(",") { cell ->
                if (cell.contains(',') || cell.contains('"') || cell.contains('\n')) {
                    "\"${cell.replace("\"", "\"\"")}\""
                } else {
                    cell
                }
            }
        }
    }
}
