package ph.edu.comteq.adankristopher_roomdatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val noteDao: NoteDao = AppDatabase.getDatabase(application).noteDao()

    // Track search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Call this when user types in search box
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Call this to clear the search
    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Combine notes and search query
    val allNotesWithTags: StateFlow<List<NoteWithTags>> =
        combine(noteDao.getAllNotesWithTags(), _searchQuery) { notes, query ->
            if (query.isBlank()) {
                notes
            } else {
                notes.filter { noteWithTags ->
                    noteWithTags.note.title.contains(query, ignoreCase = true) ||
                            noteWithTags.note.content.contains(query, ignoreCase = true) ||
                            noteWithTags.tags.any { it.name.contains(query, ignoreCase = true) } ||
                            noteWithTags.note.category.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Simple flow for all tags
    val allTags: Flow<List<Tag>> = noteDao.getAllTags()

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    fun updateNoteAndTags(note: Note, selectedTags: List<Tag>) = viewModelScope.launch {
        noteDao.updateNote(note)
        val existingCrossRefs = noteDao.getNoteTagCrossReferences(note.id)
        existingCrossRefs.forEach { crossRef ->
            noteDao.deleteNoteTagCrossRef(crossRef)
        }
        selectedTags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(note.id, tag.id))
        }
    }

    fun insertNoteReturningId(note: Note, tags: List<Tag>) = viewModelScope.launch {
        val noteId = noteDao.insertNoteReturningId(note).toInt()
        tags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tag.id))
        }
    }

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun getNoteWithTags(noteId: Int): NoteWithTags? = noteDao.getNoteWithTags(noteId)

    fun insertTag(tag: Tag) = viewModelScope.launch {
        noteDao.insertTag(tag)
    }

    fun updateTag(tag: Tag) = viewModelScope.launch {
        noteDao.updateTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        noteDao.deleteTag(tag)
    }

    fun addTagToNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun removeTagFromNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun getNotesWithTag(tagId: Int): Flow<List<Note>> = noteDao.getNotesWithTag(tagId)

    fun insertNoteWithTags(note: Note, tags: List<Tag>) = viewModelScope.launch {
        val noteId = noteDao.insertNoteReturningId(note).toInt()
        tags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tag.id))
        }
    }
}