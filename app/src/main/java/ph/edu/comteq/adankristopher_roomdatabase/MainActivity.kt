package ph.edu.comteq.adankristopher_roomdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.adankristopher_roomdatabase.ui.theme.AdanKristopherRoomDatabaseTheme

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
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                if (isAddNoteDialogVisible) {
                    AddNoteDialog(
                        availableTags = allTags,
                        onDismiss = { isAddNoteDialogVisible = false },
                        onSave = { title, content, category, selectedTags ->
                            viewModel.insertNoteReturningId(
                                Note(title = title, content = content, category = category),
                                selectedTags
                            )
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
                NoteCard(note = note.note, tags = note.tags)
            }
        }
    }
}

@Composable
fun NoteListScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier) {
    // Get all notes from viewmodel
    val notesWithTags by viewModel.allNotesWithTags.collectAsState(initial = emptyList())

    LazyColumn(modifier = modifier) {
        items(notesWithTags) { noteWithTags ->
            NoteCard(note = noteWithTags.note, tags = noteWithTags.tags)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteCard(note: Note, modifier: Modifier = Modifier, tags: List<Tag> = emptyList()) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between elements
        ) {
            Text(
                text = DateUtils.formatDateTime(note.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
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
            Text(
                text = note.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // Display tags using a FlowRow
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
            // You can also add note.content here if you want
            // Text(text = note.content)
        }
    }
}
