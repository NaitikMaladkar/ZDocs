package com.zdocs.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zdocs.data.model.SyntaxLanguage

/**
 * Syntax highlighting color scheme.
 */
object SyntaxColors {
    val keyword = Color(0xFFC678DD)      // Purple
    val string = Color(0xFF98C379)       // Green
    val number = Color(0xFFD19A66)       // Orange
    val comment = Color(0xFF5C6370)      // Gray
    val function = Color(0xFF61AFEF)     // Blue
    val type = Color(0xFFE5C07B)         // Yellow
    val tag = Color(0xFFE06C75)          // Red
    val attribute = Color(0xFFD19A66)    // Orange
    val punctuation = Color(0xFFABB2BF)  // Light gray
}

/**
 * Keywords for different languages.
 */
object SyntaxKeywords {
    val python = setOf(
        "def", "class", "import", "from", "return", "if", "elif", "else",
        "for", "while", "try", "except", "finally", "with", "as", "yield",
        "lambda", "pass", "break", "continue", "and", "or", "not", "in",
        "is", "None", "True", "False", "raise", "del", "global", "nonlocal",
        "assert", "async", "await"
    )
    val javascript = setOf(
        "function", "const", "let", "var", "return", "if", "else", "for",
        "while", "do", "switch", "case", "break", "continue", "try", "catch",
        "finally", "throw", "new", "this", "class", "extends", "import",
        "export", "from", "default", "typeof", "instanceof", "async", "await",
        "yield", "true", "false", "null", "undefined", "void", "delete"
    )
    val kotlin = setOf(
        "fun", "val", "var", "class", "object", "interface", "enum", "when",
        "if", "else", "for", "while", "do", "return", "try", "catch",
        "finally", "throw", "import", "package", "is", "as", "in", "override",
        "private", "public", "protected", "internal", "abstract", "open",
        "sealed", "data", "companion", "suspend", "inline", "reified",
        "true", "false", "null", "this", "super", "it", "by", "lazy"
    )
    val java = setOf(
        "public", "private", "protected", "class", "interface", "extends",
        "implements", "static", "final", "void", "int", "long", "double",
        "float", "boolean", "char", "String", "return", "if", "else", "for",
        "while", "do", "switch", "case", "break", "continue", "try", "catch",
        "finally", "throw", "throws", "new", "this", "super", "import",
        "package", "abstract", "synchronized", "volatile", "transient",
        "null", "true", "false", "instanceof"
    )
    val html = setOf(
        "html", "head", "body", "div", "span", "p", "a", "img", "ul", "ol",
        "li", "h1", "h2", "h3", "h4", "h5", "h6", "table", "tr", "td", "th",
        "form", "input", "button", "select", "option", "textarea", "script",
        "style", "link", "meta", "title", "header", "footer", "nav", "section",
        "article", "aside", "main"
    )
    val css = setOf(
        "color", "background", "margin", "padding", "border", "font", "display",
        "position", "width", "height", "top", "left", "right", "bottom",
        "flex", "grid", "align", "justify", "text", "overflow", "opacity",
        "transform", "transition", "animation", "box-shadow", "important",
        "none", "auto", "inherit", "initial", "relative", "absolute",
        "fixed", "sticky"
    )

    fun forLanguage(lang: SyntaxLanguage): Set<String> {
        return when (lang) {
            SyntaxLanguage.PYTHON -> python
            SyntaxLanguage.JAVASCRIPT, SyntaxLanguage.TYPESCRIPT -> javascript
            SyntaxLanguage.KOTLIN -> kotlin
            SyntaxLanguage.JAVA -> java
            SyntaxLanguage.HTML -> html
            SyntaxLanguage.CSS -> css
            SyntaxLanguage.C_CPP -> java + setOf("struct", "typedef", "sizeof", "include", "define", "ifdef", "endif", "pragma")
            SyntaxLanguage.GO -> setOf("func", "package", "import", "var", "const", "type", "struct", "interface",
                "map", "chan", "go", "defer", "return", "if", "else", "for", "range",
                "switch", "case", "default", "break", "continue", "select", "fallthrough",
                "true", "false", "nil", "make", "new", "len", "append", "fmt")
            SyntaxLanguage.RUST -> setOf("fn", "let", "mut", "pub", "struct", "enum", "impl", "trait", "mod",
                "use", "crate", "self", "super", "match", "if", "else", "for", "while",
                "loop", "break", "continue", "return", "where", "as", "in", "ref",
                "true", "false", "Some", "None", "Ok", "Err", "async", "await", "move")
            SyntaxLanguage.RUBY -> setOf("def", "end", "class", "module", "if", "else", "elsif", "unless",
                "while", "for", "do", "yield", "return", "puts", "attr_accessor",
                "require", "include", "extend", "true", "false", "nil", "self", "raise")
            SyntaxLanguage.PHP -> javascript + setOf("<?php", "function", "echo", "foreach", "array", "$this")
            SyntaxLanguage.SWIFT -> kotlin + setOf("guard", "let", "protocol", "extension", "struct", "enum")
            SyntaxLanguage.SHELL -> setOf("echo", "if", "then", "else", "fi", "for", "do", "done", "while",
                "case", "esac", "function", "return", "exit", "export", "source",
                "alias", "readonly", "local", "declare", "true", "false")
            SyntaxLanguage.DART -> kotlin + setOf("var", "final", "const", "class", "extends", "with", "implements",
                "async", "await", "yield", "required", "factory", "static", "late")
            SyntaxLanguage.VUE -> javascript // Vue uses JS syntax in script blocks
            SyntaxLanguage.SQL -> setOf("SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP",
                "ALTER", "TABLE", "INDEX", "VIEW", "JOIN", "INNER", "OUTER", "LEFT", "RIGHT",
                "ON", "AND", "OR", "NOT", "IN", "EXISTS", "BETWEEN", "LIKE", "ORDER", "BY",
                "GROUP", "HAVING", "LIMIT", "OFFSET", "UNION", "ALL", "AS", "DISTINCT",
                "COUNT", "SUM", "AVG", "MIN", "MAX", "SET", "VALUES", "INT", "VARCHAR",
                "TEXT", "BOOLEAN", "DATE", "TIMESTAMP", "NULL", "TRUE", "FALSE", "PRIMARY",
                "KEY", "FOREIGN", "REFERENCES", "CASCADE", "DEFAULT", "CHECK", "UNIQUE")
            else -> emptySet()
        }
    }
}

