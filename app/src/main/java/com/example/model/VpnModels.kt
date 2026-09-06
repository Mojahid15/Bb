package com.example.model

enum class VpnStatus(val label: String) {
    DISCONNECTED("DISCONNECTED"),
    CONNECTING("CONNECTING..."),
    CONNECTED("CONNECTED"),
    DISCONNECTING("DISCONNECTING...")
}

enum class VpnProtocol(val label: String) {
    V2RAY("V2Ray (WS/TLS)"),
    OPENVPN("OpenVPN")
}

data class KsaTweak(
    val id: String,
    val name: String,
    val carrier: String,
    val port: String,
    val sni: String,
    val pingMs: Int
)

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val ipAddress: String,
    val pingMs: Int
)

val defaultKsaTweaks = listOf(
    KsaTweak("stc_ws", "STC Free Net 2026 (WS 443)", "STC", "443", "m.stc.com.sa", 18),
    KsaTweak("stc_social", "STC Social Unlimited (TLS)", "STC", "443", "web.whatsapp.com", 22),
    KsaTweak("mobily_60", "Mobily 60 SAR Free Net", "Mobily", "443", "speedtest.mobily.com.sa", 28),
    KsaTweak("mobily_social", "Mobily Social Media Pack", "Mobily", "443", "v.whatsapp.net", 30),
    KsaTweak("zain_pass", "Zain Pass 100% Free Tweak", "Zain", "443", "speed.sa.zain.com", 24),
    KsaTweak("zain_vip", "Zain VIP SNI Tunnel (WS)", "Zain", "80", "free.zain.com.sa", 29),
    KsaTweak("redbull_direct", "Red Bull Mobile KSA Direct", "Red Bull", "443", "connect.redbullmobile.sa", 35),
    KsaTweak("salam_boost", "Salam Telecom Free Boost", "Salam", "443", "portal.salam.sa", 26),
    KsaTweak("virgin_ssl", "Virgin Mobile KSA SSL", "Virgin", "443", "virginmobile.sa", 34),
    KsaTweak("jawwy_stc", "Jawwy (STC) Social Bundle", "Jawwy", "443", "jawwy.sa", 20)
)

val defaultServers = listOf(
    VpnServer("ksa_riyadh", "KSA - Riyadh Premium 01", "Saudi Arabia", "🇸🇦", "178.62.204.18", 18),
    VpnServer("ksa_jeddah", "KSA - Jeddah Low Latency", "Saudi Arabia", "🇸🇦", "178.62.210.45", 22),
    VpnServer("de_frankfurt", "Germany - Frankfurt 04", "Germany", "🇩🇪", "159.65.120.89", 74),
    VpnServer("sg_singapore", "Singapore - High Speed", "Singapore", "🇸🇬", "128.199.202.14", 65),
    VpnServer("ae_dubai", "UAE - Dubai Express", "United Arab Emirates", "🇦🇪", "185.193.64.12", 28)
)
