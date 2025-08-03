package com.rghsoftware.pulsartimer

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class PomodoroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PomodoroState())
    val uiState: StateFlow<PomodoroState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private var currentDurationMillis: Long = TimeUnit.MINUTES.toMillis(25)

    fun handleEvent(event: PomodoroEvent) {
        when (event) {
            is PomodoroEvent.TogglePauseResume -> togglePauseResume()
            is PomodoroEvent.Skip -> skipSession()
            is PomodoroEvent.NfcScanned -> startFocusSession()
        }
    }

    private fun startFocusSession() {
        _uiState.update { it.copy(sessionTitle = "Focus") }
        startTimer(TimeUnit.MINUTES.toMillis(25))
    }

    private fun togglePauseResume() {
        if (uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer(currentDurationMillis)
        }
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    private fun startTimer(durationMillis: Long) {
        _uiState.update { it.copy(isTimerRunning = true) }
        currentDurationMillis = durationMillis
        countDownTimer = object : CountDownTimer(currentDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentDurationMillis = millisUntilFinished
                _uiState.update { it.copy(timeDisplay = formatTime(millisUntilFinished)) }
            }

            override fun onFinish() {
                _uiState.update { it.copy(isTimerRunning = false, timeDisplay = "Done!") }
            }
        }
    }

    private fun skipSession() {
        countDownTimer?.cancel()
        startFocusSession()
    }


    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }


    private fun formatTime(millisUntilFinished: Long): String {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }


}