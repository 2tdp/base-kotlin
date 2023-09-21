package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.demo.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val _uiStateAllMusic = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateAllMusic: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateAllMusic

    private val _uiStateFavorite = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateFavorite: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateFavorite

    private val _uiStateMusicSearch = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateMusicSearch: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateMusicSearch

    fun getAllMusic() {
        viewModelScope.launch {
            repository.getAllMusic().catch {
                _uiStateAllMusic.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAllMusic.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicFromAlbum(name: String) {
        viewModelScope.launch {
            repository.getAllMusicFromAlbum(name).catch {
                _uiStateFavorite.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateFavorite.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicFavorite() {
        viewModelScope.launch {
            repository.getAllMusicFavorite().catch {
                _uiStateFavorite.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateFavorite.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicFromText(str: String) {
        viewModelScope.launch {
            repository.getAllMusicFromText(str).catch {
                _uiStateMusicSearch.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateMusicSearch.value = UiState.Success(it)
            }
        }
    }
}