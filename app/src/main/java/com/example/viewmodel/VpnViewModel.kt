package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.KsaTweak
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnStatus
import com.example.model.defaultKsaTweaks
import com.example.model.defaultServers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

data class VpnUiState(
    val status: VpnStatus = VpnStatus.DISCONNECTED,
    val protocol: VpnProtocol = VpnProtocol.V2RAY,
    val selectedTweak: KsaTweak = defaultKsaTweaks[0],
    val selectedServer: VpnServer = defaultServers[0],
    val connectionStageText: String = "",
    val downSpeedText: String = "0.00 KB/s",
    val upSpeedText: String = "0.00 KB/s",
    val totalDownloadedBytes: Long = 0L,
    val totalUploadedBytes: Long = 0L,
    val durationSeconds: Long = 0L,
    val virtualIp: String = "10.8.0.42",
    val currentPingMs: Int = 18,
    val logs: List<String> = emptyList()
)

class VpnViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VpnUiState())
    val uiState: StateFlow<VpnUiState> = _uiState.asStateFlow()

    private var trafficJob: Job? = null
    private var timerJob: Job? = null

    fun selectProtocol(protocol: VpnProtocol) {
        if (_uiState.value.status == VpnStatus.DISCONNECTED) {
            _uiState.value = _uiState.value.copy(protocol = protocol)
            addLog("Protocol changed to ${protocol.label}")
        }
    }

    fun selectTweak(tweak: KsaTweak) {
        _uiState.value = _uiState.value.copy(
            selectedTweak = tweak,
            currentPingMs = tweak.pingMs
        )
        addLog("Network tweak selected: ${tweak.name} (SNI: ${tweak.sni})")
    }

    fun selectServer(server: VpnServer) {
        _uiState.value = _uiState.value.copy(
            selectedServer = server,
            currentPingMs = (server.pingMs + _uiState.value.selectedTweak.pingMs) / 2
        )
        addLog("Server switched to: ${server.name} [${server.ipAddress}]")
    }

    fun toggleConnection() {
        when (_uiState.value.status) {
            VpnStatus.DISCONNECTED -> startConnection()
            VpnStatus.CONNECTED -> disconnect()
            VpnStatus.CONNECTING -> cancelConnecting()
            VpnStatus.DISCONNECTING -> { /* waiting */ }
        }
    }

    private fun startConnection() {
        val currentProtocol = _uiState.value.protocol
        val tweak = _uiState.value.selectedTweak
        val server = _uiState.value.selectedServer

        _uiState.value = _uiState.value.copy(
            status = VpnStatus.CONNECTING,
            connectionStageText = "Initializing ${currentProtocol.name}..."
        )
        addLog("Connecting to ${server.name} via ${tweak.name}...")

        viewModelScope.launch {
            delay(400)
            _uiState.value = _uiState.value.copy(
                connectionStageText = "Resolving Host & SNI: ${tweak.sni}"
            )
            addLog("SNI Handshake: ${tweak.sni}:${tweak.port}")

            delay(500)
            _uiState.value = _uiState.value.copy(
                connectionStageText = if (currentProtocol == VpnProtocol.V2RAY) {
                    "Establishing V2Ray WebSocket + TLS..."
                } else {
                    "Authenticating OpenVPN Auth Token..."
                }
            )
            addLog("Encryption: AES-256-GCM / TLSv1.3 tunnel ready")

            delay(500)
            _uiState.value = _uiState.value.copy(
                connectionStageText = "Assigning Virtual IP Address..."
            )
            val randomIp = "10.8.${Random.nextInt(2, 250)}.${Random.nextInt(10, 250)}"

            delay(400)
            _uiState.value = _uiState.value.copy(
                status = VpnStatus.CONNECTED,
                connectionStageText = "Tunnel Secured",
                virtualIp = randomIp,
                currentPingMs = (server.pingMs + Random.nextInt(-3, 4)).coerceAtLeast(12),
                durationSeconds = 0L
            )
            addLog("CONNECTED! Virtual IP: $randomIp. Bypass active.")

            startTrafficSimulation()
            startDurationTimer()
        }
    }

    private fun disconnect() {
        _uiState.value = _uiState.value.copy(
            status = VpnStatus.DISCONNECTING,
            connectionStageText = "Terminating tunnel..."
        )
        addLog("Disconnecting tunnel...")

        viewModelScope.launch {
            trafficJob?.cancel()
            timerJob?.cancel()
            delay(500)

            _uiState.value = _uiState.value.copy(
                status = VpnStatus.DISCONNECTED,
                connectionStageText = "",
                downSpeedText = "0.00 KB/s",
                upSpeedText = "0.00 KB/s"
            )
            addLog("Tunnel closed. DISCONNECTED.")
        }
    }

    private fun cancelConnecting() {
        trafficJob?.cancel()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            status = VpnStatus.DISCONNECTED,
            connectionStageText = "",
            downSpeedText = "0.00 KB/s",
            upSpeedText = "0.00 KB/s"
        )
        addLog("Connection cancelled.")
    }

    private fun startTrafficSimulation() {
        trafficJob?.cancel()
        trafficJob = viewModelScope.launch {
            var downloaded = _uiState.value.totalDownloadedBytes
            var uploaded = _uiState.value.totalUploadedBytes

            while (_uiState.value.status == VpnStatus.CONNECTED) {
                delay(800)
                // Generate realistic telemetry
                val isBurst = Random.nextInt(10) > 7
                val downSpeedKbps = if (isBurst) {
                    Random.nextDouble(12500.0, 48000.0) // 12-48 MB/s
                } else {
                    Random.nextDouble(1800.0, 9500.0) // 1.8-9.5 MB/s
                }
                val upSpeedKbps = Random.nextDouble(250.0, 3200.0) // 250 KB/s - 3.2 MB/s

                downloaded += (downSpeedKbps * 1024 * 0.8).toLong()
                uploaded += (upSpeedKbps * 1024 * 0.8).toLong()

                val downText = formatSpeed(downSpeedKbps)
                val upText = formatSpeed(upSpeedKbps)

                _uiState.value = _uiState.value.copy(
                    downSpeedText = downText,
                    upSpeedText = upText,
                    totalDownloadedBytes = downloaded,
                    totalUploadedBytes = uploaded
                )
            }
        }
    }

    private fun startDurationTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.status == VpnStatus.CONNECTED) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    durationSeconds = _uiState.value.durationSeconds + 1
                )
            }
        }
    }

    private fun formatSpeed(kbps: Double): String {
        return if (kbps >= 1000.0) {
            val mbps = kbps / 1024.0
            String.format(Locale.US, "%.2f MB/s", mbps)
        } else {
            String.format(Locale.US, "%.2f KB/s", kbps)
        }
    }

    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", Locale.US).format(java.util.Date())
        val newLogs = (_uiState.value.logs + "[$timestamp] $message").takeLast(50)
        _uiState.value = _uiState.value.copy(logs = newLogs)
    }

    override fun onCleared() {
        super.onCleared()
        trafficJob?.cancel()
        timerJob?.cancel()
    }
}
