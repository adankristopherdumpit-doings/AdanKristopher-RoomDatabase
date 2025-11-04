package ph.edu.comteq.adankristopher_roomdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.adankristopher_roomdatabase.DateUtils.formatDateTime
import ph.edu.comteq.adankristopher_roomdatabase.ui.theme.AdanKristopherRoomDatabaseTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdanKristopherRoomDatabaseTheme {
                var searchQuery by remember { mutableStateOf("") }
                var isSearchActive by remember { mutableStateOf(false) }
                var isAddNoteDialogVisible by remember { mutableStateOf(false) }

                val allTags by viewModel.allTags.collectAsState(initial = emptyList())
                val notes by viewModel.allNotesWithTags.collectAsState(initial = emptyList())
                var noteToEdit by remember { mutableStateOf<NoteWithTags?>(null) }

                Scaffold(
                    topBar = {
                        if (isSearchActive) {
                            SearchBar(
                                modifier = Modifier.fillMaxWidth(),
                                query = searchQuery,
                                onQueryChange = {
                                    searchQuery = it
                                    viewModel.updateSearchQuery(it)
                                },
                                onSearch = { isSearchActive = false },
                                active = isSearchActive,
                                onActiveChange = { active ->
                                    if (!active) {
                                        isSearchActive = false
                                        searchQuery = ""
                                        viewModel.clearSearch()
                                    }
                                },
                                placeholder = { Text("Search notes...") },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        isSearchActive = false
                                        searchQuery = ""
                                        viewModel.clearSearch()
                                    }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Close search"
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = {
                                            searchQuery = ""
                                            viewModel.clearSearch()
                                        }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                        }
                                    }
                                }
                            ) {
                                SearchResultsList(notes = notes, searchQuery = searchQuery)
                            }
                        } else {
                            TopAppBar(
                                title = { Text("Notes") },
                                actions = {
                                    IconButton(onClick = { isSearchActive = true }) {
                                        Icon(Icons.Filled.Search, contentDescription = "Search")
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { isAddNoteDialogVisible = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add note")
                        }
                    }
                ) { innerPadding ->
                    NoteListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onEditNote = { noteWithTags ->
                            noteToEdit = noteWithTags
                            isAddNoteDialogVisible = true
                        },
                        onDeleteNote = { note ->
                            viewModel.deleteNote(note)
                        }
                    )
                }

                if (isAddNoteDialogVisible) {
                    AddNoteDialog(
                        availableTags = allTags,
                        existingNote = noteToEdit,
                        onDismiss = {
                            isAddNoteDialogVisible = false
                            noteToEdit = null
                        },
                        onSave = { title, content, category, selectedTags ->
                            if (noteToEdit == null) {
                                // ADD MODE
                                viewModel.insertNoteReturningId(
                                    Note(title = title, content = content, category = category),
                                    selectedTags
                                )
                            } else {
                                // EDIT MODE
                                viewModel.updateNoteAndTags(
                                    noteToEdit!!.note.copy(
                                        title = title,
                                        content = content,
                                        category = category
                                    ),
                                    selectedTags
                                )
                            }
                            isAddNoteDialogVisible = false
                            noteToEdit = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(notes: List<NoteWithTags>, searchQuery: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (notes.isEmpty() && searchQuery.isNotEmpty()) {
            item {
                Text(
                    text = "No notes found",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            items(notes) { note ->
                NoteCard(
                    note = note.note,
                    tags = note.tags,
                    onEdit = { /* Optional edit from search */ },
                    onDelete = { /* Optional delete from search */ }
                )
            }
        }
    }
}

@Composable
fun NoteListScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier,
    onEditNote: (NoteWithTags) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val notesWithTags by viewModel.allNotesWithTags.collectAsState(initial = emptyList())

    LazyColumn(modifier = modifier) {
        items(notesWithTags) { noteWithTags ->
            NoteCard(
                note = noteWithTags.note,
                tags = noteWithTags.tags,
                onEdit = { onEditNote(noteWithTags) },
                onDelete = { onDeleteNote(it) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteCard(
    note: Note,
    modifier: Modifier = Modifier,
    tags: List<Tag> = emptyList(),
    onEdit: (NoteWithTags) -> Unit = {},
    onDelete: (Note) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEdit(NoteWithTags(note, tags)) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row - Date + Delete Button only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDateTime(note.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                IconButton(
                    onClick = { onDelete(note) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Category
            if (note.category.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = note.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Title
            Text(
                text = note.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Content
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }

            // Tags
            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = tag.name,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

// Simple date formatter
fun formatDateTime(date: Date): String {
    return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", date).toString()
}
