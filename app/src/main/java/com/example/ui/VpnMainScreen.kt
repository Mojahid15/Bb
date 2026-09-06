package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.VpnStatus
import com.example.ui.theme.DarkSlateBackground
import com.example.viewmodel.VpnViewModel

@Composable
fun VpnMainScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showServerPicker by remember { mutableStateOf(false) }
    var showLogsSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSlateBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section: App Header, Protocol Toggle & Network Selection Card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // 1. App Bar Header
                AppHeader(
                    status = uiState.status,
                    server = uiState.selectedServer,
                    onOpenServers = { showServerPicker = true },
                    onOpenLogs = { showLogsSheet = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Protocol Toggle (V2Ray vs OpenVPN)
                ProtocolToggle(
                    currentProtocol = uiState.protocol,
                    enabled = uiState.status == VpnStatus.DISCONNECTED,
                    onProtocolSelected = { viewModel.selectProtocol(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Network / Carrier Selection Card (TARGET NETWORK KSA)
                NetworkSelectionCard(
                    selectedTweak = uiState.selectedTweak,
                    enabled = uiState.status == VpnStatus.DISCONNECTED,
                    onTweakSelected = { viewModel.selectTweak(it) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Middle Section: Centered Connection Toggle Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConnectButtonSection(
                    status = uiState.status,
                    stageText = uiState.connectionStageText,
                    onToggle = { viewModel.toggleConnection() }
                )

                if (uiState.status == VpnStatus.CONNECTED) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ConnectionDetailPill(
                        durationSeconds = uiState.durationSeconds,
                        virtualIp = uiState.virtualIp,
                        pingMs = uiState.currentPingMs
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Section: Real-time Traffic Statistics Card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrafficStatisticsCard(
                    downSpeed = uiState.downSpeedText,
                    upSpeed = uiState.upSpeedText
                )
            }
        }

        // Bottom Sheets
        if (showServerPicker) {
            ServerPickerSheet(
                selectedServer = uiState.selectedServer,
                onServerSelected = { viewModel.selectServer(it) },
                onDismiss = { showServerPicker = false }
            )
        }

        if (showLogsSheet) {
            LogsBottomSheet(
                logs = uiState.logs,
                onDismiss = { showLogsSheet = false }
            )
        }
    }
}
