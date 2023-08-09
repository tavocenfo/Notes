package com.gquesada.notes.core.network

import com.gquesada.notes.data.datasources.SharedPreferencesDataSource
import com.gquesada.notes.domain.repositories.UserRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// clase que se va invocar antes de que el request se envie
// podemos usarla para modificar nuestros request y agregar o remove la data que enviamos al server
// para este caso vamos a aagregar el token que va a permiter la autenticacion en los request
class AuthInterceptor(
    private val userRepository: UserRepository,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : Interceptor {

    private companion object {
        const val ACCESS_TOKEN_KEY = "x-access-token"
        const val NOTIFICATION_TOKEN_KEY = "not-token"
    }

    // metodo en donde tenemos accesso al request y lo podemos modificar
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { userRepository.fetchUserToken() ?: "" }
        val notToken = sharedPreferencesDataSource.getDeviceToken()
        val requestBuilder = chain.request().newBuilder()
            .addHeader(ACCESS_TOKEN_KEY, accessToken)
        notToken?.let { requestBuilder.addHeader(NOTIFICATION_TOKEN_KEY, it) }

        return chain.proceed(requestBuilder.build())
    }
}