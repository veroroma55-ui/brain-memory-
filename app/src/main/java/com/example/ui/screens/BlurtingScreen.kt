package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyCard
import com.example.ui.components.NeuralTopBar
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroGreen
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroRose
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlurtingScreen(
    cards: List<StudyCard>,
    activeCardIndex: Int,
    onNextCard: () -> Unit,
    onSaveSession: (Int, Int) -> Unit,
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

    var userBlurtingText by remember(currentCard.id) { mutableStateOf("") }
    var secondsRemaining by remember(currentCard.id) { mutableIntStateOf(60) }
    var isTimerActive by remember(currentCard.id) { mutableStateOf(false) }
    var hasEvaluated by remember(currentCard.id) { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(isTimerActive, secondsRemaining) {
        if (isTimerActive && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
            if (secondsRemaining == 0) {
                isTimerActive = false
                hasEvaluated = true
            }
        }
    }

    // Keyword Analysis
    val targetKeywords = remember(currentCard.keyTerms, currentCard.fullExplanation) {
        if (currentCard.keyTerms.isNotBlank()) {
            currentCard.keyTerms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            currentCard.fullExplanation.split(" ").filter { it.length > 4 }.take(6)
        }
    }

    val recalledKeywords = remember(userBlurtingText, hasEvaluated) {
        val userLower = userBlurtingText.lowercase()
        targetKeywords.filter { kw ->
            val cleanKw = kw.replace(Regex("[^\\p{L}\\p{Nd}]"), "").lowercase()
            userLower.contains(cleanKw)
        }
    }

    val missedKeywords = remember(recalledKeywords, targetKeywords) {
        targetKeywords.filter { !recalledKeywords.contains(it) }
    }

    val scorePercentage = if (targetKeywords.isNotEmpty()) {
        ((recalledKeywords.size.toFloat() / targetKeywords.size.toFloat()) * 100).toInt()
    } else 100

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "تفريغ الذاكرة السريع (Blurting)",
                subtitle = "سبر الأغوار العصبية في 60 ثانية",
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
            // Concept prompt card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroPink.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentCard.sensoryAnchorEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "المفهوم المستهدف للاسترجاع:",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroPink
                        )
                        Text(
                            text = currentCard.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer & Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (secondsRemaining < 15) NeuroRose else NeuroCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الوقت المتبقي: $secondsRemaining ثانية",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsRemaining < 15) NeuroRose else NeuroCyan
                    )
                }

                if (!isTimerActive && !hasEvaluated) {
                    Button(
                        onClick = { isTimerActive = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeuroCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("start_blurting_timer")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بدء المؤقت", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { secondsRemaining / 60f },
                color = if (secondsRemaining < 15) NeuroRose else NeuroCyan,
                trackColor = NeuroSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Input for Blurting
            OutlinedTextField(
                value = userBlurtingText,
                onValueChange = {
                    userBlurtingText = it
                    if (!isTimerActive && !hasEvaluated && it.isNotBlank()) {
                        isTimerActive = true
                    }
                },
                placeholder = {
                    Text(
                        text = "اكتب فوراً كل ما يخطر ببالك عن هذا المفهوم... لا تتوقف للمراجعة أو التهجئة، دع الذاكرة تتدفق بحرية!",
                        color = NeuroTextSecondary,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("blurting_input_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeuroCyan,
                    unfocusedBorderColor = NeuroSurfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                enabled = !hasEvaluated
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Submit Evaluation button
            if (!hasEvaluated) {
                Button(
                    onClick = {
                        isTimerActive = false
                        hasEvaluated = true
                        onSaveSession(scorePercentage, 60 - secondsRemaining)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeuroPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("evaluate_blurting_button")
                ) {
                    Text(
                        text = "إنهاء التفريغ وفحص دقة الذاكرة",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Results Section
            AnimatedVisibility(visible = hasEvaluated) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeuroGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "نتائج الفحص العصبي للاستدعاء:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = if (scorePercentage >= 70) NeuroGreen.copy(alpha = 0.2f) else NeuroRose.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "$scorePercentage% استرجاع",
                                        color = if (scorePercentage >= 70) NeuroGreen else NeuroRose,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Successfully recalled
                            Text(
                                text = "✓ نقاط تم استدعاؤها بنجاح (${recalledKeywords.size}):",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeuroGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                recalledKeywords.forEach { kw ->
                                    Surface(
                                        color = NeuroGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeuroGreen.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeuroGreen, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(kw, color = NeuroGreen, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Missed / Blind spots
                            if (missedKeywords.isNotEmpty()) {
                                Text(
                                    text = "✗ فجوات ذاكرة تحتاج تركيز (${missedKeywords.size}):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeuroRose,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    missedKeywords.forEach { kw ->
                                        Surface(
                                            color = NeuroRose.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, NeuroRose.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Error, contentDescription = null, tint = NeuroRose, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(kw, color = NeuroRose, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "الشرح الأصلي الكامل لتثبيت المفهوم:",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeuroTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentCard.fullExplanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                hasEvaluated = false
                                secondsRemaining = 60
                                userBlurtingText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeuroSurfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = NeuroCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة المحاولة", color = NeuroCyan)
                        }

                        Button(
                            onClick = onNextCard,
                            colors = ButtonDefaults.buttonColors(containerColor = NeuroCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("البطاقة التالية", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
