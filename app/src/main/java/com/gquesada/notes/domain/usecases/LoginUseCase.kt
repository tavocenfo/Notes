package com.gquesada.notes.domain.usecases

import com.gquesada.notes.domain.models.UserModel
import com.gquesada.notes.domain.repositories.UserRepository
import retrofit2.HttpException

class LoginUseCase(
    private val userRepository: UserRepository
) {

    suspend fun execute(input: LoginUseCaseInput): LoginUseCaseOutput =
        if (input.email.isEmpty() || input.password.isEmpty()) {
            LoginUseCaseOutput.Error.EmptyFields
        } else {
            try {
                val user = userRepository.login(input.email, input.password)
                LoginUseCaseOutput.Success(user)
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> LoginUseCaseOutput.Error.EmptyFields
                    401 -> LoginUseCaseOutput.Error.InvalidCredentials
                    else -> LoginUseCaseOutput.Error.NetworkError
                }
            }
        }

}

data class LoginUseCaseInput(
    val email: String,
    val password: String
)

sealed class LoginUseCaseOutput {

    data class Success(val user: UserModel) : LoginUseCaseOutput()
    sealed class Error : LoginUseCaseOutput() {
        object EmptyFields : Error()
        object InvalidCredentials : Error()
        object NetworkError : Error()
    }
}