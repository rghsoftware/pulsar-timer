package com.rghsoftware.pulsartimer

sealed interface PomodoroEvent {
    data object TogglePauseResume : PomodoroEvent
    data object Skip : PomodoroEvent
    data object NfcScanned : PomodoroEvent

}
