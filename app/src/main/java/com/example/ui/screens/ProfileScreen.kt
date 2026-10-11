package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserPreferences
import com.example.ui.components.CarvedWoodPlaque
import com.example.ui.components.JungleBackdrop
import com.example.ui.theme.OmiColors

data class MatchHistoryItem(
    val id: String,
    val opponents: String,
    val result: String, // "WON" or "LOST"
    val score: String,
    val badge: String? = null
)

@Composable
fun ProfileScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
    onViewRules: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSi = preferences.language == Language.SINHALA
    var selectedAvatar by remember { mutableStateOf("🤠") }
    val avatarChoices = listOf("🤠", "👑", "👸", "🦁", "🦚", "🎯")

    val matches = remember {
        listOf(
            MatchHistoryItem("1", "Kasuni & Sunil", "WON", "10 - 4", "KAPOTHI x2"),
            MatchHistoryItem("2", "Kandy Royal Club", "WON", "10 - 7", "KASSAPUWA"),
            MatchHistoryItem("3", "Colombo Masters", "LOST", "6 - 10", null),
            MatchHistoryItem("4", "Galle Lions", "WON", "10 - 5", "KAPOTHI")
        )
    }

    val totalGames = 18
    val wins = preferences.totalWins.coerceAtLeast(12)
    val losses = totalGames - wins
    val winRate = String.format("%.1f%%", (wins.toDouble() / totalGames) * 100)

    JungleBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .testTag("btn_profile_back")
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF5D2E14))
                        .border(1.5.dp, Color(0xFFC7985D), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = if (isSi) "සාමාජික ප්‍රොෆයිලය" else "Member Profile",
                    color = Color(0xFFFFF2D6),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif
                )

                // Rules Button
                Box(
                    modifier = Modifier
                        .testTag("btn_view_rules")
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E6B47))
                        .border(1.5.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                        .clickable { onViewRules() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSi) "නීති (Rules)" else "Rules 📜",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Profile Plaque
            CarvedWoodPlaque(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Avatar & Name Card
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF2E6B47))
                                .border(3.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = selectedAvatar, fontSize = 40.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "HASIYA #000",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF381D06),
                            fontFamily = FontFamily.Serif
                        )

                        Text(
                            text = "Kalam Social Game Club · Gold VIP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF8C5C00)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Avatar Picker Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarChoices.forEach { av ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedAvatar == av) Color(0xFFFFD54F) else Color(0x66FFFFFF))
                                        .border(
                                            1.5.dp,
                                            if (selectedAvatar == av) Color(0xFF8C5C00) else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable { selectedAvatar = av },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = av, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Statistics Grid
                        Text(
                            text = if (isSi) "ක්‍රීඩා සංඛ්‍යාලේඛන (Game Stats)" else "Club Game Statistics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Played", "$totalGames", Color(0xFF3E2723), Modifier.weight(1f))
                            StatBox("Wins", "$wins", Color(0xFF1B5E20), Modifier.weight(1f))
                            StatBox("Losses", "$losses", Color(0xFFB71C1C), Modifier.weight(1f))
                            StatBox("Win Rate", winRate, Color(0xFF0D47A1), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Kapothi", "5", Color(0xFF558B2F), Modifier.weight(1f))
                            StatBox("Kassapuwa", "4", Color(0xFFE65100), Modifier.weight(1f))
                            StatBox("Streak", "3 W", Color(0xFF6A1B9A), Modifier.weight(1f))
                            StatBox("Coins", "2,500", Color(0xFFF57F17), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = if (isSi) "මෑත තරඟ ඉතිහාසය (Match History)" else "Recent Match History",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Match history items
                    items(matches) { match ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF7ED))
                                .border(1.dp, Color(0xFFDEBA8B), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = match.opponents,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF381D06)
                                )
                                Text(
                                    text = "Score: ${match.score}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (match.badge != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFECB3))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = match.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF8C5C00)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Text(
                                    text = match.result,
                                    color = if (match.result == "WON") Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFF7ED))
            .border(1.dp, Color(0xFFDEBA8B), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D4C41)
            )
        }
    }
}
