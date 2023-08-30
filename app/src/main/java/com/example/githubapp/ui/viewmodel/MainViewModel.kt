package com.example.githubapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.githubapp.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val getThemeSetting: Flow<Boolean> = userRepository.getThemeSetting()
}