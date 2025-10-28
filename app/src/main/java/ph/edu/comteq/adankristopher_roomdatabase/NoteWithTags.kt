package ph.edu.comteq.adankristopher_roomdatabase

import android.nfc.Tag
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithTags(
    @Embedded val note: Note,

    @Relation(
        parentColumn = "id",
        entityColumn = "note_id",
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
)

    val tags: List<Tag>
)
