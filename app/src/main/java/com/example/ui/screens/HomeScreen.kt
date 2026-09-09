package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Deck
import com.example.ui.components.CognitiveMethodCard
import com.example.ui.components.MemoryStatCard
import com.example.ui.components.NeuralTopBar
import com.example.ui.theme.NeuroAmber
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroGreen
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroSurfaceElevated
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
    decks: List<Deck>,
    totalCards: Int,
    masteredCards: Int,
    onSelectDeck: (Deck) -> Unit,
    onLaunchGlobalMethod: (AppScreen) -> Unit,
    onCreateDeck: (title: String, category: String, description: String, icon: String, colorHex: String) -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    onOpenAudioSettings: () -> Unit
) {
    var showCreateDeckDialog by remember { mutableStateOf(false) }

    val retentionRate = if (totalCards > 0) {
        ((masteredCards.toFloat() / totalCards.toFloat()) * 100).toInt()
    } else 0

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "ذاكرة الدماغ",
                subtitle = "منظومة الحفظ المعرفي متعددة الأساليب",
                isAudioPlaying = isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onOpenAudioSettings = onOpenAudioSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Hero Visual Banner
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(NeuroCyan, NeuroPurple)),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_study_hero_1788915307192),
                            contentDescription = "قصر الذاكرة والتعلم",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Dark gradient overlay for pristine readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xCC090D16),
                                            Color(0xF0090D16)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = NeuroCyan.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "1000 ميزة عصبية من دماغك",
                                    color = NeuroCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "احفظ المعلومات بطرق استثنائية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "حجب تدريجي • قصر الذاكرة • تفريغ 60ث • فاينمان • لايتنر",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeuroTextSecondary
                            )
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MemoryStatCard(
                        label = "المفاهيم المحفورة",
                        value = "$masteredCards",
                        subValue = "صندوق لايتنر الدائم",
                        color = NeuroGreen,
                        modifier = Modifier.weight(1f)
                    )

                    MemoryStatCard(
                        label = "إجمالي المفاهيم",
                        value = "$totalCards",
                        subValue = "${decks.size} حزم دراسية",
                        color = NeuroCyan,
                        modifier = Modifier.weight(1f)
                    )

                    MemoryStatCard(
                        label = "قوة التثبيت",
                        value = "$retentionRate%",
                        subValue = "كفاءة الاسترجاع",
                        color = NeuroPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section: Cognitive Memory Modes
            item {
                Text(
                    text = "أساليب الحفظ المعرفية المبتكرة:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                CognitiveMethodCard(
                    title = "الحجب العصبي التدريجي (Cloze Deletion)",
                    tag = "الأكثر فاعلية",
                    description = "إخفاء الكلمات تدريجياً (25% -> 50% -> 100%) لفرض تشابك سينابس جديد في القشرة الدماغية.",
                    icon = "🔒",
                    gradientColors = listOf(NeuroCyan, Color(0xFF0284C7)),
                    onClick = { onLaunchGlobalMethod(AppScreen.CLOZE_STUDY) }
                )
            }

            item {
                CognitiveMethodCard(
                    title = "قصر الذاكرة التفاعلي (Method of Loci)",
                    tag = "عبقرية المكان",
                    description = "تثبيت المفاهيم في غرف مكانية (الردهة، المكتبة، المختبر) مع صور بصرية شاذة مستحيل نسيانها.",
                    icon = "🏰",
                    gradientColors = listOf(NeuroPurple, Color(0xFF6D28D9)),
                    onClick = { onLaunchGlobalMethod(AppScreen.MEMORY_PALACE) }
                )
            }

            item {
                CognitiveMethodCard(
                    title = "تفريغ الذاكرة السريع (Blurting Blitz)",
                    tag = "60 ثانية",
                    description = "اكتب كل ما في رأسك بدون توقف، ثم يقارن التطبيق كلماتك بالمصدر ليكشف فوراً فجوات نسيانك!",
                    icon = "⚡",
                    gradientColors = listOf(NeuroPink, Color(0xFFBE185D)),
                    onClick = { onLaunchGlobalMethod(AppScreen.BLURTING_BLITZ) }
                )
            }

            item {
                CognitiveMethodCard(
                    title = "تقنية فاينمان (اشرح لطفل في الـ 8)",
                    tag = "التبسيط الخارق",
                    description = "بسّط المفاهيم المعقدة بدون مصطلحات أكاديمية مع تحليل فوري للتشبيهات والقصص.",
                    icon = "👶",
                    gradientColors = listOf(NeuroAmber, Color(0xFFB45309)),
                    onClick = { onLaunchGlobalMethod(AppScreen.FEYNMAN_STUDY) }
                )
            }

            item {
                CognitiveMethodCard(
                    title = "نظام صناديق لايتنر (SM-2 Spaced Repetition)",
                    tag = "منحنى إبنجهاوس",
                    description = "بطاقات ثلاثية الأبعاد تقفز بين 5 صناديق حسب قوة الذاكرة لتثبيت المفاهيم لأشهر وسنوات.",
                    icon = "📦",
                    gradientColors = listOf(NeuroGreen, Color(0xFF047857)),
                    onClick = { onLaunchGlobalMethod(AppScreen.LEITNER_STUDY) }
                )
            }

            item {
                CognitiveMethodCard(
                    title = "القراءة البيونية وفلاش الكلمات (RSVP Speed)",
                    tag = "موجات بصرية",
                    description = "توجيه العين تلقائياً بتظليل أوائل الحروف، أو عرض فلاش الكلمات بسرعة تصل لـ 450 كلمة/دقيقة.",
                    icon = "👁️",
                    gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0369A1)),
                    onClick = { onLaunchGlobalMethod(AppScreen.BIONIC_STUDY) }
                )
            }

            // Section: Study Decks
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حزم المفاهيم الدراسية (${decks.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showCreateDeckDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeuroSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("create_deck_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NeuroCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حزمة جديدة", color = NeuroCyan, fontSize = 12.sp)
                    }
                }
            }

            items(decks) { deck ->
                val cardColor = Color(
                    try {
                        android.graphics.Color.parseColor(deck.colorHex)
                    } catch (e: Exception) {
                        0xFF06B6D4.toInt()
                    }
                )

                Card(
                    onClick = { onSelectDeck(deck) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardColor.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                        .testTag("deck_item_${deck.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(cardColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (deck.iconName) {
                                        "brain" -> "🧠"
                                        "atom" -> "⚛️"
                                        "book" -> "📖"
                                        else -> "💡"
                                    },
                                    fontSize = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Surface(
                                    color = cardColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = deck.category,
                                        fontSize = 10.sp,
                                        color = cardColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = deck.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = deck.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeuroTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        // Start Study Button
                        Button(
                            onClick = { onSelectDeck(deck) },
                            colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "استكشف",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showCreateDeckDialog) {
        CreateDeckDialog(
            onDismiss = { showCreateDeckDialog = false },
            onCreate = { t, cat, desc, icon, col ->
                onCreateDeck(t, cat, desc, icon, col)
            }
        )
    }
}
