package com.zdocs.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Spreadsheet grid editor for CSV files.
 * Displays data in an editable grid with add/delete row/column support.
 */
@Composable
fun SpreadsheetEditor(
    data: List<List<String>>,
    onCellChange: (row: Int, col: Int, value: String) -> Unit,
    onAddRow: () -> Unit,
    onAddColumn: () -> Unit,
    onDeleteRow: (Int) -> Unit,
    onDeleteColumn: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxCols = data.maxOfOrNull { it.size } ?: 1
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onAddRow, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Add, "Add Row", tint = MaterialTheme.colorScheme.primary)
            }
            Text("Row", modifier = Modifier.align(Alignment.CenterVertically), style = MaterialTheme.typography.labelSmall)

            IconButton(onClick = onAddColumn, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Add, "Add Column", tint = MaterialTheme.colorScheme.primary)
            }
            Text("Col", modifier = Modifier.align(Alignment.CenterVertically), style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${data.size} rows × $maxCols cols",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        ) {
            Column {
                // Header row with column indices and delete buttons
                Row {
                    // Row number corner cell
                    Box(
                        modifier = Modifier
                            .size(48.dp, 36.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("#", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    for (col in 0 until maxCols) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(36.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    getColumnName(col),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (maxCols > 1) {
                                    IconButton(
                                        onClick = { onDeleteColumn(col) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            "Delete Column",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Data rows
                data.forEachIndexed { rowIndex, row ->
                    Row {
                        // Row number with delete button
                        Box(
                            modifier = Modifier
                                .size(48.dp, 40.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${rowIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Cells
                        for (col in 0 until maxCols) {
                            val cellValue = row.getOrElse(col) { "" }
                            SpreadsheetCell(
                                value = cellValue,
                                onValueChange = { onCellChange(rowIndex, col, it) },
                                isFirstRow = rowIndex == 0
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single editable cell in the spreadsheet.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpreadsheetCell(
    value: String,
    onValueChange: (String) -> Unit,
    isFirstRow: Boolean,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(value) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .width(120.dp)
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .combinedClickable(
                onClick = {
                    isEditing = true
                    editValue = value
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = editValue,
                onValueChange = { editValue = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            )
            LaunchedEffect(isEditing) {
                if (isEditing) {
                    focusRequester.requestFocus()
                }
            }
            // Auto-save on losing focus concept - simplified for now
            DisposableEffect(isEditing) {
                onDispose {
                    onValueChange(editValue)
                }
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = if (isFirstRow) FontWeight.SemiBold else FontWeight.Normal
                ),
                modifier = Modifier.padding(horizontal = 6.dp),
                maxLines = 1,
                color = if (isFirstRow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Convert column index to letter (A, B, C, ... Z, AA, AB, ...).
 */
private fun getColumnName(index: Int): String {
    if (index < 0) return ""
    var result = ""
    var n = index
    do {
        result = ('A' + (n % 26)) + result
        n = n / 26 - 1
    } while (n >= 0)
    return result
}
