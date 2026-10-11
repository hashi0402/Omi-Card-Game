package com.example

import com.example.engine.OmiGameEngine
import com.example.model.Card
import com.example.model.CardRank
import com.example.model.MatchScore
import com.example.model.PlayedCard
import com.example.model.PlayerPosition
import com.example.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testOmiDeckCreation() {
        val deck = OmiGameEngine.createDeck()
        // Sri Lankan Omi deck consists of 32 cards (7, 8, 9, 10, J, Q, K, A in 4 suits)
        assertEquals(32, deck.size)
    }

    @Test
    fun testFollowSuitRule() {
        val hand = listOf(
            Card(Suit.CLUBS, CardRank.TEN),
            Card(Suit.CLUBS, CardRank.JACK),
            Card(Suit.HEARTS, CardRank.ACE)
        )

        // If Clubs led and player has Clubs: must play Clubs
        assertTrue(OmiGameEngine.isCardPlayable(Card(Suit.CLUBS, CardRank.TEN), hand, Suit.CLUBS))
        assertFalse(OmiGameEngine.isCardPlayable(Card(Suit.HEARTS, CardRank.ACE), hand, Suit.CLUBS))

        // If Spades led and player has no Spades: can play any card (trump or discard)
        assertTrue(OmiGameEngine.isCardPlayable(Card(Suit.HEARTS, CardRank.ACE), hand, Suit.SPADES))
        assertTrue(OmiGameEngine.isCardPlayable(Card(Suit.CLUBS, CardRank.TEN), hand, Suit.SPADES))
    }

    @Test
    fun testTrickWinnerEvaluation() {
        // Lead suit is Hearts, Trump is Spades
        val trick = listOf(
            PlayedCard(PlayerPosition.SOUTH, Card(Suit.HEARTS, CardRank.TEN)),
            PlayedCard(PlayerPosition.WEST, Card(Suit.HEARTS, CardRank.KING)),
            PlayedCard(PlayerPosition.NORTH, Card(Suit.SPADES, CardRank.EIGHT)), // Cut with trump
            PlayedCard(PlayerPosition.EAST, Card(Suit.HEARTS, CardRank.ACE))
        )

        val winner = OmiGameEngine.evaluateTrickWinner(trick, Suit.HEARTS, Suit.SPADES)
        // North cut with 8 of Spades (trump) so North should win over Ace of Hearts
        assertEquals(PlayerPosition.NORTH, winner.playerPosition)
    }

    @Test
    fun testTrickWinnerNoTrump() {
        val trick = listOf(
            PlayedCard(PlayerPosition.SOUTH, Card(Suit.CLUBS, CardRank.SEVEN)),
            PlayedCard(PlayerPosition.WEST, Card(Suit.CLUBS, CardRank.JACK)),
            PlayedCard(PlayerPosition.NORTH, Card(Suit.CLUBS, CardRank.ACE)),
            PlayedCard(PlayerPosition.EAST, Card(Suit.CLUBS, CardRank.KING))
        )

        val winner = OmiGameEngine.evaluateTrickWinner(trick, Suit.CLUBS, Suit.DIAMONDS)
        // North played Ace of Clubs, which is highest
        assertEquals(PlayerPosition.NORTH, winner.playerPosition)
    }

    @Test
    fun testKapothiScoreResolution() {
        val initialScore = MatchScore()
        // Player team (South/North) called trump and won all 8 tricks
        val (newScore, title, _) = OmiGameEngine.resolveRoundScores(
            trumpCaller = PlayerPosition.SOUTH,
            playerTeamTricks = 8,
            opponentTeamTricks = 0,
            currentScore = initialScore
        )

        assertTrue(title.contains("KAPOTHI"))
        assertEquals(2, newScore.playerTeamTokens)
        assertEquals(0, newScore.opponentTeamTokens)
    }

    @Test
    fun testTiedRoundCarriesOver() {
        val initialScore = MatchScore(carriedOverTokens = 0)
        // 4-4 tie
        val (newScore, title, _) = OmiGameEngine.resolveRoundScores(
            trumpCaller = PlayerPosition.SOUTH,
            playerTeamTricks = 4,
            opponentTeamTricks = 4,
            currentScore = initialScore
        )

        assertTrue(title.contains("TIED"))
        assertEquals(1, newScore.carriedOverTokens)
        assertEquals(0, newScore.playerTeamTokens)
    }
}
