package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyCard
import com.example.ui.components.NeuralTopBar
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroGreen
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextPrimary
import com.example.ui.theme.NeuroTextSecondary

@Composable
fun FeynmanScreen(
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

    var userExplanation by remember(currentCard.id) { mutableStateOf("") }
    var hasEvaluated by remember(currentCard.id) { mutableStateOf(false) }

    // Evaluation metrics
    val wordCount = userExplanation.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    val hasAnalogyWords = listOf("مثل", "كأن", "يشبه", "تخيل", "عبارة عن", "مثلما", "لعبة").any {
        userExplanation.contains(it)
    }

    Scaffold(
        topBar = {
            NeuralTopBar(
                title = "تقنية فاينمان (شرح الطفل)",
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
            // Feynman Rule Header
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroPurple.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeuroPurple, NeuroPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ChildCare, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "قاعدة ريتشارد فاينمان للعبقرية:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "إذا عجزت عن تبسيط الفكرة لطفل في الـ 8 من عمره، فأنت نفسك لم تفهمها بعمق بعد!",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeuroTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Concept & Prompt Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeuroCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentCard.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(currentCard.sensoryAnchorEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "التعريف الأكاديمي:",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeuroTextSecondary
                    )
                    Text(
                        text = currentCard.fullExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeuroTextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Child prompt question
                    Surface(
                        color = NeuroCyan.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("❓", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentCard.feynmanPrompt.ifBlank { "كيف تشرح هذا المفهوم بلغة بسيطة للغاية بدون مصطلحات معقدة؟" },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NeuroCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Student Simplification Text Field
            Text(
                text = "شرحك المبسط المبتكر (استخدم تشبيهات أو قصص):",
                style = MaterialTheme.typography.labelMedium,
                color = NeuroTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userExplanation,
                onValueChange = { userExplanation = it },
                placeholder = {
                    Text(
                        text = "مثال: تخيل أن الخلايا في رأسك مثل أصدقاء في ملعب يرمون كرة ملونة...",
                        color = NeuroTextSecondary,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("feynman_input_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeuroCyan,
                    unfocusedBorderColor = NeuroSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { hasEvaluated = true },
                enabled = userExplanation.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeuroPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evaluate_feynman_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تقييم جودة التبسيط والتشبيه", fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Evaluation Result Card
            AnimatedVisibility(visible = hasEvaluated) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .border(1.dp, NeuroGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تحليل الفهم والتبسيط:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                color = if (hasAnalogyWords && wordCount > 10) NeuroGreen.copy(alpha = 0.2f) else NeuroCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (hasAnalogyWords && wordCount > 10) "فهم إتقاني ممتاز 🌟" else "فهم أولي جيد 👍",
                                    color = if (hasAnalogyWords && wordCount > 10) NeuroGreen else NeuroCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Feedback bullets
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (hasAnalogyWords) "✓" else "💡", color = if (hasAnalogyWords) NeuroGreen else NeuroPink, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasAnalogyWords) "استخدمت تشبيهاً ملموساً يربط الفكرة بالحواس!" else "حاول استخدام كلمات تشبيه مثل (مثل، كأن، يشبه) لترسيخ الفكرة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓", color = NeuroGreen, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حجم الشرح: $wordCount كلمة (التبسيط الممتاز يكون مختصراً ومباشراً)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Suggested Mnemonic Analogy
                        Text(
                            text = "التشبيه الذهني المقترح:",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeuroTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentCard.mnemonicStory,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeuroCyan,
                            lineHeight = 20.sp
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
