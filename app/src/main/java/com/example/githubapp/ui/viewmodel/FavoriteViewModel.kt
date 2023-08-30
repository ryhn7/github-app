package com.example.githubapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.data.UserRepository
import com.example.githubapp.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repo: UserRepository): ViewModel() {

    private val _fav = MutableStateFlow(listOf<UserEntity>())
    val favorite = _fav.asStateFlow()

    init {
        getFavoriteUsers()
    }

    private fun getFavoriteUsers() {
        viewModelScope.launch {
            repo.getFavoriteUsers().collect {
                _fav.value = it
            }
        }
    }
}