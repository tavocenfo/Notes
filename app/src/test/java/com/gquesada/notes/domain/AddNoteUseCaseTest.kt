package com.gquesada.notes.domain

import com.gquesada.notes.domain.exceptions.NetworkErrorException
import com.gquesada.notes.domain.exceptions.TagNullException
import com.gquesada.notes.domain.exceptions.TitleEmptyException
import com.gquesada.notes.domain.models.TagModel
import com.gquesada.notes.domain.repositories.NoteRepository
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import com.gquesada.notes.domain.usecases.AddNoteUseCaseInput
import com.gquesada.notes.domain.usecases.AddNoteUseCaseOutput
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class AddNoteUseCaseTest {

    @MockK
    private lateinit var repository: NoteRepository

    @InjectMockKs
    private lateinit var useCase: AddNoteUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `validate when note is saved`() = runTest {
        // Given
        val input = AddNoteUseCaseInput(
            title = "title",
            description = "description",
            tag = TagModel(
                id = 1,
                title = "title"
            )
        )
        val expected = AddNoteUseCaseOutput.Success
        coEvery { repository.addNote(any()) } returns 1

        // when
        val result = useCase.execute(input)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.addNote(any()) }
    }

    @Test
    fun `validate error when tag is null`() = runTest {
        // Given
        val input = AddNoteUseCaseInput(
            title = "title",
            description = "description",
            tag = null
        )

        val expected = AddNoteUseCaseOutput.Error(TagNullException)

        // When
        val result = useCase.execute(input)

        // then
        assertEquals("Mensaje que si muestra si el assert falla", expected, result)
        coVerify(exactly = 0) { repository.addNote(any()) }

    }

    @Test
    fun `validate error when title is empty`() = runTest {
        // Given
        val input = AddNoteUseCaseInput(
            title = "",
            description = "description",
            tag = TagModel(
                id = 1,
                title = "title"
            )
        )
        val expected = AddNoteUseCaseOutput.Error(TitleEmptyException)

        // When
        val result = useCase.execute(input)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 0) { repository.addNote(any()) }
    }

    @Test
    fun `validate error when repository fails`() = runTest {
        // Given
        val input = AddNoteUseCaseInput(
            title = "title",
            description = "description",
            tag = TagModel(
                id = 1,
                title = "title"
            )
        )
        val expected = AddNoteUseCaseOutput.Error(NetworkErrorException)
        coEvery { repository.addNote(any()) } throws Exception()

        // When
        val result = useCase.execute(input)

        // then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.addNote(any()) }
    }

    @Test
    fun `validate error when request is not authorized`() = runTest {
        // Given
        val input = AddNoteUseCaseInput(
            title = "title",
            description = "description",
            tag = TagModel(
                id = 1,
                title = "title"
            )
        )
        val exception = mockk<HttpException>()
        every { exception.code() } returns 403
        val expected = AddNoteUseCaseOutput.Error(exception)
        coEvery { repository.addNote(any()) } throws exception

        // When
        val result = useCase.execute(input)

        // then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.addNote(any()) }
    }
}