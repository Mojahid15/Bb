package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KsaTweak
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnStatus
import com.example.model.defaultKsaTweaks
import com.example.model.defaultServers
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.LabelMuted
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StrokeBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppHeader(
    status: VpnStatus,
    server: VpnServer,
    onOpenServers: () -> Unit,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "MOJAHID VPN",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.76.sp, // letterSpacing 0.08
                modifier = Modifier.testTag("tvAppTitle")
            )

            Spacer(modifier = Modifier.height(4.dp))

            val statusColor = when (status) {
                VpnStatus.DISCONNECTED -> TextSecondary
                VpnStatus.CONNECTING -> AccentAmber
                VpnStatus.CONNECTED -> AccentEmerald
                VpnStatus.DISCONNECTING -> AccentRed
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = status.label,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("tvConnectionStatus")
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Server badge button
            Surface(
                onClick = onOpenServers,
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, StrokeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = server.flag, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = server.name.take(12),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Logs icon button
            IconButton(
                onClick = onOpenLogs,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Connection Logs",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ProtocolToggle(
    currentProtocol: VpnProtocol,
    enabled: Boolean,
    onProtocolSelected: (VpnProtocol) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("toggleProtocol"),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, StrokeBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // V2Ray Button
            val isV2ray = currentProtocol == VpnProtocol.V2RAY
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(if (isV2ray) PrimaryBlue else Color.Transparent)
                    .clickable(enabled = enabled) { onProtocolSelected(VpnProtocol.V2RAY) }
                    .padding(vertical = 12.dp)
                    .testTag("btnV2ray"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V2Ray (WS/TLS)",
                    color = if (isV2ray) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isV2ray) FontWeight.Bold else FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(44.dp)
                    .background(StrokeBorder)
            )

            // OpenVPN Button
            val isOpenVpn = currentProtocol == VpnProtocol.OPENVPN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                    .background(if (isOpenVpn) PrimaryBlue else Color.Transparent)
                    .clickable(enabled = enabled) { onProtocolSelected(VpnProtocol.OPENVPN) }
                    .padding(vertical = 12.dp)
                    .testTag("btnOpenVPN"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OpenVPN",
                    color = if (isOpenVpn) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isOpenVpn) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NetworkSelectionCard(
    selectedTweak: KsaTweak,
    enabled: Boolean,
    onTweakSelected: (KsaTweak) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cardNetworkSelection"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, StrokeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "TARGET NETWORK (KSA)",
                color = LabelMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Spinner container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSlateBackground)
                    .border(BorderStroke(1.dp, StrokeBorder), RoundedCornerShape(8.dp))
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = 14.dp)
                    .testTag("spinnerKsaTweak"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentCyan.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, AccentCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = selectedTweak.carrier,
                                color = AccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = selectedTweak.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Tweak",
                        tint = AccentCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(CardSurface)
                        .border(1.dp, StrokeBorder, RoundedCornerShape(8.dp))
                ) {
                    defaultKsaTweaks.forEach { tweak ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = tweak.name,
                                            color = if (tweak.id == selectedTweak.id) AccentCyan else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = if (tweak.id == selectedTweak.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "SNI: ${tweak.sni} • Port: ${tweak.port}",
                                            color = LabelMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        text = "${tweak.pingMs} ms",
                                        color = AccentEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            onClick = {
                                onTweakSelected(tweak)
                                expanded = false
                            },
                            leadingIcon = {
                                if (tweak.id == selectedTweak.id) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectButtonSection(
    status: VpnStatus,
    stageText: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val buttonColor by animateColorAsState(
        targetValue = when (status) {
            VpnStatus.DISCONNECTED -> PrimaryBlue
            VpnStatus.CONNECTING -> AccentAmber
            VpnStatus.CONNECTED -> AccentEmerald
            VpnStatus.DISCONNECTING -> AccentRed
        },
        animationSpec = tween(400),
        label = "btnColor"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .testTag("btnConnectWrapper"),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing animation aura when connecting or connected
            if (status == VpnStatus.CONNECTING || status == VpnStatus.CONNECTED) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(buttonColor.copy(alpha = pulseAlpha))
                )
            }

            // Outer decorative ring
            Box(
                modifier = Modifier
                    .size(152.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    buttonColor.copy(alpha = 0.8f),
                                    StrokeBorder,
                                    buttonColor.copy(alpha = 0.4f),
                                    StrokeBorder
                                )
                            )
                        ),
                        CircleShape
                    )
            )

            // Primary Connect Button (140dp x 140dp matching XML fabCustomSize 140dp)
            Surface(
                onClick = onToggle,
                modifier = Modifier
                    .size(140.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .testTag("fabConnect"),
                shape = CircleShape,
                color = buttonColor
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Connect",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp) // matching XML maxImageSize 54dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (status) {
                                VpnStatus.DISCONNECTED -> "TAP TO CONNECT"
                                VpnStatus.CONNECTING -> "CONNECTING"
                                VpnStatus.CONNECTED -> "CONNECTED"
                                VpnStatus.DISCONNECTING -> "STOPPING"
                            },
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        if (stageText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stageText,
                color = if (status == VpnStatus.CONNECTING) AccentAmber else AccentEmerald,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TrafficStatisticsCard(
    downSpeed: String,
    upSpeed: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cardStats"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, StrokeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Download speed column (weight 1)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = LabelMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DOWNLOAD",
                        color = LabelMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = downSpeed,
                    color = AccentCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("tvDownSpeed")
                )
            }

            // Divider matching XML layout (1dp width, match_parent height, #334155)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(StrokeBorder)
            )

            // Upload speed column (weight 1)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = LabelMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "UPLOAD",
                        color = LabelMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = upSpeed,
                    color = AccentEmerald,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("tvUpSpeed")
                )
            }
        }
    }
}

@Composable
fun ConnectionDetailPill(
    durationSeconds: Long,
    virtualIp: String,
    pingMs: Int,
    modifier: Modifier = Modifier
) {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val hours = minutes / 60
    val timeFormatted = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardSurface.copy(alpha = 0.6f),
        border = BorderStroke(0.8.dp, StrokeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AccentEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IP: $virtualIp",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$pingMs ms",
                    color = AccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = timeFormatted,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerPickerSheet(
    selectedServer: VpnServer,
    onServerSelected: (VpnServer) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = CardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select VPN Server Location",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = AccentCyan)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(defaultServers) { server ->
                    val isSelected = server.id == selectedServer.id
                    Surface(
                        onClick = {
                            onServerSelected(server)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else DarkSlateBackground,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AccentCyan else StrokeBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = server.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = server.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${server.country} • ${server.ipAddress}",
                                        color = LabelMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${server.pingMs} ms",
                                    color = AccentEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsBottomSheet(
    logs: List<String>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = CardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = AccentCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connection Logs",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = AccentCyan)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                color = DarkSlateBackground,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, StrokeBorder)
            ) {
                if (logs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No connection logs recorded yet.\nTap connect to initiate tunnel handshake.",
                            color = LabelMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        reverseLayout = true
                    ) {
                        items(logs.reversed()) { log ->
                            Text(
                                text = log,
                                color = if (log.contains("CONNECTED")) AccentEmerald
                                else if (log.contains("DISCONNECTED") || log.contains("Terminating")) AccentAmber
                                else TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
