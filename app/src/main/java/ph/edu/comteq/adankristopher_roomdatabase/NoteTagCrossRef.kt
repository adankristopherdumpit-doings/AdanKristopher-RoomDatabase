package ph.edu.comteq.adankristopher_roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["noteId", "tagId"]
)

data class NoteTagCrossRef(
    @ColumnInfo(name = "note_id")
    val noteId: Int,
    @ColumnInfo(name = "tag_id")
    val tagId: Int
)
