package com.zdocs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zdocs.data.model.SyntaxLanguage
import com.zdocs.data.model.ZFile
import com.zdocs.ui.components.SpreadsheetEditor

data class UndoRedoState(
    val history: List<String> = emptyList(),
    val currentIndex: Int = -1
) {
    val canUndo: Boolean get() = currentIndex > 0
    val canRedo: Boolean get() = currentIndex < history.size - 1
    val current: String get() = if (currentIndex >= 0 && currentIndex < history.size) history[currentIndex] else ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    zFile: ZFile,
    content: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    isDarkTheme: Boolean = false,
    csvData: List<List<String>>? = null,
    onCsvDataChange: (List<List<String>>) -> Unit = {},
    isExternalFile: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isSpreadsheet = zFile.extension.lowercase() in listOf("csv", "tsv")
    val syntaxLanguage = SyntaxLanguage.fromExtension(zFile.extension)
    val isCodeFile = syntaxLanguage != SyntaxLanguage.PLAIN_TEXT && syntaxLanguage != SyntaxLanguage.MARKDOWN

    // Undo/Redo state for text content
    var undoRedoState by remember(content) {
        mutableStateOf(UndoRedoState(history = listOf(content), currentIndex = 0))
    }

    // CSV undo/redo state
    var csvUndoRedoState by remember(csvData) {
        mutableStateOf(UndoRedoState(
            history = if (csvData != null) listOf(csvData.toString()) else emptyList(),
            currentIndex = 0
        ))
    }
    var currentCsvData by remember(csvData) { mutableStateOf(csvData) }

    fun pushTextHistory(newContent: String) {
        val newHistory = undoRedoState.history.take(undoRedoState.currentIndex + 1) + newContent
        // Keep max 50 history entries
        val trimmed = if (newHistory.size > 50) newHistory.takeLast(50) else newHistory
        val newIndex = if (newHistory.size > 50) 49 else newHistory.size - 1
        undoRedoState = UndoRedoState(history = trimmed, currentIndex = newIndex)
    }

    fun pushCsvHistory(newData: List<List<String>>) {
        val serialized = newData.toString()
        val newHistory = csvUndoRedoState.history.take(csvUndoRedoState.currentIndex + 1) + serialized
        val trimmed = if (newHistory.size > 50) newHistory.takeLast(50) else newHistory
        val newIndex = if (newHistory.size > 50) 49 else newHistory.size - 1
        csvUndoRedoState = UndoRedoState(history = trimmed, currentIndex = newIndex)
        currentCsvData = newData
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = zFile.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Auto-save on back
                        if (isSpreadsheet && currentCsvData != null) {
                            onCsvDataChange(currentCsvData!!)
                        } else {
                            onSave(undoRedoState.current)
                        }
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Undo button
                    IconButton(
                        onClick = {
                            if (undoRedoState.canUndo && !isSpreadsheet) {
                                undoRedoState = undoRedoState.copy(currentIndex = undoRedoState.currentIndex - 1)
                            }
                        },
                        enabled = if (isSpreadsheet) csvUndoRedoState.canUndo else undoRedoState.canUndo
                    ) {
                        Icon(
                            Icons.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if ((if (isSpreadsheet) csvUndoRedoState.canUndo else undoRedoState.canUndo))
                                MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    // Redo button
                    IconButton(
                        onClick = {
                            if (undoRedoState.canRedo && !isSpreadsheet) {
                                undoRedoState = undoRedoState.copy(currentIndex = undoRedoState.currentIndex + 1)
                            }
                        },
                        enabled = if (isSpreadsheet) csvUndoRedoState.canRedo else undoRedoState.canRedo
                    ) {
                        Icon(
                            Icons.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if ((if (isSpreadsheet) csvUndoRedoState.canRedo else undoRedoState.canRedo))
                                MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (isSpreadsheet && currentCsvData != null) {
            SpreadsheetEditor(
                data = currentCsvData!!,
                onCellChange = { row, col, value ->
                    val newData = currentCsvData!!.mapIndexed { r, rowData ->
                        if (r == row) {
                            val mutableRow = rowData.toMutableList()
                            while (mutableRow.size <= col) mutableRow.add("")
                            mutableRow[col] = value
                            mutableRow.toList()
                        } else rowData
                    }
                    pushCsvHistory(newData)
                    onCsvDataChange(newData)
                },
                onAddRow = {
                    val newRow = List(currentCsvData!!.firstOrNull()?.size ?: 1) { "" }
                    val newData = currentCsvData!! + listOf(newRow)
                    pushCsvHistory(newData)
                    onCsvDataChange(newData)
                },
                onAddColumn = {
                    val newData = currentCsvData!!.map { row -> row + "" }
                    pushCsvHistory(newData)
                    onCsvDataChange(newData)
                },
                onDeleteRow = { index ->
                    if (currentCsvData!!.size > 1) {
                        val newData = currentCsvData!!.filterIndexed { i, _ -> i != index }
                        pushCsvHistory(newData)
                        onCsvDataChange(newData)
                    }
                },
                onDeleteColumn = { index ->
                    if ((currentCsvData!!.firstOrNull()?.size ?: 0) > 1) {
                        val newData = currentCsvData!!.map { row ->
                            row.filterIndexed { i, _ -> i != index }
                        }
                        pushCsvHistory(newData)
                        onCsvDataChange(newData)
                    }
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            OutlinedTextField(
                value = undoRedoState.current,
                onValueChange = { newContent ->
                    pushTextHistory(newContent)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = if (isCodeFile) FontFamily.Monospace else FontFamily.SansSerif
                ),
                placeholder = { Text("Start typing...") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
