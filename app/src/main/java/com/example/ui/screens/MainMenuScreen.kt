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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Card
import com.example.model.CardRank
import com.example.model.Language
import com.example.model.Suit
import com.example.model.UserPreferences
import com.example.ui.components.CardBackView
import com.example.ui.components.CarvedWoodPlaque
import com.example.ui.components.CoinsChip
import com.example.ui.components.JungleBackdrop
import com.example.ui.components.PlayingCardView
import com.example.ui.components.ProfileChip
import com.example.ui.components.WoodMenuButton
import com.example.ui.theme.OmiColors

@Composable
fun MainMenuScreen(
    preferences: UserPreferences,
    onPlayClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onInviteFriendsClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRulesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSi = preferences.language == Language.SINHALA

    JungleBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: Coins & Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                CoinsChip(coins = preferences.coins)

                Column(horizontalAlignment = Alignment.End) {
                    ProfileChip(
                        playerName = "HASIYA",
                        playerId = "#000",
                        onClick = onProfileClick
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Social login mini badges (G and f) from Screenshot 2
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SocialBadge("G", Color(0xFF4285F4))
                        SocialBadge("f", Color(0xFF1877F2))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // 2. Central Carved Wood Plaque matching Screenshot 2
            CarvedWoodPlaque(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Artwork: Fanned mini cards + OMI badge + card back
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Left Ace
                        PlayingCardView(
                            card = Card(Suit.SPADES, CardRank.ACE),
                            width = 44.dp,
                            height = 66.dp,
                            modifier = Modifier
                                .offset(x = (-46).dp, y = (-2).dp)
                                .rotate(-12f)
                        )

                        // Right Card back (Green paisley)
                        CardBackView(
                            width = 44.dp,
                            height = 66.dp,
                            feltThemeIndex = preferences.selectedTableFeltIndex,
                            modifier = Modifier
                                .offset(x = 52.dp, y = (-4).dp)
                                .rotate(14f)
                        )

                        // Center Badge
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF261D16), Color(0xFF100B07))
                                    )
                                )
                                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "♠ ♥",
                                    color = Color(0xFFFFD700),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "OMI",
                                    color = Color(0xFFFFE082),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "CARD GAME",
                                    color = Color(0xFFC7A76D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons matching Screenshot 2
                    WoodMenuButton(
                        text = if (isSi) "සෙල්ලම් කරන්න (Play)" else "Play",
                        onClick = onPlayClick,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_play"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WoodMenuButton(
                        text = if (isSi) "බහු ක්‍රීඩක (Multiplayer)" else "Multiplayer",
                        onClick = onMultiplayerClick,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_multiplayer"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WoodMenuButton(
                        text = if (isSi) "මිතුරන්ට ආරාධනා කර සෙල්ලම් කරන්න" else "Play with invite Friends",
                        onClick = onInviteFriendsClick,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_invite_friends"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4th Menu button (Kalam Game Club Tournaments & Clubs)
                    WoodMenuButton(
                        text = if (isSi) "කලාම් ක්ලබ් තරඟාවලි (Tournaments)" else "Kalam Club Tournaments",
                        onClick = onTournamentsClick,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_tournaments"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Settings & Stats footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Settings with green gear
                        Row(
                            modifier = Modifier
                                .testTag("btn_settings")
                                .clickable { onSettingsClick() }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSi) "සැකසුම්" else "Settings",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF45220C),
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Green Gear Icon
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFF388E3C))
                                    .border(2.dp, Color(0xFF81C784), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚙",
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        // Rules button & Wins counter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .testTag("btn_menu_rules")
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2E6B47))
                                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(10.dp))
                                    .clickable { onRulesClick() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isSi) "නීති 📜" else "Rules 📜",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = if (isSi) "ජය " else "Wins ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A250E),
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "${preferences.totalWins}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E6B47)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

@Composable
private fun SocialBadge(text: String, bg: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0xFFD6C09A), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = bg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}
