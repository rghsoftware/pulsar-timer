package com.rghsoftware.pulsartimer

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rghsoftware.pulsartimer.ui.theme.PulsarTimerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulsarTimerTheme {
                val state by viewModel.uiState.collectAsState()
                PomodoroScreen(state = state, onEvent = viewModel::handleEvent)

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            viewModel.handleEvent(PomodoroEvent.NfcScanned)
        }
    }
}

@Composable
fun PomodoroScreen(state: PomodoroState, onEvent: (PomodoroEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = state.sessionTitle, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
        Text(text = state.timeDisplay, fontSize = 80.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {onEvent(PomodoroEvent.TogglePauseResume)}) {
                Text(text = if (state.isTimerRunning) "Pause" else "Resume", fontSize = 18.sp)
            }
            Button(onClick = { onEvent(PomodoroEvent.Skip)}) {
                Text(text = "Skip", fontSize = 18.sp)
            }
        }
    }
}