package com.gquesada.notes.data

import com.gquesada.notes.data.database.entities.NoteAndTag
import com.gquesada.notes.data.database.entities.NoteEntity
import com.gquesada.notes.data.database.entities.TagEntity
import com.gquesada.notes.data.datasources.DatabaseNoteDataSource
import com.gquesada.notes.data.datasources.DatabaseTagDataSource
import com.gquesada.notes.data.datasources.RemoteNoteDataSource
import com.gquesada.notes.data.network.models.RemoteNote
import com.gquesada.notes.data.network.models.RemoteTag
import com.gquesada.notes.data.repositories.NoteRepositoryImpl
import com.gquesada.notes.domain.models.NoteModel
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.NoteRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class NoteRepositoryTest {

    @MockK
    private lateinit var noteDataSource: DatabaseNoteDataSource

    @MockK
    private lateinit var remoteNoteDataSource: RemoteNoteDataSource

    @MockK
    private lateinit var tagDataSource: DatabaseTagDataSource

    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = NoteRepositoryImpl(
            noteDataSource = noteDataSource,
            remoteNoteDataSource = remoteNoteDataSource,
            tagDataSource = tagDataSource
        )
    }


    @Test
    fun `validate when notes can be retrieved from network`() = runTest {
        // Given
        //data de network
        val remoteData = listOf(
            RemoteNote(
                id = 1,
                title = "test",
                description = "description",
                dateCreated = 1,
                tag = RemoteTag(
                    id = 1,
                    name = "Test"
                )
            ),
            RemoteNote(
                id = 2,
                title = "test",
                description = "description",
                dateCreated = 1,
                tag = RemoteTag(
                    id = 2,
                    name = "Test"
                )
            ),
            RemoteNote(
                id = 3,
                title = "test",
                description = "description",
                dateCreated = 1,
                tag = RemoteTag(
                    id = 1,
                    name = "Test"
                )
            )
        )
        val localData = listOf(
            NoteAndTag(
                notes = listOf(
                    NoteEntity(
                        id = 1,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                    NoteEntity(
                        id = 3,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                ),
                tag = TagEntity(
                    id = 1,
                    name = "Test"
                )
            ),
            NoteAndTag(
                notes = listOf(
                    NoteEntity(
                        id = 2,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                ),
                tag = TagEntity(
                    id = 2,
                    name = "Test"
                )
            ),
        )
        // data retornada por el repository
        val expectedNotes = listOf(
            NoteModel(
                id = 1,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 1,
                    title = "Test"
                )
            ),
            NoteModel(
                id = 3,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 1,
                    title = "Test"
                )
            ),
            NoteModel(
                id = 2,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 2,
                    title = "Test"
                )
            ),
        )

        coEvery { remoteNoteDataSource.getNotes() } returns remoteData
        every { noteDataSource.getAllNotes() } returns flowOf(localData)
        coEvery { tagDataSource.insert(any<List<TagEntity>>()) } returns Unit
        coEvery { noteDataSource.addNote(any()) } returns 1

        // When
        val result = repository.getAllNotes().first()

        // Then
        assertEquals(expectedNotes, result)

        coVerify(exactly = 1) { remoteNoteDataSource.getNotes() }
        coVerify(exactly = 1) {
            tagDataSource.insert(
                listOf(
                    TagEntity(
                        id = 1,
                        name = "Test"
                    ),
                    TagEntity(
                        id = 2,
                        name = "Test"
                    ),
                )
            )
        }
        coVerify(exactly = 1) {
            noteDataSource.addNotes(
                listOf(
                    NoteEntity(
                        id = 1,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1,
                    ),
                    NoteEntity(
                        id = 2,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 2,
                    ),
                    NoteEntity(
                        id = 3,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1,
                    ),
                )
            )
        }
        verify(atLeast = 1) { noteDataSource.getAllNotes() }
    }

    @Test
    fun `validate notes can be retrieved locally when a communication error is thrown`() = runTest {
        // Given
        val localData = listOf(
            NoteAndTag(
                notes = listOf(
                    NoteEntity(
                        id = 1,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                    NoteEntity(
                        id = 3,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                ),
                tag = TagEntity(
                    id = 1,
                    name = "Test"
                )
            ),
            NoteAndTag(
                notes = listOf(
                    NoteEntity(
                        id = 2,
                        title = "test",
                        description = "description",
                        date = 1,
                        tagId = 1
                    ),
                ),
                tag = TagEntity(
                    id = 2,
                    name = "Test"
                )
            ),
        )
        // data retornada por el repository
        val expectedNotes = listOf(
            NoteModel(
                id = 1,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 1,
                    title = "Test"
                )
            ),
            NoteModel(
                id = 3,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 1,
                    title = "Test"
                )
            ),
            NoteModel(
                id = 2,
                title = "test",
                description = "description",
                date = 1,
                tag = TagModel(
                    id = 2,
                    title = "Test"
                )
            ),
        )

        coEvery { remoteNoteDataSource.getNotes() } throws Exception()
        every { noteDataSource.getAllNotes() } returns flowOf(localData)

        // When
        val result = repository.getAllNotes().first()

        // Then
        assertEquals(expectedNotes, result)

        coVerify(exactly = 1) { remoteNoteDataSource.getNotes() }
        coVerify(exactly = 0) { tagDataSource.insert(any<List<TagEntity>>()) }
        coVerify(exactly = 0) { noteDataSource.addNotes(any()) }
        verify(atLeast = 1) { noteDataSource.getAllNotes() }
    }


    @Test
    fun `validate getAllNotes fails when request is not authorized`() = runTest {
        // Given
        // data retornada por el repository
        val expectedException = mockk<HttpException>()
        every { expectedException.code() } returns 401

        coEvery { remoteNoteDataSource.getNotes() } throws expectedException

        // When
        val result = try {
            repository.getAllNotes().first()
        } catch (e: Exception) {
            e
        }

        // Then
        assertEquals(expectedException, result)

        coVerify(exactly = 1) { remoteNoteDataSource.getNotes() }
        coVerify(exactly = 0) { tagDataSource.insert(any<List<TagEntity>>()) }
        coVerify(exactly = 0) { noteDataSource.addNotes(any()) }
        verify(exactly = 0) { noteDataSource.getAllNotes() }
    }

    @Test
    fun `validate note is saved in server and locally`() = runTest {
        // Given
        val noteModel = NoteModel(
            id = 1,
            title = "test",
            description = "description",
            date = 1,
            tag = TagModel(
                id = 1,
                title = "Test"
            )
        )
        val remoteNote = RemoteNote(
            id = 1,
            title = "test",
            description = "description",
            dateCreated = 1,
            tag = RemoteTag(
                id = 1,
                name = "Test"
            )
        )
        val localNote = NoteEntity(
            id = 1,
            title = "test",
            description = "description",
            date = 1,
            tagId = 1,
        )

        val expected = 1L

        coEvery { remoteNoteDataSource.insert(any()) } returns Unit
        coEvery { noteDataSource.addNote(any()) } returns expected

        // When
        val result = repository.addNote(noteModel)

        // Then
        assertEquals(expected, result)

        coVerify(exactly = 1) { remoteNoteDataSource.insert(remoteNote) }
        coVerify(exactly = 1) { noteDataSource.addNote(localNote) }
    }

    @Test
    fun `validate note is not saved locally when server request fails`() = runTest {
        // Given
        val noteModel = NoteModel(
            id = 1,
            title = "test",
            description = "description",
            date = 1,
            tag = TagModel(
                id = 1,
                title = "Test"
            )
        )
        val remoteNote = RemoteNote(
            id = 1,
            title = "test",
            description = "description",
            dateCreated = 1,
            tag = RemoteTag(
                id = 1,
                name = "Test"
            )
        )

        val expected = Exception()

        coEvery { remoteNoteDataSource.insert(remoteNote) } throws expected

        // When
        val result = try {
            repository.addNote(noteModel)
        } catch (e: Exception) {
            e
        }

        assertEquals(expected, result)
        coVerify(exactly = 1) { remoteNoteDataSource.insert(remoteNote) }
        coVerify(exactly = 0) { noteDataSource.addNote(any()) }
    }

    @Test
    fun `validate any error is thrown`() = runTest {
        // Given
        val noteModel = NoteModel(
            id = 1,
            title = "test",
            description = "description",
            date = 1,
            tag = TagModel(
                id = 1,
                title = "Test"
            )
        )
        val remoteNote = RemoteNote(
            id = 1,
            title = "test",
            description = "description",
            dateCreated = 1,
            tag = RemoteTag(
                id = 1,
                name = "Test"
            )
        )

        val expected = Exception()

        coEvery { remoteNoteDataSource.insert(remoteNote) } throws expected

        // When
        val result = try {
            repository.addNote(noteModel)
        } catch (e: Exception) {
            e
        }

        assertEquals(expected, result)
    }

}