package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
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

data class PalaceRoom(
    val id: String,
    val nameAr: String,
    val icon: ImageVector,
    val ambientColor: Color,
    val descriptionAr: String
)

val PalaceRoomsList = listOf(
    PalaceRoom(
        id = "entrance",
        nameAr = "ردهة الاستقبال",
        icon = Icons.Default.DoorFront,
        ambientColor = Color(0xFF06B6D4),
        descriptionAr = "البوابة الكبرى: مكان وضع المفاهيم التأسيسية والمداخل الأولية"
    ),
    PalaceRoom(
        id = "library",
        nameAr = "القاعة المركزية",
        icon = Icons.Default.AutoStories,
        ambientColor = Color(0xFF8B5CF6),
        descriptionAr = "المكتبة الفخمة: الرفوف العالية للمفاهيم الضخمة والقوانين الأساسية"
    ),
    PalaceRoom(
        id = "lab",
        nameAr = "مختبر الأفكار",
        icon = Icons.Default.Science,
        ambientColor = Color(0xFFEC4899),
        descriptionAr = "المختبر الكيميائي: مخصص للتفاعلات والروابط المعقدة والمشابك"
    ),
    PalaceRoom(
        id = "garden",
        nameAr = "الحديقة التأملية",
        icon = Icons.Default.Park,
        ambientColor = Color(0xFF10B981),
        descriptionAr = "الفناء المفتوح: المفاهيم الفلسفية والتأملات الكونية العميقة"
    )
)

@Composable
fun MemoryPalaceScreen(
    cards: List<StudyCard>,
    onBack: () -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    onOpenAudioSettings: () -> Unit
) {
    var selectedRoomIndex by remember { mutableIntStateOf(0) }
    val currentRoom = PalaceRoomsList[selectedRoomIndex]

    // Cards in this room or fallback
    val roomCards = remember(cards, currentRoom) {
        cards.filter { card ->
            card.memoryPalaceRoom.contains(currentRoom.nameAr.take(5))
        }.ifEmpty {
            // If none explicitly matched, assign deterministically based on hash
            cards.filter { (it.id % PalaceRoomsList.size).toInt() == selectedRoomIndex }
        }
    }

    var selectedCardIndex by remember(selectedRoomIndex) { mutableIntStateOf(0) }
    val activeCard = roomCards.getOrNull(selectedCardIndex)

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "قصر الذاكرة (Method of Loci)",
                subtitle = currentRoom.nameAr,
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
            // Palace Theory banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NeuroSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(currentRoom.ambientColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏰", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "سر خطباء اليونان والرومان",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الدماغ البشري يتذكر الأماكن الجغرافية والصور الغريبة بـ 10 أضعاف كفاءة النصوص المجردة!",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeuroTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room selector tabs
            Text(
                text = "اختر الغرفة في قصرك الذهني:",
                style = MaterialTheme.typography.labelMedium,
                color = NeuroTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(PalaceRoomsList.indices.toList()) { index ->
                    val room = PalaceRoomsList[index]
                    val isSelected = selectedRoomIndex == index
                    Surface(
                        onClick = {
                            selectedRoomIndex = index
                            selectedCardIndex = 0
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) room.ambientColor.copy(alpha = 0.2f) else NeuroSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) room.ambientColor else Color.Transparent
                        ),
                        modifier = Modifier.testTag("palace_room_tab_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = room.icon,
                                contentDescription = null,
                                tint = if (isSelected) room.ambientColor else NeuroTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = room.nameAr,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) room.ambientColor else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room description
            Text(
                text = currentRoom.descriptionAr,
                style = MaterialTheme.typography.bodySmall,
                color = NeuroTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (activeCard == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NeuroSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد مفاهيم مخزنة في هذه الغرفة حالياً",
                        color = NeuroTextSecondary
                    )
                }
            } else {
                // Interactive Locus Card
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            currentRoom.ambientColor.copy(alpha = 0.5f),
                            RoundedCornerShape(22.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = currentRoom.ambientColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeCard.memoryPalaceRoom,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = currentRoom.ambientColor
                                )
                            }

                            Text(
                                text = activeCard.sensoryAnchorEmoji,
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = activeCard.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = activeCard.fullExplanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeuroTextPrimary,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Vivid Mnemonic Story Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            NeuroPurple.copy(alpha = 0.2f),
                                            NeuroCyan.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .border(1.dp, NeuroPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔮", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المشهد الذهني الشاذ المثبّت في هذه الزاوية:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NeuroPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeCard.mnemonicStory,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Room sub-cards carousel if multiple
                        if (roomCards.size > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (selectedCardIndex > 0) selectedCardIndex--
                                    },
                                    enabled = selectedCardIndex > 0
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "السابق")
                                }

                                Text(
                                    text = "الموضع ${selectedCardIndex + 1} من ${roomCards.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeuroTextSecondary
                                )

                                IconButton(
                                    onClick = {
                                        if (selectedCardIndex < roomCards.size - 1) selectedCardIndex++
                                    },
                                    enabled = selectedCardIndex < roomCards.size - 1
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "التالي")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Guided Palace Walk Button
            Button(
                onClick = {
                    selectedRoomIndex = (selectedRoomIndex + 1) % PalaceRoomsList.size
                    selectedCardIndex = 0
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentRoom.ambientColor
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("palace_next_room_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الانتقال للغرفة التالية في الجولة الذهنية",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
