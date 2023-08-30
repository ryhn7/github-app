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
class FollowViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {


    private val _followers = MutableStateFlow<Result<ArrayList<User>>>(Result.Loading)
    val followers = _followers.asStateFlow()

    private val _following = MutableStateFlow<Result<ArrayList<User>>>(Result.Loading)
    val following = _following.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun getUserFollowers(username: String) {
        _followers.value = Result.Loading
        viewModelScope.launch {
            repo.getUserFollowers(username).collect {
                _followers.value = it
            }
        }

        _isLoading.value = true
    }

    fun getUserFollowing(username: String) {
        _following.value = Result.Loading
        viewModelScope.launch {
            repo.getUserFollowing(username).collect {
                _following.value = it
            }
        }

        _isLoading.value = true
    }
}