package com.example.githubapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.data.Result
import com.example.githubapp.data.UserRepository
import com.example.githubapp.data.local.entity.UserEntity
import com.example.githubapp.data.remote.response.DataUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {


    private val _user = MutableStateFlow<Result<DataUser>>(Result.Loading)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun getUserDetail(username: String) {
        _user.value = Result.Loading
        viewModelScope.launch {
            repo.getUserDetail(username).collect {
                _user.value = it
            }
        }
        _isLoading.value = true
    }

    fun saveFavorite(user: UserEntity) {
        viewModelScope.launch {
            repo.saveFavoriteUser(user)
        }
    }

    fun deleteFavorite(user: UserEntity) {
        viewModelScope.launch {
            repo.deleteFavoriteUser(user)
        }
    }

    fun checkFavorite(username: String): Flow<Boolean> = repo.isFavoriteUser(username)

}