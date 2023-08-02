package com.gquesada.notes.core.network

import retrofit2.HttpException

object Extensions {

    fun Throwable.isNotAuthorizedException(): Boolean =
        this is HttpException && (code() == 401 || code() == 403)
}