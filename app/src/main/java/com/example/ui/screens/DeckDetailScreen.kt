package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deck
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
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeckDetailScreen(
    deck: Deck,
    cards: List<StudyCard>,
    onLaunchStudyMode: (AppScreen) -> Unit,
    onAddCard: (
        title: String,
        explanation: String,
        keywords: String,
        mnemonic: String,
        palaceRoom: String,
        emoji: String,
        colorHex: String,
        feynmanPrompt: String
    ) -> Unit,
    onDeleteDeck: () -> Unit,
    onBack: () -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    onOpenAudioSettings: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = deck.title,
                subtitle = "${cards.size} مفاهيم مخزنة",
                onBack = onBack,
                isAudioPlaying = isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onOpenAudioSettings = onOpenAudioSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NeuroCyan,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_card_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة بطاقة")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Deck Info Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = NeuroCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = deck.category,
                                    color = NeuroCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = onDeleteDeck,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف الحزمة",
                                    tint = NeuroRose.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = deck.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeuroTextSecondary
                        )
                    }
                }
            }

            item {
                Text(
                    text = "اختر أسلوب الحفظ المعرفي لهذه الحزمة:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeuroTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Study Mode Quick Launchers
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudyLaunchChip(
                        title = "الحجب التدريجي",
                        icon = Icons.Default.Lock,
                        color = NeuroCyan,
                        onClick = { onLaunchStudyMode(AppScreen.CLOZE_STUDY) },
                        testTag = "launch_cloze_button"
                    )

                    StudyLaunchChip(
                        title = "قصر الذاكرة",
                        icon = Icons.Default.DoorFront,
                        color = NeuroPurple,
                        onClick = { onLaunchStudyMode(AppScreen.MEMORY_PALACE) },
                        testTag = "launch_palace_button"
                    )

                    StudyLaunchChip(
                        title = "تفريغ الذاكرة 60ث",
                        icon = Icons.Default.Timer,
                        color = NeuroPink,
                        onClick = { onLaunchStudyMode(AppScreen.BLURTING_BLITZ) },
                        testTag = "launch_blurting_button"
                    )

                    StudyLaunchChip(
                        title = "تقنية فاينمان",
                        icon = Icons.Default.ChildCare,
                        color = NeuroAmber,
                        onClick = { onLaunchStudyMode(AppScreen.FEYNMAN_STUDY) },
                        testTag = "launch_feynman_button"
                    )

                    StudyLaunchChip(
                        title = "صناديق لايتنر",
                        icon = Icons.Default.Layers,
                        color = NeuroGreen,
                        onClick = { onLaunchStudyMode(AppScreen.LEITNER_STUDY) },
                        testTag = "launch_leitner_button"
                    )

                    StudyLaunchChip(
                        title = "قراءة بيونية وفلاش",
                        icon = Icons.Default.Bolt,
                        color = Color(0xFF38BDF8),
                        onClick = { onLaunchStudyMode(AppScreen.BIONIC_STUDY) },
                        testTag = "launch_bionic_button"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "البطاقات والمفاهيم المخزنة (${cards.size}):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (cards.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NeuroSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد بطاقات في هذه الحزمة بعد. اضغط + لإضافة مفهوم!",
                            color = NeuroTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(cards) { card ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeuroSurfaceVariant, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(card.sensoryAnchorEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = card.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    color = NeuroGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "صندوق لايتنر ${card.leitnerBox}",
                                        fontSize = 10.sp,
                                        color = NeuroGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = card.fullExplanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = NeuroTextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        AddEditCardDialog(
            deckId = deck.id,
            onDismiss = { showAddDialog = false },
            onSave = { t, exp, kw, mn, pal, em, col, fey ->
                onAddCard(t, exp, kw, mn, pal, em, col, fey)
            }
        )
    }
}

@Composable
fun StudyLaunchChip(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
