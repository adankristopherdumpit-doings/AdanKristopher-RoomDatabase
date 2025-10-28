package ph.edu.comteq.adankristopher_roomdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.query
import ph.edu.comteq.adankristopher_roomdatabase.ui.theme.AdanKristopherRoomDatabaseTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdanKristopherRoomDatabaseTheme {
                var searchQuery by remember { mutableStateListOf("") }
                var isSearchActive by remember { mutableStateOf(false) }
                val notes by viewModel.allNotes.collectAsState(initial = emptyList())
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    if (isSearchActive) {
                        SearchBar(modifier = Modifier.fillMaxWidth(),
                            inputField = {

                            },
                            expanded = true,
                            onExpandedChange = { shouldExpand ->
                                // Handle when SearchBar wants to change expanded state
                                if (!shouldExpand) {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }
                            }
                        ) {
// content shown inside the search view
                        }

                        Inside the inputField:
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                viewModel.updateSearchQuery(it)
                            },
                            onSearch = {},
                            expanded = true,
                            onExpandedChange = { shouldExpand ->
                                // This is called when the system wants to change expanded state
                                if (!shouldExpand) {
                                    // User wants to collapse/exit search
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }
                            },
                            placeholder = {Text("Search notes...")},
                            leadingIcon = {
                                IconButton(onClick = {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
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
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            }
                        )
                                )
                            },
                            expanded = true,
                            onExpandedChange = { shouldExpand ->
                                if (!shouldExpand) {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }

                            }
                        ) {
                }
                    } else{
                            TopAppBar(
                                title = { Text("Notes") },
                                actions = {
                                    IconButton(onClick = {
                                        isSearchActive = true
                                    }) {
                                        Icon(Icons.Filled.Search, "Search")
                                    }
                                }
                            )
                    }
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(Icons.Filled.Add, "Add Note")
                        }
                    }

                ) { innerPadding ->
                    NotesListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NotesListScreen(viewModel: NoteViewModel, modifier: Modifier){
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    LazyColumn (modifier = Modifier){
        items(notes) { note ->
            NoteCard(note)
        }
    }
}

@Composable
fun NoteCard(note: Note, modifier: Modifier = Modifier){
    Card (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = DateUtils.formatDate(note.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = note.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

















//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AdanKristopherRoomDatabaseTheme {
//        NotesListScreen()
//    }
//}