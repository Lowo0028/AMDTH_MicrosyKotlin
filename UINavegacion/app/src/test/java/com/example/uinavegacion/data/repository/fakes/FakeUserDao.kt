package com.example.uinavegacion.data.repository.fakes

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity

class FakeUserDao : UserDao {
    //Dao falso para las pruebas
    private val data = mutableListOf<UserEntity>()
    //funciones para manipular esta data
    override suspend fun insert(user: UserEntity): Long {
        val nextId = (data.maxOfOrNull { it.id ?: 0 } ?:0) + 1
        data.add(user.copy(id = nextId))
        return nextId.toLong()
    }

    override suspend fun getByEmail(email: String): UserEntity? {
        return data.firstOrNull { it.email == email }
    }

    override suspend fun count(): Int {
        return data.size
    }

    override suspend fun getAll(): List<UserEntity> {
        return data.sortedBy { it.id ?: 0 }
    }


}