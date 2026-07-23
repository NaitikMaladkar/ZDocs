package com.zdocs.data.model

import androidx.compose.ui.graphics.Color
import com.zdocs.ui.theme.*

/**
 * Enum representing all supported file types in ZDocs.
 * Each type has its own folder in local storage, icon, and color.
 */
enum class FileType(
    val displayName: String,
    val folderName: String,
    val extensions: List<String>,
    val color: Color,
    val description: String,
    val emoji: String
) {
    DOCUMENT(
        displayName = "Document",
        folderName = "Documents",
        extensions = listOf("docx", "odt", "txt", "md", "doc", "rtf"),
        color = Color(0xFF1565C0),
        description = "DOCX, ODT, TXT, MD files",
        emoji = "📄"
    ),
    SPREADSHEET(
        displayName = "Spreadsheet",
        folderName = "Spreadsheets",
        extensions = listOf("xlsx", "xls", "csv", "ods", "tsv"),
        color = Color(0xFF2E7D32),
        description = "XLSX, XLS, CSV, ODS files",
        emoji = "📊"
    ),
    PRESENTATION(
        displayName = "Presentation",
        folderName = "Presentations",
        extensions = listOf("pptx", "odp", "ppt"),
        color = Color(0xFFE65100),
        description = "PPTX, ODP, PPT files",
        emoji = "🎞️"
    ),
    PDF(
        displayName = "PDF",
        folderName = "PDFs",
        extensions = listOf("pdf"),
        color = Color(0xFFC62828),
        description = "PDF files",
        emoji = "📕"
    ),
    NOTE(
        displayName = "Note",
        folderName = "Notes",
        extensions = listOf("txt", "md"),
        color = Color(0xFFF57F17),
        description = "Quick notes and memos",
        emoji = "📝"
    ),
    CODE(
        displayName = "Code",
        folderName = "Code",
        extensions = listOf(
            "py", "js", "ts", "tsx", "html", "css", "java", "kt", "cpp", "c",
            "go", "rs", "rb", "php", "sql", "swift", "dart", "vue", "sh", "bat",
            "json", "xml", "yaml", "yml", "md"
        ),
        color = Color(0xFF6A1B9A),
        description = "Python, JS, TS, HTML, CSS, Java, Kotlin, C++, Go, Rust, Ruby, PHP, SQL, Swift, Dart, Vue, Shell, and more",
        emoji = "💻"
    ),
    DATA(
        displayName = "Data",
        folderName = "Data",
        extensions = listOf("json", "xml", "yaml", "yml", "toml", "ini", "cfg", "conf"),
        color = Color(0xFFD84315),
        description = "JSON, XML, YAML, config files",
        emoji = "🗃️"
    );

    companion object {
        fun fromExtension(ext: String): FileType {
            val lowerExt = ext.lowercase()
            val priorityOrder = listOf(PDF, PRESENTATION, SPREADSHEET, CODE, DATA, NOTE, DOCUMENT)
            return priorityOrder.firstOrNull { type ->
                type.extensions.contains(lowerExt)
            } ?: DOCUMENT
        }

        fun fromFileName(fileName: String): FileType {
            val dotIndex = fileName.lastIndexOf('.')
            return if (dotIndex > 0 && dotIndex < fileName.length - 1) {
                fromExtension(fileName.substring(dotIndex + 1))
            } else {
                NOTE
            }
        }
    }
}

/**
 * Language definition for syntax highlighting.
 */
enum class SyntaxLanguage(val fileExtensions: List<String>) {
    PYTHON(listOf("py")),
    JAVASCRIPT(listOf("js")),
    TYPESCRIPT(listOf("ts", "tsx")),
    HTML(listOf("html", "htm")),
    CSS(listOf("css")),
    KOTLIN(listOf("kt", "kts")),
    JAVA(listOf("java")),
    C_CPP(listOf("c", "cpp", "h", "hpp")),
    GO(listOf("go")),
    RUST(listOf("rs")),
    RUBY(listOf("rb")),
    PHP(listOf("php")),
    SWIFT(listOf("swift")),
    DART(listOf("dart")),
    VUE(listOf("vue")),
    SQL(listOf("sql")),
    SHELL(listOf("sh", "bash")),
    JSON(listOf("json")),
    XML(listOf("xml", "svg")),
    YAML(listOf("yaml", "yml")),
    MARKDOWN(listOf("md")),
    PLAIN_TEXT(emptyList());

    companion object {
        fun fromExtension(ext: String): SyntaxLanguage {
            val lowerExt = ext.lowercase()
            return entries.firstOrNull { lang ->
                lang.fileExtensions.contains(lowerExt)
            } ?: PLAIN_TEXT
        }
    }
}
