package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyCard
import com.example.ui.components.NeuralTopBar
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroGreen
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroSurfaceElevated
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary
import kotlinx.coroutines.delay

@Composable
fun BionicSpeedScreen(
    cards: List<StudyCard>,
    activeCardIndex: Int,
    onNextCard: () -> Unit,
    onPrevCard: () -> Unit,
    onBack: () -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    onOpenAudioSettings: () -> Unit
) {
    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد بطاقات متاحة", color = NeuroTextSecondary)
        }
        return
    }

    val currentCard = cards.getOrElse(activeCardIndex) { cards.first() }

    // Mode: 0 = Bionic Text, 1 = RSVP Word Flasher
    var viewMode by remember { mutableIntStateOf(0) }

    // RSVP Flasher state
    val words = remember(currentCard.fullExplanation) {
        currentCard.fullExplanation.trim().split(Regex("\\s+"))
    }
    var currentWordIndex by remember(currentCard.id) { mutableIntStateOf(0) }
    var isPlayingRsvp by remember(currentCard.id) { mutableStateOf(false) }
    var wordsPerMinute by remember { mutableFloatStateOf(260f) }

    val delayMillis = (60000 / wordsPerMinute).toLong()

    LaunchedEffect(isPlayingRsvp, currentWordIndex, wordsPerMinute) {
        if (isPlayingRsvp && currentWordIndex < words.size) {
            delay(delayMillis)
            if (currentWordIndex < words.size - 1) {
                currentWordIndex++
            } else {
                isPlayingRsvp = false
            }
        }
    }

    // Build Bionic Annotated String
    val bionicText = remember(currentCard.fullExplanation) {
        buildAnnotatedString {
            val tokens = currentCard.fullExplanation.split(" ")
            tokens.forEachIndexed { index, token ->
                if (token.isNotBlank()) {
                    val halfLen = (token.length / 2).coerceAtLeast(1)
                    val firstHalf = token.take(halfLen)
                    val secondHalf = token.drop(halfLen)

                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Black,
                            color = NeuroCyan,
                            fontSize = 18.sp
                        )
                    ) {
                        append(firstHalf)
                    }

                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    ) {
                        append(secondHalf)
                    }

                    if (index < tokens.size - 1) append(" ")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "القراءة البيونية الفائقة",
                subtitle = "بطاقة ${activeCardIndex + 1} من ${cards.size}",
                onBack = onBack,
                isAudioPlaying = isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onOpenAudioSettings = onOpenAudioSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Mode Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewMode == 0,
                    onClick = { viewMode = 0 },
                    label = { Text("قراءة بيونية موجهة (Bionic)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeuroCyan,
                        selectedLabelColor = Color.Black
                    )
                )

                FilterChip(
                    selected = viewMode == 1,
                    onClick = { viewMode = 1 },
                    label = { Text("فلاش بصري سريع (RSVP)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeuroPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Concept Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentCard.sensoryAnchorEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentCard.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تحفيز القشرة البصرية لابتلاع المعلومات بأقل مجهود",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewMode == 0) {
                // Bionic Paragraph Mode
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeuroCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "النص البيوني (تثبيت العين التلقائي):",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeuroCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = NeuroCyan, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = bionicText,
                            lineHeight = 32.sp
                        )
                    }
                }
            } else {
                // RSVP Flasher Mode
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeuroPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "فلاش الكلمات المتتابعة (بدون نطق باطني):",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroPurple,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Word Flash Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(NeuroSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentWord = words.getOrNull(currentWordIndex) ?: ""
                            Text(
                                text = currentWord,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = NeuroCyan,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { if (words.isNotEmpty()) (currentWordIndex + 1).toFloat() / words.size else 0f },
                            color = NeuroPurple,
                            trackColor = NeuroSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "الكلمة ${currentWordIndex + 1} من ${words.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentWordIndex = 0
                                    isPlayingRsvp = false
                                }
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "إعادة", tint = NeuroTextSecondary)
                            }

                            Button(
                                onClick = { isPlayingRsvp = !isPlayingRsvp },
                                colors = ButtonDefaults.buttonColors(containerColor = NeuroPurple),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("rsvp_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingRsvp) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isPlayingRsvp) "إيقاف مؤقت" else "بدء الفلاش")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Speed Slider
                        Text(
                            text = "سرعة القراءة: ${wordsPerMinute.toInt()} كلمة / دقيقة",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroTextSecondary
                        )

                        Slider(
                            value = wordsPerMinute,
                            onValueChange = { wordsPerMinute = it },
                            valueRange = 150f..450f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeuroCyan,
                                activeTrackColor = NeuroCyan
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevCard, enabled = activeCardIndex > 0) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "السابق")
                }

                Text(
                    text = "${activeCardIndex + 1} / ${cards.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeuroTextSecondary
                )

                IconButton(onClick = onNextCard, enabled = activeCardIndex < cards.size - 1) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "التالي")
                }
            }
        }
    }
}
