package ph.edu.comteq.adankristopher_roomdatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val noteDao: NoteDao = AppDatabase.getDatabase(application).noteDao()

    // Track what the user is searching for
    private val _searchQuery = MutableStateFlow("")

    val allNotes: Flow<List<Note>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            noteDao.getALlNotes()
        } else {
            noteDao.searchNotes(query)
        }
    }

    // ADD THESE MISSING FUNCTIONS:
    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    fun updateNoteAndTags(note: Note, selectedTags: List<Tag>) = viewModelScope.launch {
        // 1. Update the note itself
        noteDao.updateNote(note)

        // 2. Update the tags - remove all existing and add new ones
        // First, remove all existing tag connections
        val existingCrossRefs = noteDao.getNoteTagCrossReferences(note.id)
        existingCrossRefs.forEach { crossRef ->
            noteDao.deleteNoteTagCrossRef(crossRef)
        }

        // Then, add the new tag connections
        selectedTags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(note.id, tag.id))
        }
    }

    fun insertNoteReturningId(note: Note, tags: List<Tag>) = viewModelScope.launch {
        // 1️⃣ Insert the note and get its generated ID
        val noteId = noteDao.insertNoteReturningId(note).toInt()

        // 2️⃣ Connect tags to the note
        tags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tag.id))
        }
    }

    // Call this when user types in search box
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Call this to clear the search
    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insertNoteReturningId(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    // NEW: All notes WITH their tags
    val allNotesWithTags: Flow<List<NoteWithTags>> = noteDao.getAllNotesWithTags()

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun getNoteWithTags(noteId: Int): NoteWithTags? {
        return noteDao.getNoteWithTags(noteId)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch {
        noteDao.insertTag(tag)
    }

    fun updateTag(tag: Tag) = viewModelScope.launch {
        noteDao.updateTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        noteDao.deleteTag(tag)
    }

    // Add a tag to a note
    fun addTagToNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    // Remove a tag from a note
    fun removeTagFromNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    // Get all notes that have a specific tag
    fun getNotesWithTag(tagId: Int): Flow<List<Note>> {
        return noteDao.getNotesWithTag(tagId)
    }

    fun insertNoteWithTags(note: Note, tags: List<Tag>) = viewModelScope.launch {
        val noteId = noteDao.insertNoteReturningId(note).toInt()
        tags.forEach { tag ->
            noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tag.id))
        }
    }

    val allTags: Flow<List<Tag>> = noteDao.getAllTags()
}