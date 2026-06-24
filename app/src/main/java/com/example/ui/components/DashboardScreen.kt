package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    usdRate: Double?,
    eurRate: Double?,
    isLoading: Boolean,
    onSettingsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        InteractiveParticleBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Column {
                            Text("BCV Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate100)
                            Text("RED NEURONAL ACTIVA", fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = NeonEmerald, letterSpacing = 1.sp)
                        }
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Slate300)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Sync Status Chip
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(CardBackground)
                        .border(1.dp, CardBorder, CircleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonEmerald))
                    Text("Última actualización: hace un momento", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Slate300)
                }

                if (isLoading && usdRate == null) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonBlue)
                    }
                } else {
                    RateCard(
                        title = "Dólar Americano (USD)",
                        rate = usdRate,
                        currency = "Bs/USD",
                        accentColor = NeonBlue,
                        badgeText = "+0.42%",
                        progress = 0.75f,
                        isLoading = isLoading
                    )
                    RateCard(
                        title = "Euro Europeo (EUR)",
                        rate = eurRate,
                        currency = "Bs/EUR",
                        accentColor = NeonEmerald,
                        badgeText = "-0.12%",
                        progress = 0.5f,
                        isLoading = isLoading
                    )
                    
                    // Notification Summary Panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NeonBlue.copy(alpha = 0.2f))
                            .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Próximo Reporte Programado", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate100)
                            Text("Frecuencia: 4 veces al día (Smart Mode)", fontSize = 11.sp, color = Slate400)
                        }
                        Text("14:00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun RateCard(
    title: String,
    rate: Double?,
    currency: String,
    accentColor: Color,
    badgeText: String,
    progress: Float,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
    ) {
        // Glowing orb in top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-16).dp)
                .size(96.dp)
                .blur(40.dp)
                .background(accentColor.copy(alpha = 0.15f), CircleShape)
        )
        
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = (-0.5).sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimatedContent(
                    targetState = rate,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    }, label = "rate_anim"
                ) { targetRate ->
                    Text(
                        text = if (targetRate != null) String.format("%.2f", targetRate) else "---",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate100,
                        letterSpacing = (-1).sp
                    )
                }
                Text(
                    text = currency,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate400,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isLoading && rate != null) {
                    LinearProgressIndicator(
                        modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape),
                        color = accentColor,
                        trackColor = CardBorder
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(CardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(accentColor)
                        )
                    }
                }
                Text("OFICIAL BCV", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
            }
        }
    }
}
