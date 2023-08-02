package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.UserModel
import com.gquesada.notes.domain.repositories.UserRepository

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun execute(): UserModel? =
        userRepository.getUser()
}