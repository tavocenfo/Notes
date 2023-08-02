package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.repositories.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {

    suspend fun execute() {
        userRepository.logout()
    }
}