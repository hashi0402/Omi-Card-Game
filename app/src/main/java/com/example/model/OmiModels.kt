package com.example.model

/**
 * Standard 4 Suits in Sri Lankan Omi card game.
 */
enum class Suit(val symbol: String, val displayNameEn: String, val displayNameSi: String, val isRed: Boolean) {
    CLUBS("♣", "Clubs", "කලාබර", false),
    SPADES("♠", "Spades", "ඉස්කෝප", false),
    HEARTS("♥", "Hearts", "හර්ත", true),
    DIAMONDS("♦", "Diamonds", "රූයිත", true);

    companion object {
        fun fromSymbol(sym: String): Suit = entries.find { it.symbol == sym } ?: SPADES
    }
}

/**
 * 32 cards are used in Omi: 7, 8, 9, 10, J, Q, K, A for each suit.
 * Sri Lankan Omi rank order (from lowest to highest):
 * 7 < 8 < 9 < 10 < J < Q < K < A
 */
enum class CardRank(val label: String, val power: Int) {
    SEVEN("7", 1),
    EIGHT("8", 2),
    NINE("9", 3),
    TEN("10", 4),
    JACK("J", 5),
    QUEEN("Q", 6),
    KING("K", 7),
    ACE("A", 8);
}

data class Card(
    val suit: Suit,
    val rank: CardRank,
    val id: String = "${suit.name}_${rank.name}"
) {
    val displayLabel: String = rank.label
}

enum class PlayerPosition(val id: Int, val labelEn: String, val labelSi: String) {
    SOUTH(0, "You", "ඔබ"),
    WEST(1, "Left Opponent", "වම් ප්‍රතිවාදියා"),
    NORTH(2, "Partner", "සහකරු"),
    EAST(3, "Right Opponent", "දකුණු ප්‍රතිවාදියා");

    val isPlayerTeam: Boolean get() = this == SOUTH || this == NORTH
}

data class Player(
    val position: PlayerPosition,
    val name: String,
    val avatarEmoji: String,
    val isHuman: Boolean = false,
    val hand: List<Card> = emptyList(),
    val tricksWon: Int = 0
)

data class PlayedCard(
    val playerPosition: PlayerPosition,
    val card: Card
)

enum class GamePhase {
    SPLASH,
    MAIN_MENU,
    TRUMP_SELECTION,
    PLAYING_CARDS,
    ROUND_SUMMARY,
    SETTINGS,
    MULTIPLAYER_LOBBY,
    PRIVATE_ROOM,
    PROFILE,
    RULES
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SINHALA("si", "සිංහල")
}

data class TableTheme(
    val id: String,
    val name: String,
    val feltColorHex: Long,
    val isLocked: Boolean = false
)

data class CardBackTheme(
    val id: String,
    val name: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val patternType: String
)

data class UserPreferences(
    val language: Language = Language.ENGLISH,
    val musicEnabled: Boolean = false,
    val musicVolume: Float = 1.0f,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 1.0f,
    val parallaxEnabled: Boolean = true,
    val parallaxLevel: Float = 0.10f,
    val hapticsEnabled: Boolean = true,
    val hapticsLevel: Float = 0.50f,
    val undoWindowSeconds: Int = 14,
    val selectedTableFeltIndex: Int = 0,
    val selectedCardBackIndex: Int = 0,
    val coins: Int = 2500,
    val totalWins: Int = 2
)

data class MatchScore(
    val playerTeamTokens: Int = 0,
    val opponentTeamTokens: Int = 0,
    val targetTokens: Int = 10,
    val roundNumber: Int = 1,
    val carriedOverTokens: Int = 0
)

data class GameState(
    val phase: GamePhase = GamePhase.SPLASH,
    val players: Map<PlayerPosition, Player> = emptyMap(),
    val dealerPosition: PlayerPosition = PlayerPosition.EAST,
    val trumpChooserPosition: PlayerPosition = PlayerPosition.SOUTH,
    val trumpSuit: Suit? = null,
    val currentTurn: PlayerPosition = PlayerPosition.SOUTH,
    val currentTrick: List<PlayedCard> = emptyList(),
    val leadSuit: Suit? = null,
    val completedTricksThisRound: Int = 0,
    val lastTrickWinner: PlayerPosition? = null,
    val lastTrickCards: List<PlayedCard> = emptyList(),
    val matchScore: MatchScore = MatchScore(),
    val roundResultAnnouncement: String? = null,
    val isRoundFinished: Boolean = false,
    val isMatchFinished: Boolean = false,
    val winningTeamAnnouncement: String? = null,
    val statusMessage: String = ""
)
