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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NeuroCyan
import com.example.ui.theme.NeuroPink
import com.example.ui.theme.NeuroPurple
import com.example.ui.theme.NeuroSurfaceVariant
import com.example.ui.theme.NeuroTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditCardDialog(
    deckId: Long,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        explanation: String,
        keywords: String,
        mnemonic: String,
        palaceRoom: String,
        emoji: String,
        colorHex: String,
        feynmanPrompt: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    var mnemonic by remember { mutableStateOf("") }
    var palaceRoom by remember { mutableStateOf("ردهة الاستقبال - الباب الذهبي") }
    var selectedEmoji by remember { mutableStateOf("🧠") }
    var selectedColor by remember { mutableStateOf("#06B6D4") }
    var feynmanPrompt by remember { mutableStateOf("") }

    val palaceRooms = listOf(
        "ردهة الاستقبال - الباب الذهبي",
        "القاعة المركزية - الرف العلوي",
        "مختبر الأفكار - طاولة المجهر",
        "الحديقة التأملية - النافورة الكبرى"
    )

    val emojiChoices = listOf("🧠", "⚡", "🔬", "⏳", "🌌", "📖", "💡", "🎯")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeuroCyan.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                .testTag("add_card_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة بطاقة معرفية خارقة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المفهوم أو السؤال") },
                    modifier = Modifier.fillMaxWidth().testTag("card_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = explanation,
                    onValueChange = {
                        explanation = it
                        if (keywords.isBlank() && it.isNotBlank()) {
                            // Extract keywords automatically
                            keywords = it.split(" ").filter { word -> word.length > 4 }.take(4).joinToString(", ")
                        }
                    },
                    label = { Text("الشرح الكامل والتفاصيل للحفظ") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("card_explanation_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("الكلمات المفتاحية للحجب (مفصولة بفواصل)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mnemonic generator button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "القصة الذهنية الشاذة (Mnemonic):",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeuroPurple,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                mnemonic = "تخيل $title وكأنه عملاق كرتوني يرتدي نظارة ذهبية ويرقص في سماء مليئة بنجوم الكريستال!"
                            }
                        }
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "توليد قصة", tint = NeuroCyan)
                    }
                }

                OutlinedTextField(
                    value = mnemonic,
                    onValueChange = { mnemonic = it },
                    placeholder = { Text("قصة غريبة ومبالغ فيها لترسيخ الذاكرة...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emoji Anchor
                Text(
                    text = "الأيقونة الحسية الرابطة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeuroTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    emojiChoices.forEach { em ->
                        val isSel = selectedEmoji == em
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSel) NeuroCyan.copy(alpha = 0.3f) else NeuroSurfaceVariant)
                                .border(if (isSel) 2.dp else 0.dp, NeuroCyan, CircleShape)
                                .clickable { selectedEmoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(em, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Palace Room choice
                Text(
                    text = "غرفة قصر الذاكرة المخصصة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeuroTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    palaceRooms.forEach { room ->
                        val isSel = palaceRoom == room
                        Surface(
                            onClick = { palaceRoom = room },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) NeuroPurple.copy(alpha = 0.2f) else NeuroSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeuroPurple else Color.Transparent)
                        ) {
                            Text(
                                text = room.take(15) + "...",
                                fontSize = 11.sp,
                                color = if (isSel) NeuroPurple else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && explanation.isNotBlank()) {
                            onSave(
                                title,
                                explanation,
                                keywords,
                                mnemonic.ifBlank { "تخيل $title مضاءً بنور ساطع في ذاكرتك" },
                                palaceRoom,
                                selectedEmoji,
                                selectedColor,
                                feynmanPrompt
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeuroCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_card_button")
                ) {
                    Text("حفظ البطاقة في الشبكة العصبية", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
