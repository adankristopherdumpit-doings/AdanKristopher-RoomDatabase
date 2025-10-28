package ph.edu.comteq.adankristopher_roomdatabase

import android.R.attr.tag
import android.app.Application
import android.nfc.Tag
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allNotes: Flow<List<Note>> = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            noteDao.getAllNotes()  // Show everything
        } else {
            noteDao.searchNotes(query)  // Show only matches
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch(){
        _searchQuery.value = ""
    }

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insertNote(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }
}


    val allNotesWithTags: Flow<List<NoteWithTags>> = NoteDao.getNotesWithTags()

    suspend fun getNoteWithTags(noteId: Int): NoteWithTags? {
        return NoteDao.getNotesWithTags(noteId)
    }
fun insertTag(tag: Tag) = viewModelScope.launch {
    NoteDao.insertTag(tag)
}

fun updateTag(Tag) = viewModelScope.launch {
    NoteDao.updateTag(tag)
}

fun deleteTag(tag: Tag) = viewModelScope.launch {
    NoteDao.deleteTag(tag)
}