/**
 * Simple syntax highlighter that applies color spans based on language keywords,
 * strings, comments, and numbers. Uses regex-free character scanning for safety.
 */
@Composable
fun SyntaxHighlightedText(
    content: String,
    language: SyntaxLanguage,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val baseColor = if (isDarkTheme) Color(0xFFABB2BF) else Color(0xFF383A42)
    val annotatedContent = remember(content, language, isDarkTheme) {
        highlightSyntax(content, language, baseColor)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = annotatedContent,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )
    }
}

/**
 * Produce an AnnotatedString with syntax highlighting.
 * Simple token-based approach: scan for keywords, strings, comments, numbers.
 */
private fun highlightSyntax(
    content: String,
    language: SyntaxLanguage,
    baseColor: Color
): AnnotatedString {
    if (language == SyntaxLanguage.PLAIN_TEXT || language == SyntaxLanguage.MARKDOWN) {
        return buildAnnotatedString {
            withStyle(SpanStyle(color = baseColor)) { append(content) }
        }
    }

    val keywords = SyntaxKeywords.forLanguage(language)
    val lines = content.lines()

    return buildAnnotatedString {
        lines.forEachIndexed { lineIndex, line ->
            if (lineIndex > 0) append('\n')
            val i = 0
            var pos = 0
            while (pos < line.length) {
                // Single-line comments
                val commentStart = when (language) {
                    SyntaxLanguage.PYTHON, SyntaxLanguage.RUBY, SyntaxLanguage.SHELL -> if (line[pos] == '#') pos else -1
                    SyntaxLanguage.C_CPP, SyntaxLanguage.JAVA, SyntaxLanguage.KOTLIN, SyntaxLanguage.JAVASCRIPT,
                    SyntaxLanguage.TYPESCRIPT, SyntaxLanguage.GO, SyntaxLanguage.RUST, SyntaxLanguage.SWIFT,
                    SyntaxLanguage.CSS, SyntaxLanguage.DART, SyntaxLanguage.VUE -> {
                        if (pos + 1 < line.length && line[pos] == '/' && line[pos + 1] == '/') pos
                        else -1
                    }
                    SyntaxLanguage.SQL -> if (pos + 1 < line.length && line[pos] == '-' && line[pos + 1] == '-') pos else -1
                    SyntaxLanguage.HTML, SyntaxLanguage.XML -> -1
                    else -> -1
                }
                if (commentStart >= 0) {
                    withStyle(SpanStyle(color = SyntaxColors.comment)) {
                        append(line.substring(pos))
                    }
                    pos = line.length
                    continue
                }

                // Strings
                if (line[pos] == '"' || line[pos] == '\'') {
                    val quote = line[pos]
                    val start = pos
                    pos++
                    while (pos < line.length && line[pos] != quote) {
                        if (line[pos] == '\\' && pos + 1 < line.length) pos++
                        pos++
                    }
                    if (pos < line.length) pos++ // closing quote
                    withStyle(SpanStyle(color = SyntaxColors.string)) {
                        append(line.substring(start, pos))
                    }
                    continue
                }

                // Numbers
                if (line[pos].isDigit() || (line[pos] == '-' && pos + 1 < line.length && line[pos + 1].isDigit())) {
                    val start = pos
                    if (line[pos] == '-') pos++
                    while (pos < line.length && (line[pos].isDigit() || line[pos] == '.')) pos++
                    withStyle(SpanStyle(color = SyntaxColors.number)) {
                        append(line.substring(start, pos))
                    }
                    continue
                }

                // HTML/XML tags
                if ((language == SyntaxLanguage.HTML || language == SyntaxLanguage.XML) && line[pos] == '<') {
                    val start = pos
                    while (pos < line.length && line[pos] != '>') pos++
                    if (pos < line.length) pos++ // closing >
                    withStyle(SpanStyle(color = SyntaxColors.tag)) {
                        append(line.substring(start, pos))
                    }
                    continue
                }

                // Words (identifiers / keywords)
                if (line[pos].isLetter() || line[pos] == '_') {
                    val start = pos
                    while (pos < line.length && (line[pos].isLetterOrDigit() || line[pos] == '_')) pos++
                    val word = line.substring(start, pos)
                    if (keywords.contains(word)) {
                        withStyle(SpanStyle(color = SyntaxColors.keyword, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)) {
                            append(word)
                        }
                    } else {
                        withStyle(SpanStyle(color = baseColor)) {
                            append(word)
                        }
                    }
                    continue
                }

                // Default character
                withStyle(SpanStyle(color = SyntaxColors.punctuation)) {
                    append(line[pos])
                }
                pos++
            }
        }
    }
}
