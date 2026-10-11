package com.example.engine

import com.example.model.Card
import com.example.model.CardRank
import com.example.model.GameState
import com.example.model.MatchScore
import com.example.model.PlayedCard
import com.example.model.Player
import com.example.model.PlayerPosition
import com.example.model.Suit
import kotlin.random.Random

object OmiGameEngine {

    /**
     * Build the standard 32-card Sri Lankan Omi deck (7, 8, 9, 10, J, Q, K, A)
     */
    fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (suit in Suit.entries) {
            for (rank in CardRank.entries) {
                deck.add(Card(suit, rank))
            }
        }
        return deck.shuffled(Random(System.currentTimeMillis()))
    }

    /**
     * Start a new round:
     * Deals 4 cards to each player first, then sets up trump selection phase.
     */
    fun dealFirstPhase(
        dealer: PlayerPosition,
        trumpChooser: PlayerPosition,
        currentScore: MatchScore
    ): Pair<GameState, List<Card>> {
        val fullDeck = createDeck()
        // Deal 4 cards each
        val firstBatch = mutableMapOf<PlayerPosition, List<Card>>()
        var cursor = 0
        for (pos in PlayerPosition.entries) {
            val hand4 = fullDeck.subList(cursor, cursor + 4).sortedWith(compareBy({ it.suit.ordinal }, { -it.rank.power }))
            firstBatch[pos] = hand4
            cursor += 4
        }
        val remainingDeck = fullDeck.subList(cursor, fullDeck.size)

        val players = mapOf(
            PlayerPosition.SOUTH to Player(
                position = PlayerPosition.SOUTH,
                name = "HASIYA",
                avatarEmoji = "🤠",
                isHuman = true,
                hand = firstBatch[PlayerPosition.SOUTH] ?: emptyList()
            ),
            PlayerPosition.WEST to Player(
                position = PlayerPosition.WEST,
                name = "Kasuni",
                avatarEmoji = "👩",
                isHuman = false,
                hand = firstBatch[PlayerPosition.WEST] ?: emptyList()
            ),
            PlayerPosition.NORTH to Player(
                position = PlayerPosition.NORTH,
                name = "Nimal",
                avatarEmoji = "🧑‍🌾",
                isHuman = false,
                hand = firstBatch[PlayerPosition.NORTH] ?: emptyList()
            ),
            PlayerPosition.EAST to Player(
                position = PlayerPosition.EAST,
                name = "Sunil",
                avatarEmoji = "🧔",
                isHuman = false,
                hand = firstBatch[PlayerPosition.EAST] ?: emptyList()
            )
        )

        val state = GameState(
            phase = com.example.model.GamePhase.TRUMP_SELECTION,
            players = players,
            dealerPosition = dealer,
            trumpChooserPosition = trumpChooser,
            trumpSuit = null,
            currentTurn = trumpChooser,
            currentTrick = emptyList(),
            leadSuit = null,
            completedTricksThisRound = 0,
            matchScore = currentScore,
            statusMessage = if (trumpChooser == PlayerPosition.SOUTH) "Choose a trump suit" else "${players[trumpChooser]?.name} is choosing trump..."
        )

        return Pair(state, remainingDeck)
    }

    /**
     * Complete dealing remaining 4 cards to each player after trump suit is decided.
     */
    fun completeDeal(
        state: GameState,
        remainingDeck: List<Card>,
        selectedTrump: Suit
    ): GameState {
        var cursor = 0
        val updatedPlayers = state.players.toMutableMap()

        for (pos in PlayerPosition.entries) {
            val existing = updatedPlayers[pos]?.hand ?: emptyList()
            val next4 = remainingDeck.subList(cursor, cursor + 4)
            val full8 = (existing + next4).sortedWith(compareBy({ it.suit.ordinal }, { -it.rank.power }))
            cursor += 4

            val player = updatedPlayers[pos]
            if (player != null) {
                updatedPlayers[pos] = player.copy(hand = full8, tricksWon = 0)
            }
        }

        return state.copy(
            phase = com.example.model.GamePhase.PLAYING_CARDS,
            trumpSuit = selectedTrump,
            currentTurn = state.trumpChooserPosition,
            currentTrick = emptyList(),
            leadSuit = null,
            players = updatedPlayers,
            statusMessage = "Trump is ${selectedTrump.displayNameEn} ${selectedTrump.symbol}"
        )
    }

    /**
     * Check if a card is legally playable according to Sri Lankan Omi rules:
     * - If leading, any card can be played.
     * - If following, must follow suit if player holds any cards of leadSuit.
     * - If player has no cards of leadSuit, any card can be played (trump or discard).
     */
    fun isCardPlayable(card: Card, hand: List<Card>, leadSuit: Suit?): Boolean {
        if (leadSuit == null) return true
        val hasLeadSuit = hand.any { it.suit == leadSuit }
        return if (hasLeadSuit) {
            card.suit == leadSuit
        } else {
            true
        }
    }

    /**
     * Determine winner of a 4-card trick according to Omi rules:
     * - Highest trump card wins if any trump played.
     * - Otherwise highest card of leadSuit wins.
     */
    fun evaluateTrickWinner(trick: List<PlayedCard>, leadSuit: Suit, trumpSuit: Suit): PlayedCard {
        require(trick.isNotEmpty()) { "Trick cannot be empty" }
        val trumpCards = trick.filter { it.card.suit == trumpSuit }
        return if (trumpCards.isNotEmpty()) {
            trumpCards.maxByOrNull { it.card.rank.power } ?: trick.first()
        } else {
            val leadCards = trick.filter { it.card.suit == leadSuit }
            leadCards.maxByOrNull { it.card.rank.power } ?: trick.first()
        }
    }

    /**
     * Simple intelligent Bot AI for playing a card in Omi:
     */
    fun selectBotCard(botPlayer: Player, leadSuit: Suit?, trumpSuit: Suit, currentTrick: List<PlayedCard>): Card {
        val hand = botPlayer.hand
        val legalCards = hand.filter { isCardPlayable(it, hand, leadSuit) }
        if (legalCards.isEmpty()) return hand.first()

        if (leadSuit == null) {
            // Leading:
            // Prefer leading Ace or King of a non-trump suit, or highest card of longest suit
            val aces = legalCards.filter { it.rank == CardRank.ACE && it.suit != trumpSuit }
            if (aces.isNotEmpty()) return aces.first()
            val highCards = legalCards.filter { it.rank.power >= CardRank.KING.power && it.suit != trumpSuit }
            if (highCards.isNotEmpty()) return highCards.first()
            // Otherwise play highest card
            return legalCards.maxByOrNull { it.rank.power } ?: legalCards.first()
        } else {
            // Following:
            val followingSuit = legalCards.filter { it.suit == leadSuit }
            if (followingSuit.isNotEmpty()) {
                val currentWinningCard = evaluateTrickWinner(currentTrick, leadSuit, trumpSuit)
                val isPartnerWinning = currentWinningCard.playerPosition.isPlayerTeam == botPlayer.position.isPlayerTeam

                if (isPartnerWinning) {
                    // Play lowest to save strength
                    return followingSuit.minByOrNull { it.rank.power } ?: followingSuit.first()
                } else {
                    // Try to beat current winner if possible
                    val winningRank = if (currentWinningCard.card.suit == leadSuit) currentWinningCard.card.rank.power else -1
                    val winningMoves = followingSuit.filter { it.rank.power > winningRank }
                    return if (winningMoves.isNotEmpty()) {
                        winningMoves.minByOrNull { it.rank.power } ?: winningMoves.first()
                    } else {
                        followingSuit.minByOrNull { it.rank.power } ?: followingSuit.first()
                    }
                }
            } else {
                // Out of lead suit: can trump or discard
                val trumps = legalCards.filter { it.suit == trumpSuit }
                val currentWinningCard = evaluateTrickWinner(currentTrick, leadSuit, trumpSuit)
                val isPartnerWinning = currentWinningCard.playerPosition.isPlayerTeam == botPlayer.position.isPlayerTeam

                if (isPartnerWinning) {
                    // Partner winning, slough lowest non-trump card
                    val nonTrumps = legalCards.filter { it.suit != trumpSuit }
                    return nonTrumps.minByOrNull { it.rank.power } ?: legalCards.minByOrNull { it.rank.power } ?: legalCards.first()
                } else {
                    // Cut with trump if we have trump
                    if (trumps.isNotEmpty()) {
                        if (currentWinningCard.card.suit == trumpSuit) {
                            val overTrumps = trumps.filter { it.rank.power > currentWinningCard.card.rank.power }
                            if (overTrumps.isNotEmpty()) {
                                return overTrumps.minByOrNull { it.rank.power } ?: overTrumps.first()
                            }
                        } else {
                            // Trump with lowest trump
                            return trumps.minByOrNull { it.rank.power } ?: trumps.first()
                        }
                    }
                    // Cannot win trick, discard lowest card
                    return legalCards.minByOrNull { it.rank.power } ?: legalCards.first()
                }
            }
        }
    }

    /**
     * Bot chooses trump from first 4 cards.
     * Evaluates suit with highest count and high rank cards.
     */
    fun selectBotTrump(hand: List<Card>): Suit {
        val suitCounts = hand.groupBy { it.suit }
        // Pick suit with most cards, tie break with highest total power
        return suitCounts.entries.maxByOrNull { entry ->
            entry.value.size * 10 + entry.value.sumOf { it.rank.power }
        }?.key ?: Suit.SPADES
    }

    /**
     * Calculate Omi round tokens:
     * Total tricks = 8.
     * If Trump Caller's team has >= 5 tricks:
     *   - 8 tricks: Kapothi (2 tokens + carried over)
     *   - 5, 6, 7 tricks: 1 token + carried over
     * If Defending team has >= 5 tricks:
     *   - Kassapuwa (2 tokens + carried over)
     * If 4-4 tie:
     *   - 0 tokens awarded, 1 token carries over.
     */
    fun resolveRoundScores(
        trumpCaller: PlayerPosition,
        playerTeamTricks: Int,
        opponentTeamTricks: Int,
        currentScore: MatchScore
    ): Triple<MatchScore, String, String> {
        val callerIsPlayerTeam = trumpCaller.isPlayerTeam
        val carried = currentScore.carriedOverTokens

        var newPlayerTokens = currentScore.playerTeamTokens
        var newOpponentTokens = currentScore.opponentTeamTokens
        var newCarried = 0
        var title = ""
        var message = ""

        if (playerTeamTricks > opponentTeamTricks) {
            // Player team won more tricks
            val tokensWon = if (playerTeamTricks == 8) {
                // Kapothi!
                title = "KAPOTHI! (කපෝති!)"
                2 + carried
            } else {
                if (callerIsPlayerTeam) {
                    title = "Round Won! (+1 Token)"
                    1 + carried
                } else {
                    // Kassapuwa! (Defenders won)
                    title = "KASSAPUWA! (+2 Tokens)"
                    2 + carried
                }
            }
            newPlayerTokens += tokensWon
            message = "Your team won $playerTeamTricks tricks to $opponentTeamTricks! (+$tokensWon tokens)"
        } else if (opponentTeamTricks > playerTeamTricks) {
            // Opponents won more tricks
            val tokensWon = if (opponentTeamTricks == 8) {
                title = "Opponent KAPOTHI!"
                2 + carried
            } else {
                if (!callerIsPlayerTeam) {
                    title = "Opponents Won (+1 Token)"
                    1 + carried
                } else {
                    title = "Opponent KASSAPUWA! (+2 Tokens)"
                    2 + carried
                }
            }
            newOpponentTokens += tokensWon
            message = "Opponents won $opponentTeamTricks tricks to $playerTeamTricks! (Opponents +$tokensWon tokens)"
        } else {
            // 4-4 Tie! Tokens carry over to next round
            title = "TIED ROUND! (සම විය)"
            newCarried = carried + 1
            message = "4-4 Tie! 1 token carries over to the next round (Carried: $newCarried)."
        }

        val updatedScore = currentScore.copy(
            playerTeamTokens = newPlayerTokens,
            opponentTeamTokens = newOpponentTokens,
            roundNumber = currentScore.roundNumber + 1,
            carriedOverTokens = newCarried
        )

        return Triple(updatedScore, title, message)
    }

    fun getNextPosition(current: PlayerPosition): PlayerPosition {
        return when (current) {
            PlayerPosition.SOUTH -> PlayerPosition.WEST
            PlayerPosition.WEST -> PlayerPosition.NORTH
            PlayerPosition.NORTH -> PlayerPosition.EAST
            PlayerPosition.EAST -> PlayerPosition.SOUTH
        }
    }
}
