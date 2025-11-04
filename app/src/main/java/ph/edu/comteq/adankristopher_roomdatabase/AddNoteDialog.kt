package ph.edu.comteq.adankristopher_roomdatabase

import androidx.compose.foundation.layout.Arrangement // Import Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddNoteDialog(
    availableTags: List<Tag>,
    existingNote: NoteWithTags? = null, // 👈 Optional for Edit Mode
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, selectedTags: List<Tag>) -> Unit
) {
    // If editing, prefill fields from the existing note
    var title by remember { mutableStateOf(existingNote?.note?.title ?: "") }
    var content by remember { mutableStateOf(existingNote?.note?.content ?: "") }
    var category by remember { mutableStateOf(existingNote?.note?.category ?: "") }

    // Selected tags – pre-select those already linked to this note if in edit mode
    val selectedTags = remember {
        mutableStateListOf<Tag>().apply {
            existingNote?.tags?.let { addAll(it) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingNote == null) "Add Note" else "Edit Note") // 👈 Dynamic title
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Tags:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag)
                                else selectedTags.add(tag)
                            },
                            label = { Text(tag.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(tag.color.toColorInt())
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(title, content, category, selectedTags)
                onDismiss()
            }) {
                Text(if (existingNote == null) "Save" else "Update") // 👈 Dynamic button text
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
