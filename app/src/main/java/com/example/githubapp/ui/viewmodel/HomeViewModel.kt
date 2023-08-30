package com.example.githubapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.data.Result
import com.example.githubapp.data.UserRepository
import com.example.githubapp.data.remote.response.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<Result<ArrayList<User>>>(Result.Loading)
    val users = _users.asStateFlow()

    init {
        findUser("\"\"")
    }

    fun findUser(query: String) {
        _users.value = Result.Loading
        viewModelScope.launch {
            repo.searchUsername(query).collect {
                _users.value = it
            }
        }
    }
}
