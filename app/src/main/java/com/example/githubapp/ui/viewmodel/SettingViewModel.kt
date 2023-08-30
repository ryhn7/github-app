package com.example.githubapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val getThemeSetting: Flow<Boolean> = userRepository.getThemeSetting()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            userRepository.saveThemeSetting(isDarkModeActive)
        }
    }
}