package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.models.LocalNote
import com.gquesada.notes.data.models.LocalTag

class LocalNoteDataSource {

    private val notes = mutableListOf(
        LocalNote(
            id = 1,
            title = "Android Architecture",
            description = "Enseñar la estructura de una arquitectura para un Android App",
            tag = LocalTag(
                id = 1,
                title = "Cenfotec"
            ),
            date = 1685660673
        ),
        LocalNote(
            id = 2,
            title = "Android Dabatabase",
            description = "Enseñar como montar una base de datos en Android",
            tag = LocalTag(
                id = 1,
                title = "Cenfotec"
            ),
            date = 1685660673
        ),
        LocalNote(
            id = 3,
            title = "Ordenar Cuarto",
            description = null,
            tag = LocalTag(
                id = 2,
                title = "Hogar"
            ),
            date = 1685660673
        ),
        LocalNote(
            id = 4,
            title = "Retrofit",
            description = "Enseñar como consumir Servicios web desde Android utilizando la libreria Retrofit",
            tag = LocalTag(
                id = 1,
                title = "Cenfotec"
            ),
            date = 1685660673
        ),
    )

    fun getAllNotes(): List<LocalNote> = notes

    fun addNote(localNote: LocalNote) {
        notes.add(localNote)
    }

    fun deleteNote(id: Int) {
        notes.removeIf { note -> note.id == id }
    }

    fun updateNote(note: LocalNote) {
        val index = notes.indexOfFirst { item -> item.id == note.id }
        notes[index] = note
    }
}