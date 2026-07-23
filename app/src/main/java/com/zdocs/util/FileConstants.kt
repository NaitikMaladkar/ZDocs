package com.zdocs.util

/**
 * Constants used throughout the ZDocs application.
 */
object FileConstants {

    // Base folder name in Android local storage
    const val APP_FOLDER_NAME = "ZDocs"

    // Subfolder names - match FileType.folderName
    const val DOCUMENTS_FOLDER = "Documents"
    const val SPREADSHEETS_FOLDER = "Spreadsheets"
    const val PRESENTATIONS_FOLDER = "Presentations"
    const val PDFS_FOLDER = "PDFs"
    const val NOTES_FOLDER = "Notes"
    const val CODE_FOLDER = "Code"
    const val DATA_FOLDER = "Data"

    // All folder names
    val ALL_FOLDERS = listOf(
        DOCUMENTS_FOLDER,
        SPREADSHEETS_FOLDER,
        PRESENTATIONS_FOLDER,
        PDFS_FOLDER,
        NOTES_FOLDER,
        CODE_FOLDER,
        DATA_FOLDER
    )

    // Default file extensions for new files
    const val DEFAULT_DOCUMENT_EXT = "txt"
    const val DEFAULT_SPREADSHEET_EXT = "csv"
    const val DEFAULT_PRESENTATION_EXT = "pptx"
    const val DEFAULT_PDF_EXT = "pdf"
    const val DEFAULT_NOTE_EXT = "txt"
    const val DEFAULT_CODE_EXT = "py"
    const val DEFAULT_DATA_EXT = "json"

    // Max file size for editing (10MB)
    const val MAX_EDIT_FILE_SIZE = 10 * 1024 * 1024L

    // Preferences
    const val PREFS_NAME = "zdocs_prefs"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_FAVORITES = "favorites"
}
