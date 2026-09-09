package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyCard
import com.example.ui.components.NeuralTopBar
import com.example.ui.theme.NeuroAmber
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroGreen
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroRose
import com.example.ui.theme.NeuroSurfaceElevated
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary

@Composable
fun LeitnerScreen(
    cards: List<StudyCard>,
    activeCardIndex: Int,
    onNextCard: () -> Unit,
    onPrevCard: () -> Unit,
    onCardReviewed: (StudyCard, Boolean) -> Unit,
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
    var isFlipped by remember(currentCard.id) { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_flip"
    )

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "نظام لايتنر (التكرار المتباعد)",
                subtitle = "البطاقة ${activeCardIndex + 1} من ${cards.size}",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Leitner 5 Boxes Shelf
            Text(
                text = "مستوى التثبيت في صناديق لايتنر الـ 5:",
                style = MaterialTheme.typography.labelMedium,
                color = NeuroTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..5).forEach { boxNum ->
                    val isActiveBox = currentCard.leitnerBox == boxNum
                    val boxColor = when (boxNum) {
                        1 -> NeuroRose
                        2 -> NeuroAmber
                        3 -> NeuroCyan
                        4 -> NeuroPurple
                        5 -> NeuroGreen
                        else -> NeuroCyan
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isActiveBox) boxColor.copy(alpha = 0.25f) else NeuroSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isActiveBox) 2.dp else 1.dp,
                            if (isActiveBox) boxColor else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (boxNum == 5) Icons.Default.AllInclusive else Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = if (isActiveBox) boxColor else NeuroTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ص$boxNum",
                                fontSize = 11.sp,
                                fontWeight = if (isActiveBox) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActiveBox) boxColor else NeuroTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The Flip Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped }
                    .testTag("leitner_flip_card")
            ) {
                if (rotation <= 90f) {
                    // Front Face
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                1.5.dp,
                                Brush.linearGradient(listOf(NeuroCyan, NeuroPurple)),
                                RoundedCornerShape(24.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = NeuroCyan.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "المفهوم الأساسي",
                                        color = NeuroCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = "اقلب البطاقة",
                                    tint = NeuroTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentCard.sensoryAnchorEmoji,
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = currentCard.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Text(
                                text = "اضغط على البطاقة لقلبها واختبار تذكرك",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeuroTextSecondary
                            )
                        }
                    }
                } else {
                    // Back Face
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                            .border(
                                1.5.dp,
                                Brush.linearGradient(listOf(NeuroPurple, NeuroPink)),
                                RoundedCornerShape(24.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "التفاصيل المحفورة في الذاكرة:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeuroPurple,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = null,
                                    tint = NeuroTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = currentCard.fullExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            // Mnemonic Reminder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeuroPurple.copy(alpha = 0.15f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "💡 ${currentCard.mnemonicStory}",
                                    fontSize = 12.sp,
                                    color = NeuroTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Leitner Action Controls
            Text(
                text = "كيف كان مستوى استرجاعك لهذه البطاقة؟",
                style = MaterialTheme.typography.labelMedium,
                color = NeuroTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Forgot -> demote to box 1
                Button(
                    onClick = {
                        onCardReviewed(currentCard, false)
                        isFlipped = false
                        onNextCard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeuroRose),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("leitner_forgot_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسيت (صندوق 1)", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Remembered -> promote box
                Button(
                    onClick = {
                        onCardReviewed(currentCard, true)
                        isFlipped = false
                        onNextCard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeuroGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("leitner_remembered_button")
                ) {
                    Icon(Icons.Default.Done, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تذكرت (+1 صندوق)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
