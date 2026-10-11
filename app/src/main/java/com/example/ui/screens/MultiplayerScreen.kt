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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.ui.components.CoinsChip
import com.example.ui.components.JungleBackdrop
import com.example.ui.components.WoodMenuButton
import com.example.ui.theme.OmiColors

data class ClubTable(
    val id: String,
    val name: String,
    val stakeCoins: Int,
    val activePlayers: Int,
    val totalSeats: Int = 4,
    val status: String = "Waiting"
)

@Composable
fun MultiplayerScreen(
    preferences: UserPreferences,
    isInviteFriendsMode: Boolean,
    onStartGame: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSi = preferences.language == Language.SINHALA
    var joinCodeInput by remember { mutableStateOf("") }
    var privateRoomCode by remember { mutableStateOf("KLM-8492") }
    var seat2Occupied by remember { mutableStateOf(false) }
    var seat3Occupied by remember { mutableStateOf(true) }
    var seat4Occupied by remember { mutableStateOf(false) }

    val tables = remember {
        listOf(
            ClubTable("1", "Kalam Colombo Club", 250, 3),
            ClubTable("2", "Kandy Royal Lounge", 500, 2),
            ClubTable("3", "Tropical Jungle Masters", 1000, 1),
            ClubTable("4", "Galle Fort Omi Table", 250, 4, status = "In Progress")
        )
    }

    JungleBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .testTag("btn_multiplayer_back")
                        .size(42.dp)
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
                    text = if (isInviteFriendsMode) {
                        if (isSi) "මිතුරු ආරාධනා (Private Table)" else "Private Table"
                    } else {
                        if (isSi) "කලාම් සමාජ ක්‍රීඩා ක්ලබ්" else "Kalam Social Game Club"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFF3D6),
                    fontFamily = FontFamily.Serif
                )

                CoinsChip(coins = preferences.coins)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isInviteFriendsMode) {
                // Private Table / Invite Friends Mode
                CarvedWoodPlaque(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSi) "ඔබේ මේසයේ කේතය (Room Code)" else "Your Table Room Code",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Room Code Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(4.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E242B))
                                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = privateRoomCode,
                                color = Color(0xFFFFD700),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4 Seats Representation
                        Text(
                            text = if (isSi) "මේසයේ ආසන (Table Seats: 4 Players)" else "Table Seats (4 Players)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SeatItem("HASIYA (Host)", "🤠", isReady = true, isMe = true)
                            SeatItem(
                                if (seat2Occupied) "Kasuni" else "Empty / Bot",
                                if (seat2Occupied) "👩" else "🤖",
                                isReady = seat2Occupied,
                                onClick = { seat2Occupied = !seat2Occupied }
                            )
                            SeatItem(
                                if (seat3Occupied) "Nimal (Partner)" else "Empty / Bot",
                                if (seat3Occupied) "🧑‍🌾" else "🤖",
                                isReady = seat3Occupied,
                                onClick = { seat3Occupied = !seat3Occupied }
                            )
                            SeatItem(
                                if (seat4Occupied) "Sunil" else "Empty / Bot",
                                if (seat4Occupied) "🧔" else "🤖",
                                isReady = seat4Occupied,
                                onClick = { seat4Occupied = !seat4Occupied }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Join existing room section
                        Text(
                            text = if (isSi) "නැතහොත් වෙනත් කේතයකට එකතු වන්න:" else "Or Join Friend's Room Code:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = joinCodeInput,
                                onValueChange = { joinCodeInput = it.uppercase() },
                                placeholder = { Text("e.g. KLM-9912", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2E6B47))
                                    .clickable {
                                        if (joinCodeInput.isNotEmpty()) {
                                            privateRoomCode = joinCodeInput
                                        }
                                    }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = if (isSi) "එක්වන්න" else "Join", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        WoodMenuButton(
                            text = if (isSi) "ක්‍රීඩාව ආරම්භ කරන්න (Start Game)" else "Start Game (Begin Omi)",
                            onClick = onStartGame,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_start_private_game"
                        )
                    }
                }
            } else {
                // Kalam Social Game Club Lobby
                CarvedWoodPlaque(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = if (isSi) "ක්‍රියාකාරී කලාම් මේස (Active Club Tables)" else "Active Kalam Club Tables",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C),
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(tables) { table ->
                                TableRoomCard(
                                    table = table,
                                    onJoin = onStartGame
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        WoodMenuButton(
                            text = if (isSi) "+ අලුත් මේසයක් සාදන්න (Create Table)" else "+ Create New Table",
                            onClick = onStartGame,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_create_table"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatItem(
    name: String,
    emoji: String,
    isReady: Boolean,
    isMe: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isReady) Color(0xFF2E6B47) else Color(0xFF755B42))
                .border(2.dp, if (isMe) Color(0xFFFFD700) else Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF381D06),
            maxLines = 1
        )
        Text(
            text = if (isReady) "READY" else "TAP BOT",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isReady) Color(0xFF1B5E20) else Color(0xFF755B42)
        )
    }
}

@Composable
private fun TableRoomCard(
    table: ClubTable,
    onJoin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF7ED))
            .border(1.5.dp, Color(0xFFC7985D), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = table.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF381D06)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏛 Entry: ${table.stakeCoins} Coins", fontSize = 12.sp, color = Color(0xFF8C5C00), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "👥 ${table.activePlayers}/${table.totalSeats} Players", fontSize = 12.sp, color = Color(0xFF45220C))
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (table.activePlayers < 4) Color(0xFF2E7D32) else Color(0xFF757575))
                .clickable(enabled = table.activePlayers < 4) { onJoin() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (table.activePlayers < 4) "Play" else "Full",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
