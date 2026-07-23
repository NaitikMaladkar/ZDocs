package com.zdocs.util

import android.content.Context
import android.os.Environment
import com.zdocs.data.model.FileType
import com.zdocs.data.model.ZFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Helper class for managing local file storage.
 * 
 * Storage structure:
 *   Android/Data/com.zdocs/ (or internal files dir)
 *   └── ZDocs/
 *       ├── Spreadsheets/
 *       ├── Documents/
 *       ├── Notes/
 *       ├── Code/
 *       └── Data/
 */
class StorageHelper(private val context: Context) {

    /**
     * Get the base ZDocs directory.
     * Uses app-specific external storage (Android/data/com.zdocs/files/ZDocs)
     * which doesn't require permissions on Android 10+.
     */
    fun getBaseDir(): File {
        val baseDir = File(
            context.getExternalFilesDir(null),
            FileConstants.APP_FOLDER_NAME
        )
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
        return baseDir
    }

    /**
     * Get the directory for a specific file type.
     * Creates it if it doesn't exist.
     */
    fun getTypeDir(fileType: FileType): File {
        val dir = File(getBaseDir(), fileType.folderName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Initialize all type directories.
     */
    fun initDirectories() {
        FileConstants.ALL_FOLDERS.forEach { folderName ->
            val dir = File(getBaseDir(), folderName)
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
    }

    /**
     * Create a new file in the appropriate type directory.
     * @return the created ZFile, or null if creation failed
     */
    fun createFile(fileName: String, fileType: FileType, content: String = ""): ZFile? {
        return try {
            val dir = getTypeDir(fileType)
            var file = File(dir, fileName)

            // Auto-number if file already exists
            if (file.exists()) {
                val baseName = fileName.substringBeforeLast('.')
                val ext = fileName.substringAfterLast('.', "")
                var counter = 1
                while (file.exists()) {
                    val newName = if (ext.isNotEmpty()) "$baseName ($counter).$ext" else "$baseName ($counter)"
                    file = File(dir, newName)
                    counter++
                }
            }

            file.writeText(content)
            ZFile.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get all files of a specific type.
     */
    fun getFilesByType(fileType: FileType): List<ZFile> {
        val dir = getTypeDir(fileType)
        return dir.listFiles()
            ?.filter { it.isFile }
            ?.map { ZFile.fromFile(it) }
            ?.sortedByDescending { it.lastModified }
            ?: emptyList()
    }

    /**
     * Get all files across all types.
     */
    fun getAllFiles(): List<ZFile> {
        return FileType.entries.flatMap { getFilesByType(it) }
            .sortedByDescending { it.lastModified }
    }

    /**
     * Get file count for a specific type.
     */
    fun getFileCount(fileType: FileType): Int {
        return getTypeDir(fileType).listFiles()
            ?.count { it.isFile } ?: 0
    }

    /**
     * Delete a file.
     */
    fun deleteFile(zFile: ZFile): Boolean {
        return try {
            val file = File(zFile.path)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Rename a file.
     */
    fun renameFile(zFile: ZFile, newName: String): ZFile? {
        return try {
            val oldFile = File(zFile.path)
            val newFile = File(oldFile.parent, newName)
            if (oldFile.renameTo(newFile)) {
                ZFile.fromFile(newFile, zFile.isFavorite)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Read file content as text.
     */
    fun readFileContent(zFile: ZFile): String? {
        return try {
            val file = File(zFile.path)
            if (file.exists() && file.length() <= FileConstants.MAX_EDIT_FILE_SIZE) {
                file.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Write content to a file.
     */
    fun writeFileContent(zFile: ZFile, content: String): Boolean {
        return try {
            val file = File(zFile.path)
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Save a file from an external source (e.g., "Open with" from another app).
     * Copies the content into the appropriate type folder.
     */
    fun saveExternalFile(inputStream: InputStream, fileName: String): ZFile? {
        return try {
            val fileType = FileType.fromFileName(fileName)
            val dir = getTypeDir(fileType)
            var targetFile = File(dir, fileName)

            // Handle duplicate names
            if (targetFile.exists()) {
                val baseName = fileName.substringBeforeLast('.')
                val ext = fileName.substringAfterLast('.', "")
                var counter = 1
                while (targetFile.exists()) {
                    val newName = if (ext.isNotEmpty()) "${baseName}_$counter.$ext" else "${baseName}_$counter"
                    targetFile = File(dir, newName)
                    counter++
                }
            }

            FileOutputStream(targetFile).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }

            ZFile.fromFile(targetFile).copy(isFromExternal = true)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get the total storage used by ZDocs.
     */
    fun getTotalStorageUsed(): Long {
        return getBaseDir().walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    /**
     * Clear all cache/temp files.
     */
    fun clearCache() {
        context.cacheDir?.deleteRecursively()
    }
}
