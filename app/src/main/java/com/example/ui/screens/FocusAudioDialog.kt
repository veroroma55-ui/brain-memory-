package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.BrainwaveMode
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary
import kotlinx.coroutines.delay

@Composable
fun FocusAudioDialog(
    isPlaying: Boolean,
    currentMode: BrainwaveMode,
    onModeSelected: (BrainwaveMode) -> Unit,
    onTogglePlay: () -> Unit,
    onDismiss: () -> Unit
) {
    // 4-7-8 Breathing Guide state
    var breathPhase by remember { mutableStateOf("شهيق عميق (4 ثوان)") }
    var breathScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            breathPhase = "شهيق عميق عبر الأنف (4 ث)"
            breathScale.animateTo(1.35f, animationSpec = tween(4000, easing = LinearEasing))
            breathPhase = "احبس النفس وثبّت الفكرة (7 ث)"
            delay(7000)
            breathPhase = "زفير هادئ عبر الفم (8 ث)"
            breathScale.animateTo(1.0f, animationSpec = tween(8000, easing = LinearEasing))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeuroCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("focus_audio_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مولد موجات الدماغ والتركيز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = NeuroTextSecondary
                        )
                    }
                }

                Text(
                    text = "نغمات ثنائية التردد (Binaural Beats) تُحفز القشرة الدماغية للدخول في حالة التدفق وحفظ المفاهيم بسرعة مضاعفة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeuroTextSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode selections
                BrainwaveMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    Surface(
                        onClick = { onModeSelected(mode) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeuroCyan.copy(alpha = 0.15f) else NeuroSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeuroCyan else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (isSelected) NeuroCyan else NeuroTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = mode.titleAr,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeuroCyan else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = mode.descriptionAr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeuroTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breathing circle guide
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(breathScale.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(NeuroCyan.copy(alpha = 0.4f), NeuroPurple.copy(alpha = 0.1f))
                            )
                        )
                        .border(2.dp, NeuroCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧠",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = breathPhase,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuroCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onTogglePlay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) MaterialTheme.colorScheme.error else NeuroCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_play_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlaying) "إيقاف الموجات الصوتية" else "بدء تشغيل النبضات الدماغية",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
