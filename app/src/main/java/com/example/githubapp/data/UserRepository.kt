package com.example.githubapp.data

import android.util.Log
import com.example.githubapp.data.local.entity.UserEntity
import com.example.githubapp.data.local.room.UserDao
import com.example.githubapp.data.remote.response.DataUser
import com.example.githubapp.data.remote.response.User
import com.example.githubapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val preferences: AppPreferences
) {

    companion object{
        private const val TAG = "UserRepository"
    }

    fun searchUsername(q: String): Flow<Result<ArrayList<User>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.searchUsername(q).items
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "searchUsername: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUserFollowers(username: String): Flow<Result<ArrayList<User>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.getUserFollowers(username)
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "getUserFollowers: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUserFollowing(username: String): Flow<Result<ArrayList<User>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.getUserFollowing(username)
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "getUserFollowing: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUserDetail(username: String): Flow<Result<DataUser>> = flow {
        emit(Result.Loading)
        try {
            val user = apiService.getUserDetailByUsername(username)
            emit(Result.Success(user))
        } catch (e: Exception) {
            Log.d(TAG, "getUserDetail: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun isFavoriteUser(username: String): Flow<Boolean> = userDao.isFavoriteUser(username)

    fun getFavoriteUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun deleteFavoriteUser(user: UserEntity) {
        userDao.delete(user)
    }

    suspend fun saveFavoriteUser(user: UserEntity) {
        userDao.insert(user)
    }

    fun getThemeSetting(): Flow<Boolean> {
        return preferences.getThemeSetting()
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        preferences.saveThemeSetting(isDarkModeActive)
    }

}