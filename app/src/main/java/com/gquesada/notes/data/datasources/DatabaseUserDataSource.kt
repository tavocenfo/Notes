package com.gquesada.notes.data.datasources

import com.gquesada.notes.data.database.daos.UserDao
import com.gquesada.notes.data.database.entities.UserEntity

class DatabaseUserDataSource(
    private val userDao: UserDao
) {

    suspend fun insert(userEntity: UserEntity) {
        userDao.insert(userEntity)
    }

    suspend fun getUser(): UserEntity? =
        userDao.getUser()

    suspend fun deleteAll() {
        userDao.deleteAll()
    }
}