package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import com.example.audio.SoundManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.OmiGameEngine
import com.example.model.Card
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.Language
import com.example.model.MatchScore
import com.example.model.PlayedCard
import com.example.model.PlayerPosition
import com.example.model.Suit
import com.example.model.UserPreferences
import com.example.ui.components.CardBackView
import com.example.ui.components.CarvedWoodPlaque
import com.example.ui.components.JungleBackdrop
import com.example.ui.components.PlayerSeatBadge
import com.example.ui.components.PlayingCardView
import com.example.ui.components.TrumpBadge
import com.example.ui.components.WoodMenuButton
import com.example.ui.theme.OmiColors
import kotlinx.coroutines.delay

@Composable
fun GameTableScreen(
    preferences: UserPreferences,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSi = preferences.language == Language.SINHALA

    // Game State management
    var gameState by remember {
        mutableStateOf(
            OmiGameEngine.dealFirstPhase(
                dealer = PlayerPosition.EAST,
                trumpChooser = PlayerPosition.SOUTH,
                currentScore = MatchScore()
            ).first
        )
    }

    var remainingDeck by remember {
        mutableStateOf(
            OmiGameEngine.dealFirstPhase(
                dealer = PlayerPosition.EAST,
                trumpChooser = PlayerPosition.SOUTH,
                currentScore = MatchScore()
            ).second
        )
    }

    var selectedCardId by remember { mutableStateOf<String?>(null) }
    var trickAnnouncement by remember { mutableStateOf<String?>(null) }
    var soundMuted by remember { mutableStateOf(!preferences.soundEnabled) }

    // Start a fresh round
    fun restartRound() {
        SoundManager.playCardDealSound(!soundMuted, preferences.soundVolume)
        val nextChooser = OmiGameEngine.getNextPosition(gameState.dealerPosition)
        val (newState, deck) = OmiGameEngine.dealFirstPhase(
            dealer = nextChooser,
            trumpChooser = PlayerPosition.SOUTH,
            currentScore = gameState.matchScore
        )
        gameState = newState
        remainingDeck = deck
        selectedCardId = null
        trickAnnouncement = null
    }

    // Handle Trump Chosen
    fun onTrumpSelected(trump: Suit) {
        SoundManager.playTrumpSelectedSound(!soundMuted, preferences.soundVolume)
        gameState = OmiGameEngine.completeDeal(
            state = gameState,
            remainingDeck = remainingDeck,
            selectedTrump = trump
        )
    }

    // Play Card for human or bot
    fun playCard(pos: PlayerPosition, card: Card) {
        SoundManager.playCardPlaySound(!soundMuted, preferences.soundVolume)
        val currentTrick = gameState.currentTrick
        val leadSuit = if (currentTrick.isEmpty()) card.suit else gameState.leadSuit ?: card.suit
        val updatedHand = (gameState.players[pos]?.hand ?: emptyList()).filter { it.id != card.id }
        val updatedPlayer = gameState.players[pos]?.copy(hand = updatedHand)

        val updatedTrick = currentTrick + PlayedCard(pos, card)
        val updatedPlayers = gameState.players.toMutableMap()
        if (updatedPlayer != null) {
            updatedPlayers[pos] = updatedPlayer
        }

        if (updatedTrick.size < 4) {
            // Next turn
            val nextTurn = OmiGameEngine.getNextPosition(pos)
            gameState = gameState.copy(
                players = updatedPlayers,
                currentTrick = updatedTrick,
                leadSuit = leadSuit,
                currentTurn = nextTurn,
                statusMessage = if (nextTurn == PlayerPosition.SOUTH) "Your turn to play" else "${updatedPlayers[nextTurn]?.name}'s turn"
            )
        } else {
            // Trick is complete (4 cards played)
            SoundManager.playTrickWonSound(!soundMuted, preferences.soundVolume)
            val trumpSuit = gameState.trumpSuit ?: Suit.SPADES
            val winningCard = OmiGameEngine.evaluateTrickWinner(updatedTrick, leadSuit, trumpSuit)
            val winnerPos = winningCard.playerPosition
            val winner = updatedPlayers[winnerPos]
            val newTricksCount = (winner?.tricksWon ?: 0) + 1
            if (winner != null) {
                updatedPlayers[winnerPos] = winner.copy(tricksWon = newTricksCount)
            }

            val trickNumber = gameState.completedTricksThisRound + 1
            val winnerTeamName = if (winnerPos.isPlayerTeam) {
                if (isSi) "ඔබේ කණ්ඩායම ජයග්‍රහණය කළා!" else "Your team won the trick!"
            } else {
                if (isSi) "ප්‍රතිවාදීන් ජයග්‍රහණය කළා!" else "Opponents won the trick!"
            }

            trickAnnouncement = winnerTeamName

            gameState = gameState.copy(
                players = updatedPlayers,
                currentTrick = updatedTrick,
                lastTrickWinner = winnerPos,
                lastTrickCards = updatedTrick,
                completedTricksThisRound = trickNumber,
                statusMessage = winnerTeamName
            )
        }
    }

    // Bot automation effect
    LaunchedEffect(gameState.phase, gameState.currentTurn, gameState.currentTrick.size) {
        if (gameState.phase == GamePhase.TRUMP_SELECTION) {
            if (gameState.trumpChooserPosition != PlayerPosition.SOUTH) {
                delay(800)
                val botHand = gameState.players[gameState.trumpChooserPosition]?.hand ?: emptyList()
                val chosenTrump = OmiGameEngine.selectBotTrump(botHand)
                onTrumpSelected(chosenTrump)
            }
        } else if (gameState.phase == GamePhase.PLAYING_CARDS) {
            // If trick has 4 cards, wait for user to see, then clear and advance
            if (gameState.currentTrick.size == 4) {
                delay(1200)
                val winner = gameState.lastTrickWinner ?: PlayerPosition.SOUTH
                trickAnnouncement = null

                if (gameState.completedTricksThisRound >= 8) {
                    // Round finished
                    val playerTricks = (gameState.players[PlayerPosition.SOUTH]?.tricksWon ?: 0) +
                            (gameState.players[PlayerPosition.NORTH]?.tricksWon ?: 0)
                    val oppTricks = (gameState.players[PlayerPosition.WEST]?.tricksWon ?: 0) +
                            (gameState.players[PlayerPosition.EAST]?.tricksWon ?: 0)

                    val (newScore, title, msg) = OmiGameEngine.resolveRoundScores(
                        trumpCaller = gameState.trumpChooserPosition,
                        playerTeamTricks = playerTricks,
                        opponentTeamTricks = oppTricks,
                        currentScore = gameState.matchScore
                    )

                    SoundManager.playVictorySound(!soundMuted, preferences.soundVolume)
                    gameState = gameState.copy(
                        matchScore = newScore,
                        isRoundFinished = true,
                        roundResultAnnouncement = "$title\n$msg"
                    )
                } else {
                    // Start next trick
                    gameState = gameState.copy(
                        currentTrick = emptyList(),
                        leadSuit = null,
                        currentTurn = winner,
                        statusMessage = if (winner == PlayerPosition.SOUTH) "Your lead" else "${gameState.players[winner]?.name} leads"
                    )
                }
            } else {
                // If it's a Bot's turn, select and play card
                val turn = gameState.currentTurn
                if (turn != PlayerPosition.SOUTH) {
                    delay(700)
                    val botPlayer = gameState.players[turn]
                    if (botPlayer != null && botPlayer.hand.isNotEmpty()) {
                        val cardToPlay = OmiGameEngine.selectBotCard(
                            botPlayer = botPlayer,
                            leadSuit = gameState.leadSuit,
                            trumpSuit = gameState.trumpSuit ?: Suit.SPADES,
                            currentTrick = gameState.currentTrick
                        )
                        playCard(turn, cardToPlay)
                    }
                }
            }
        }
    }

    val feltColor = when (preferences.selectedTableFeltIndex % 5) {
        0 -> OmiColors.FeltJungleGreen
        1 -> OmiColors.FeltRoyalNavy
        2 -> OmiColors.FeltVelvetWine
        3 -> OmiColors.FeltTeakWood
        else -> OmiColors.FeltGoldenOasis
    }

    val southPlayer = gameState.players[PlayerPosition.SOUTH]
    val northPlayer = gameState.players[PlayerPosition.NORTH]
    val westPlayer = gameState.players[PlayerPosition.WEST]
    val eastPlayer = gameState.players[PlayerPosition.EAST]

    val playerTeamTricks = (southPlayer?.tricksWon ?: 0) + (northPlayer?.tricksWon ?: 0)
    val oppTeamTricks = (westPlayer?.tricksWon ?: 0) + (eastPlayer?.tricksWon ?: 0)

    JungleBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Controls Bar: Coins | Sound | Restart | Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Coin Counter matching Screenshot 4
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF5A3418))
                        .border(1.5.dp, Color(0xFFC7985D), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏛", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${gameState.matchScore.playerTeamTokens}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                // Right controls: Sound | Restart | Exit
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircleIconButton(
                        text = if (soundMuted) "🔇" else "🔊",
                        onClick = { soundMuted = !soundMuted },
                        testTag = "btn_sound_toggle"
                    )
                    CircleIconButton(
                        text = "⟳",
                        onClick = { restartRound() },
                        testTag = "btn_restart"
                    )
                    CircleIconButton(
                        text = "✕",
                        onClick = onExitGame,
                        testTag = "btn_exit_game"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Scoreboard Banner ("Partner 0 | 0 Opponent") matching Screenshot 4 & 5
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF38355C))
                    .border(1.5.dp, Color(0xFF5A5688), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Partner side
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSi) "සහකරු" else "Partner",
                            color = Color(0xFFE2E2F0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$playerTeamTricks",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Center divider with spade motif
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF282542)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gameState.trumpSuit?.symbol ?: "♠",
                            color = if (gameState.trumpSuit?.isRed == true) Color(0xFFFF5252) else Color(0xFFFFD700),
                            fontSize = 18.sp
                        )
                    }

                    // Opponent side
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$oppTeamTricks",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (isSi) "ප්‍රතිවාදීන්" else "Opponent",
                            color = Color(0xFFE2E2F0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Main Oval Felt Table Area with 3D Perspective & Lacquered Bevel Rim
            val density = LocalDensity.current.density
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        rotationX = 10f
                        cameraDistance = 18f * density
                    }
                    .shadow(18.dp, RoundedCornerShape(80.dp), spotColor = Color(0xAA000000))
                    .clip(RoundedCornerShape(80.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                feltColor.copy(alpha = 0.95f),
                                feltColor,
                                Color(0xFF071F11)
                            )
                        )
                    )
                    .border(
                        width = 5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF8D5B2C), Color(0xFF45220C), Color(0xFFC7985D), Color(0xFF2E1303))
                        ),
                        shape = RoundedCornerShape(80.dp)
                    )
            ) {
                // Table Felt 3D Lighting & Mandala Background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 3D overhead warm casino lamp spotlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x28FFF9C4), Color(0x00FFFFFF)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = w * 0.45f
                        )
                    )

                    // Outer recessed felt shadow ring
                    drawCircle(
                        color = Color(0x33000000),
                        radius = w * 0.46f,
                        center = Offset(w * 0.5f, h * 0.5f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                    )
                    // Inner flower ornament with gold gleam
                    drawCircle(
                        color = Color(0x22FFD700),
                        radius = w * 0.18f,
                        center = Offset(w * 0.5f, h * 0.5f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }

                // NORTH PLAYER (Top / Partner)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerSeatBadge(
                        name = if (isSi) "සහකරු (Partner)" else "Partner",
                        avatarEmoji = northPlayer?.avatarEmoji ?: "🧑‍🌾",
                        position = PlayerPosition.NORTH,
                        tricksWon = northPlayer?.tricksWon ?: 0,
                        isCurrentTurn = gameState.currentTurn == PlayerPosition.NORTH
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Cards face down
                    Row(horizontalArrangement = Arrangement.spacedBy((-18).dp)) {
                        val count = (northPlayer?.hand?.size ?: 4).coerceAtMost(6)
                        for (i in 0 until count) {
                            CardBackView(width = 32.dp, height = 48.dp, feltThemeIndex = preferences.selectedTableFeltIndex)
                        }
                    }
                }

                // WEST PLAYER (Left Opponent)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerSeatBadge(
                        name = if (isSi) "ප්‍රතිවාදී 1" else "Opponent",
                        avatarEmoji = westPlayer?.avatarEmoji ?: "👩",
                        position = PlayerPosition.WEST,
                        tricksWon = westPlayer?.tricksWon ?: 0,
                        isCurrentTurn = gameState.currentTurn == PlayerPosition.WEST
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy((-28).dp)) {
                        val count = (westPlayer?.hand?.size ?: 4).coerceAtMost(4)
                        for (i in 0 until count) {
                            CardBackView(width = 28.dp, height = 42.dp, feltThemeIndex = preferences.selectedTableFeltIndex)
                        }
                    }
                }

                // EAST PLAYER (Right Opponent)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerSeatBadge(
                        name = if (isSi) "ප්‍රතිවාදී 2" else "Opponent",
                        avatarEmoji = eastPlayer?.avatarEmoji ?: "🧔",
                        position = PlayerPosition.EAST,
                        tricksWon = eastPlayer?.tricksWon ?: 0,
                        isCurrentTurn = gameState.currentTurn == PlayerPosition.EAST
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy((-28).dp)) {
                        val count = (eastPlayer?.hand?.size ?: 4).coerceAtMost(4)
                        for (i in 0 until count) {
                            CardBackView(width = 28.dp, height = 42.dp, feltThemeIndex = preferences.selectedTableFeltIndex)
                        }
                    }
                }

                // CENTER TRICK AREA
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Lotus Motif
                    Text(
                        text = "❀",
                        fontSize = 32.sp,
                        color = Color(0x33FFFFFF)
                    )

                    // Trump indicator pill in table center
                    if (gameState.trumpSuit != null && gameState.currentTrick.isEmpty()) {
                        TrumpBadge(
                            trumpSuit = gameState.trumpSuit!!,
                            labelPrefix = if (isSi) "තුරුම්පු" else "Trump",
                            modifier = Modifier.offset(y = 20.dp)
                        )
                    }

                    // Render played cards in current trick
                    gameState.currentTrick.forEach { played ->
                        val (offsetX, offsetY, rot) = when (played.playerPosition) {
                            PlayerPosition.SOUTH -> Triple(0.dp, 28.dp, 0f)
                            PlayerPosition.NORTH -> Triple(0.dp, (-28).dp, 0f)
                            PlayerPosition.WEST -> Triple((-28).dp, 0.dp, -12f)
                            PlayerPosition.EAST -> Triple(28.dp, 0.dp, 12f)
                        }

                        PlayingCardView(
                            card = played.card,
                            isPlayable = true,
                            width = 46.dp,
                            height = 68.dp,
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .rotate(rot)
                        )
                    }

                    // Trick winner announcement toast
                    if (trickAnnouncement != null) {
                        Box(
                            modifier = Modifier
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xEE1E242B))
                                .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = trickAnnouncement!!,
                                color = Color(0xFFFFD700),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Human Player (SOUTH) Hand & Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSi) "කාඩ්පතක් තෝරන්න (Choose a card)" else "Choose a card",
                    color = Color(0xFFFFF7ED),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Fanned 8 Cards (or 4 cards during trump selection)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val hand = southPlayer?.hand ?: emptyList()
                    val spacing = if (hand.size > 5) (-16).dp else (-8).dp

                    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        hand.forEach { card ->
                            val isPlayable = if (gameState.phase == GamePhase.PLAYING_CARDS && gameState.currentTurn == PlayerPosition.SOUTH) {
                                OmiGameEngine.isCardPlayable(card, hand, gameState.leadSuit)
                            } else {
                                false
                            }

                            PlayingCardView(
                                card = card,
                                isPlayable = isPlayable || gameState.phase == GamePhase.TRUMP_SELECTION,
                                isSelected = selectedCardId == card.id,
                                width = 50.dp,
                                height = 76.dp,
                                onClick = {
                                    if (gameState.phase == GamePhase.PLAYING_CARDS && gameState.currentTurn == PlayerPosition.SOUTH && isPlayable) {
                                        selectedCardId = card.id
                                        playCard(PlayerPosition.SOUTH, card)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Status Footer: Trump suit | Avatar | Tricks
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF38355C))
                        .border(1.dp, Color(0xFF5A5688), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Trump Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = gameState.trumpSuit?.symbol ?: "♠",
                                fontSize = 16.sp,
                                color = if (gameState.trumpSuit?.isRed == true) Color(0xFFFF5252) else Color(0xFFFFD700)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSi) "තුරුම්පු" else "Trump",
                                color = Color(0xFFD6D6E8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Center Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E6B47))
                                .border(1.5.dp, Color(0xFF84AF96), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤠", fontSize = 18.sp)
                        }

                        // Right Tricks counter
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isSi) "කැපීම් (Tricks)" else "Tricks",
                                color = Color(0xFFD6D6E8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$playerTeamTricks – $oppTeamTricks",
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // 5. Trump Selection Modal matching Screenshot 4
        AnimatedVisibility(
            visible = gameState.phase == GamePhase.TRUMP_SELECTION && gameState.trumpChooserPosition == PlayerPosition.SOUTH,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CarvedWoodPlaque(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("modal_trump_selection")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSi) "තුරුම්පු ලකුණ තෝරන්න" else "Choose a trump suit",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C),
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isSi) "තුරුම්පු" else "Trump",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5A2C10)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // 4 Suit buttons matching Screenshot 4: ♣ Clubs, ♠ Spades, ♥ Hearts, ♦ Diamonds
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SuitButton(Suit.CLUBS, onClick = { onTrumpSelected(Suit.CLUBS) })
                            SuitButton(Suit.SPADES, onClick = { onTrumpSelected(Suit.SPADES) })
                            SuitButton(Suit.HEARTS, onClick = { onTrumpSelected(Suit.HEARTS) })
                            SuitButton(Suit.DIAMONDS, onClick = { onTrumpSelected(Suit.DIAMONDS) })
                        }
                    }
                }
            }
        }

        // 6. Round Result Modal
        AnimatedVisibility(
            visible = gameState.isRoundFinished,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                CarvedWoodPlaque(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSi) "වටය අවසන්!" else "Round Completed!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF45220C),
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = gameState.roundResultAnnouncement ?: "",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF381D06),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "Your Tokens: ${gameState.matchScore.playerTeamTokens}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Opponent Tokens: ${gameState.matchScore.opponentTeamTokens}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        WoodMenuButton(
                            text = if (isSi) "ඊළඟ වටය (Next Round)" else "Next Round",
                            onClick = { restartRound() },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_next_round"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuitButton(suit: Suit, onClick: () -> Unit) {
    val color = if (suit.isRed) OmiColors.SuitRed else OmiColors.SuitBlack
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(80),
        label = "suit_press"
    )

    Box(
        modifier = Modifier
            .testTag("suit_${suit.name}")
            .offset(y = yOffset)
            .size(64.dp)
            .shadow(if (isPressed) 3.dp else 10.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFFFD700))
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPressed) {
                        listOf(Color(0xFFEDE4D3), Color(0xFFDDD2BE))
                    } else {
                        listOf(Color(0xFFFFFFFF), Color(0xFFFFF9ED), Color(0xFFF3E7D3))
                    }
                )
            )
            .border(
                width = 2.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFEE58), Color(0xFFB78103), Color(0xFFFFD54F))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = suit.symbol,
            color = color,
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.shadow(3.dp)
        )
    }
}

@Composable
private fun CircleIconButton(
    text: String,
    onClick: () -> Unit,
    testTag: String = "circle_icon_button"
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(38.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF5A3418))
            .border(1.5.dp, Color(0xFFC7985D), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
