package com.zdocs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zdocs.data.model.ZFile

/**
 * Dialog for renaming a file.
 * Pre-fills with the current filename and allows changing it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameFileDialog(
    zFile: ZFile,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newName by remember { mutableStateOf(zFile.name) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Rename File",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        isError = it.isBlank() || it.contains("/")
                    },
                    label = { Text("File name") },
                    singleLine = true,
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("File name cannot be empty or contain /") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Location: ${zFile.type.displayName} folder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (!isError && newName.isNotBlank()) {
                                onRename(newName)
                            }
                        },
                        enabled = !isError && newName.isNotBlank() && newName != zFile.name
                    ) {
                        Text("Rename")
                    }
                }
            }
        }
    }
}
