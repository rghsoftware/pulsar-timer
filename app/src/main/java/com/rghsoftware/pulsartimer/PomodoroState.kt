package com.rghsoftware.pulsartimer

data class PomodoroState(
    val timeDisplay: String = "25:00",
    val sessionTitle: String = "Focus",
    val isTimerRunning: Boolean = false,
)
