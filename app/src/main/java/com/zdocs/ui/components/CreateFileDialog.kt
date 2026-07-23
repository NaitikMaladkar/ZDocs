package com.zdocs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zdocs.data.model.FileType

/**
 * Category types shown in the first step of file creation.
 */
enum class CreateCategory(
    val displayName: String,
    val fileType: FileType?,
    val color: androidx.compose.ui.graphics.Color,
    val icon: ImageVector,
    val availableExtensions: List<String>
) {
    TEXT_DOCUMENT(
        displayName = "Text Document",
        fileType = FileType.DOCUMENT,
        color = FileType.DOCUMENT.color,
        icon = Icons.Filled.Description,
        availableExtensions = listOf("docx", "odt", "txt", "md", "rtf", "doc")
    ),
    SPREADSHEET(
        displayName = "Spreadsheet",
        fileType = FileType.SPREADSHEET,
        color = FileType.SPREADSHEET.color,
        icon = Icons.Filled.TableChart,
        availableExtensions = listOf("xlsx", "xls", "csv", "ods", "tsv")
    ),
    PRESENTATION(
        displayName = "Presentation",
        fileType = FileType.PRESENTATION,
        color = FileType.PRESENTATION.color,
        icon = Icons.Filled.Slideshow,
        availableExtensions = listOf("pptx", "odp", "ppt")
    ),
    PDF(
        displayName = "PDF",
        fileType = FileType.PDF,
        color = FileType.PDF.color,
        icon = Icons.Filled.PictureAsPdf,
        availableExtensions = listOf("pdf")
    ),
    CODE(
        displayName = "Code",
        fileType = FileType.CODE,
        color = FileType.CODE.color,
        icon = Icons.Filled.Code,
        availableExtensions = listOf(
            "py", "js", "ts", "tsx", "html", "css", "java", "kt", "cpp", "c",
            "go", "rs", "rb", "php", "sql", "swift", "dart", "vue", "sh", "bat",
            "json", "xml", "yaml", "yml", "md"
        )
    ),
    CUSTOM(
        displayName = "Custom",
        fileType = null,
        color = androidx.compose.ui.graphics.Color(0xFF546E7A),
        icon = Icons.Filled.Add,
        availableExtensions = emptyList()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreate: (fileName: String, fileType: FileType, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<CreateCategory?>(null) }
    var fileName by remember { mutableStateOf("") }
    var selectedExtension by remember { mutableStateOf("") }
    var expandedExtension by remember { mutableStateOf(false) }

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
                // Header with close/back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == null) "Create New File" else selectedCategory!!.displayName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = {
                        if (selectedCategory != null) {
                            selectedCategory = null
                            fileName = ""
                            selectedExtension = ""
                        } else {
                            onDismiss()
                        }
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedCategory == null) {
                    // Step 1: Show category options as rectangular cards in a grid
                    Text(
                        text = "Choose a type",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CreateCategory.entries) { category ->
                            CategoryCard(
                                category = category,
                                onClick = {
                                    selectedCategory = category
                                    // Set default extension for the category
                                    selectedExtension = category.availableExtensions.firstOrNull() ?: "txt"
                                }
                            )
                        }
                    }
                } else if (selectedCategory == CreateCategory.CUSTOM) {
                    // Custom file: name.ext pattern input
                    Text(
                        text = "Enter file name with extension",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File name (e.g., myfile.py)") },
                        placeholder = { Text("name.ext") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No name = \"New File\" (auto-numbered). No extension = default .txt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            selectedCategory = null
                            fileName = ""
                            selectedExtension = ""
                        }) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val finalName = if (fileName.isBlank()) {
                                    "New File.txt"
                                } else if (fileName.contains(".")) {
                                    fileName
                                } else {
                                    "$fileName.txt"
                                }
                                val fileType = FileType.fromFileName(finalName)
                                onCreate(finalName, fileType, "")
                            }
                        ) {
                            Text("Create")
                        }
                    }
                } else {
                    // Step 2: Show filename input + extension selector + Create button
                    Text(
                        text = "File name",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File name") },
                        placeholder = {
                            Text(
                                if (fileName.isBlank()) "New File" else fileName
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Leave empty for \"New File\" (auto-numbered if duplicate)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Choose type",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Extension selector dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedExtension = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = selectedCategory!!.icon,
                                        contentDescription = null,
                                        tint = selectedCategory!!.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = ".${selectedExtension}",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                                Text(
                                    text = "${selectedCategory!!.availableExtensions.size} types",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedExtension,
                            onDismissRequest = { expandedExtension = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            selectedCategory!!.availableExtensions.forEach { ext ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = ".${ext}",
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                                color = selectedCategory!!.color
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = getExtDescription(ext),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedExtension = ext
                                        expandedExtension = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = selectedCategory!!.icon,
                                            contentDescription = null,
                                            tint = selectedCategory!!.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            selectedCategory = null
                            fileName = ""
                            selectedExtension = ""
                        }) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val baseName = if (fileName.isBlank()) "New File" else fileName
                                val finalName = "$baseName.$selectedExtension"
                                val fileType = FileType.fromFileName(finalName)
                                onCreate(finalName, fileType, "")
                            }
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CreateCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.displayName,
                    tint = category.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                if (category.availableExtensions.isNotEmpty()) {
                    Text(
                        text = ".${category.availableExtensions.first()} +${category.availableExtensions.size - 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "name.ext pattern",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun getExtDescription(ext: String): String {
    return when (ext) {
        "docx" -> "Word Document"
        "odt" -> "OpenDocument Text"
        "txt" -> "Plain Text"
        "md" -> "Markdown"
        "rtf" -> "Rich Text Format"
        "doc" -> "Word Legacy"
        "xlsx" -> "Excel Workbook"
        "xls" -> "Excel Legacy"
        "csv" -> "Comma-Separated"
        "ods" -> "OpenDocument Sheet"
        "tsv" -> "Tab-Separated"
        "pptx" -> "PowerPoint"
        "odp" -> "OpenDocument Pres."
        "ppt" -> "PowerPoint Legacy"
        "pdf" -> "PDF Document"
        "py" -> "Python"
        "js" -> "JavaScript"
        "ts" -> "TypeScript"
        "tsx" -> "React TSX"
        "html" -> "HTML"
        "css" -> "CSS"
        "java" -> "Java"
        "kt" -> "Kotlin"
        "cpp" -> "C++"
        "c" -> "C"
        "go" -> "Go"
        "rs" -> "Rust"
        "rb" -> "Ruby"
        "php" -> "PHP"
        "sql" -> "SQL"
        "swift" -> "Swift"
        "dart" -> "Dart"
        "vue" -> "Vue"
        "sh" -> "Shell Script"
        "bat" -> "Batch Script"
        "json" -> "JSON"
        "xml" -> "XML"
        "yaml" -> "YAML"
        "yml" -> "YAML"
        else -> ext.uppercase()
    }
}
