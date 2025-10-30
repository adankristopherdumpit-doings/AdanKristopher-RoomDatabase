package ph.edu.comteq.adankristopher_roomdatabase

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithTags(
    @Embedded val note: Note,

    @Relation(
        parentColumn = "id",            // Note's primary key
        entityColumn = "id",            // Tag's primary key
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",   // CrossRef column
            entityColumn = "tag_id"     // CrossRef column
        )
    )
    val tags: List<Tag>
)

