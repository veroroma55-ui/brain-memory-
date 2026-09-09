package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.theme.NeuroSurfaceElevated
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClozeStudyScreen(
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
        Scaffold(
            topBar = {
                NeuralTopBar(
                    title = "الحجب التدريجي (Cloze)",
                    onBack = onBack,
                    isAudioPlaying = isAudioPlaying,
                    onToggleAudio = onToggleAudio,
                    onOpenAudioSettings = onOpenAudioSettings
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد بطاقات في هذه الحزمة بعد",
                    color = NeuroTextSecondary
                )
            }
        }
        return
    }

    val currentCard = cards.getOrElse(activeCardIndex) { cards.first() }

    // Mask level: 1 (0% masked), 2 (25%), 3 (50%), 4 (75%), 5 (100% ghost)
    var maskLevel by remember(currentCard.id) { mutableIntStateOf(2) }
    var showMnemonic by remember(currentCard.id) { mutableStateOf(false) }

    // Set of revealed word indices
    val revealedIndices = remember(currentCard.id, maskLevel) {
        mutableStateMapOf<Int, Boolean>()
    }

    // Split words
    val words = remember(currentCard.fullExplanation) {
        currentCard.fullExplanation.trim().split(Regex("\\s+"))
    }

    // Key terms list for prioritized masking
    val keyTermsList = remember(currentCard.keyTerms) {
        currentCard.keyTerms.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
    }

    // Determine which words should be masked based on level
    val shouldMaskWord: (Int, String) -> Boolean = { index, word ->
        val clean = word.replace(Regex("[^\\p{L}\\p{Nd}]"), "").lowercase()
        val isKeyTerm = keyTermsList.any { clean.contains(it) || it.contains(clean) }

        when (maskLevel) {
            1 -> false // No masking
            2 -> isKeyTerm // Mask only key terms
            3 -> isKeyTerm || (index % 3 == 0) // Mask ~50%
            4 -> isKeyTerm || (index % 2 == 0) // Mask ~75%
            5 -> true // Mask 100% (Ghost Recall)
            else -> false
        }
    }

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "الحجب العصبي التدريجي",
                subtitle = "بطاقة ${activeCardIndex + 1} من ${cards.size}",
                onBack = onBack,
                isAudioPlaying = isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onOpenAudioSettings = onOpenAudioSettings
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onCardReviewed(currentCard, false)
                            onNextCard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeuroRose),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cloze_needs_review_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("يحتاج تكرار")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onCardReviewed(currentCard, true)
                            onNextCard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeuroGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cloze_mastered_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تم التثبيت!")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Level Selector Chips
            Text(
                text = "مستوى التحدي والاسترجاع الذهني:",
                style = MaterialTheme.typography.labelMedium,
                color = NeuroTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    1 to "الكل",
                    2 to "25% حجب",
                    3 to "50% حجب",
                    4 to "75% حجب",
                    5 to "استرجاع شبحي"
                ).forEach { (lvl, label) ->
                    val selected = maskLevel == lvl
                    FilterChip(
                        selected = selected,
                        onClick = { maskLevel = lvl },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeuroCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Concept Title Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentCard.sensoryAnchorEmoji,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentCard.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { showMnemonic = !showMnemonic },
                            modifier = Modifier.testTag("toggle_mnemonic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "تلميح الذاكرة",
                                tint = if (showMnemonic) NeuroPink else NeuroTextSecondary
                            )
                        }
                    }

                    // Mnemonic preview
                    AnimatedVisibility(visible = showMnemonic) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeuroPurple.copy(alpha = 0.15f))
                                .border(1.dp, NeuroPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "💡 الربط العصبي والشاذ (Mnemonic Story):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NeuroPurple
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentCard.mnemonicStory,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Interactive Masked Text Container
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "المفهوم المحجوب (انقر على أي كلمة للكشف عنها):",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeuroTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        words.forEachIndexed { index, word ->
                            val isMaskedTarget = shouldMaskWord(index, word)
                            val isRevealed = revealedIndices[index] == true

                            if (isMaskedTarget && !isRevealed) {
                                // Masked clickable pill
                                Surface(
                                    onClick = { revealedIndices[index] = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeuroPurple.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        NeuroCyan.copy(alpha = 0.8f)
                                    ),
                                    modifier = Modifier.testTag("masked_word_$index")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = NeuroCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (maskLevel == 5) word.firstOrNull()?.toString() ?: "•" else "____",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeuroCyan
                                        )
                                    }
                                }
                            } else {
                                // Visible word (with subtle highlight if it was revealed)
                                val highlightColor by animateColorAsState(
                                    if (isRevealed) NeuroCyan else MaterialTheme.colorScheme.onSurface,
                                    label = "word_color"
                                )
                                Text(
                                    text = word,
                                    fontSize = 17.sp,
                                    fontWeight = if (isRevealed) FontWeight.Bold else FontWeight.Normal,
                                    color = highlightColor,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.clickable {
                                        if (isMaskedTarget) {
                                            revealedIndices[index] = false
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Reveal All button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                words.indices.forEach { revealedIndices[it] = true }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeuroSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("reveal_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = NeuroCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "كشف الكل",
                                color = NeuroCyan,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next / Prev card navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevCard,
                    enabled = activeCardIndex > 0,
                    modifier = Modifier.testTag("cloze_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "السابق",
                        tint = if (activeCardIndex > 0) MaterialTheme.colorScheme.onSurface else NeuroTextSecondary.copy(alpha = 0.3f)
                    )
                }

                Text(
                    text = "${activeCardIndex + 1} / ${cards.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeuroTextSecondary
                )

                IconButton(
                    onClick = onNextCard,
                    enabled = activeCardIndex < cards.size - 1,
                    modifier = Modifier.testTag("cloze_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "التالي",
                        tint = if (activeCardIndex < cards.size - 1) MaterialTheme.colorScheme.onSurface else NeuroTextSecondary.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
