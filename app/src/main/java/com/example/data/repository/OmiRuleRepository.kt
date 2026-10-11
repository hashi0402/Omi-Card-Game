package com.example.data.repository

import com.example.data.db.OmiRuleDao
import com.example.data.db.OmiRuleEntity
import kotlinx.coroutines.flow.Flow

class OmiRuleRepository(private val ruleDao: OmiRuleDao) {

    val allRules: Flow<List<OmiRuleEntity>> = ruleDao.getAllRules()

    suspend fun ensureDefaultRulesPopulated() {
        if (ruleDao.getRulesCount() == 0) {
            val defaultRules = listOf(
                OmiRuleEntity(
                    category = "DECK",
                    titleEn = "32-Card Traditional Deck",
                    titleSi = "කාඩ්පත් 32ක සම්ප්‍රදායික කට්ටලය",
                    summaryEn = "Only 32 cards are used (7, 8, 9, 10, J, Q, K, A) in 4 suits.",
                    summarySi = "කොළ 32ක් පමණක් භාවිත වේ (7, 8, 9, 10, J, Q, K, A).",
                    detailsEn = "Omi is played with a 32-card deck stripped of cards 2 through 6. Card power from lowest to highest: 7 < 8 < 9 < 10 < J < Q < K < A. Ace is always the highest card.",
                    detailsSi = "ඔමි ක්‍රීඩාවේදී 2 සිට 6 දක්වා කාඩ්පත් ඉවත් කර ඉතිරි 32 භාවිත වේ. කාඩ්පත් බලය: 7 < 8 < 9 < 10 < J < Q < K < A (ආසියා ඉහළම කාඩ්පතයි).",
                    orderIndex = 1
                ),
                OmiRuleEntity(
                    category = "DEAL",
                    titleEn = "Two-Phase Deal (4 + 4 Cards)",
                    titleSi = "දෙපියවර බෙදීම (කාඩ් 4 + 4)",
                    summaryEn = "First 4 cards dealt, then trump chosen, then remaining 4 cards dealt.",
                    summarySi = "මුලින්ම කාඩ්පත් 4 බැගින් බෙදා තුරුම්පුව තෝරා, ඉතිරි කාඩ් 4 බෙදනු ලැබේ.",
                    detailsEn = "Dealer distributes 4 cards each. The player next to the dealer inspects their 4 cards and declares the trump suit. Then remaining 4 cards are dealt, giving each player 8 cards in hand.",
                    detailsSi = "බෙදන්නා පළමුව ක්‍රීඩකයන්ට කාඩ් 4 බැගින් බෙදයි. තුරුම්පු තෝරන්නා තම කාඩ් 4 පරීක්ෂා කර තුරුම්පුව තෝරයි. ඉන්පසු ඉතිරි කාඩ් 4 බෙදා එක් අයෙකුට කාඩ් 8ක් සම්පූර්ණ කෙරේ.",
                    orderIndex = 2
                ),
                OmiRuleEntity(
                    category = "TRUMP",
                    titleEn = "Trump Suit (තුරුම්පුව)",
                    titleSi = "තුරුම්පු බලය සහ නීති",
                    summaryEn = "Trump suit outranks all other suits in trick taking.",
                    summarySi = "තුරුම්පු කොළයක් අනෙකුත් සියලුම කොළ වලට වඩා බලවත්ය.",
                    detailsEn = "Any card of the trump suit beats any card of the non-trump suits, regardless of rank. If multiple trump cards are played in a trick, the highest trump card wins.",
                    detailsSi = "වටයක් තුළ ඕනෑම තුරුම්පු කොළයක් වෙනත් කොළ වලට වඩා බලවත් වේ. තුරුම්පු කොළ කිහිපයක් වැටුණහොත් ඉහළම තුරුම්පු කාඩ්පත හිමි ක්‍රීඩකයා ජයග්‍රහණය කරයි.",
                    orderIndex = 3
                ),
                OmiRuleEntity(
                    category = "PLAY",
                    titleEn = "Follow Suit & Cutting Rule",
                    titleSi = "කොළය අනුගමනය කිරීම සහ කැපීම (Cut)",
                    summaryEn = "Must follow lead suit if held. If void, player can cut with trump or discard.",
                    summarySi = "ඇදපු කොළයේ වර්ගය ඇත්නම් අනිවාර්යයෙන් දැමිය යුතුය. නැත්නම් තුරුම්පුවෙන් කපාගත හැක.",
                    detailsEn = "Players must play a card of the suit led if they hold one. If a player has no cards of the led suit, they may 'cut' with a trump card or discard any other card.",
                    detailsSi = "ඇදපු කොළයේ වර්ගය තමන් සතුව තිබේ නම් අනිවාර්යයෙන්ම එම වර්ගයේ කොළයක් දැමිය යුතුය. එම වර්ගය නොමැති නම් පමණක් තුරුම්පුවෙන් කපා ජයගැනීමට හෝ වෙනත් වර්ගයක් දැමිය හැක.",
                    orderIndex = 4
                ),
                OmiRuleEntity(
                    category = "KAPOTHI",
                    titleEn = "Kapothi! (කපෝති)",
                    titleSi = "කපෝති ජයග්‍රහණය (+2 Tokens)",
                    summaryEn = "Winning all 8 tricks in a single round yields 2 tokens (or 3 with carried over).",
                    summarySi = "එක් වටයක කැපීම් 8ම ජයගත් විට ටෝකන් 2ක් (කපෝති) හිමිවේ.",
                    detailsEn = "If the trump calling partnership wins all 8 tricks without conceding a single trick, they achieve KAPOTHI! They receive 2 tokens plus any carried over tokens.",
                    detailsSi = "තුරුම්පුව තේරූ කණ්ඩායම ප්‍රතිවාදීන්ට කිසිදු කැපීමක් නොදී වට 8ම ජයග්‍රහණය කළහොත් 'කපෝති' හිමිවන අතර ටෝකන් 2ක් සමඟ පෙර වටවල ඉතිරි වූ ටෝකන් ද ලැබේ.",
                    tokenValue = 2,
                    orderIndex = 5
                ),
                OmiRuleEntity(
                    category = "KASSAPUWA",
                    titleEn = "Kassapuwa! (කස්සපුව)",
                    titleSi = "කස්සපුව (+2 Tokens)",
                    summaryEn = "If defenders win 5 or more tricks against trump callers, defenders get 2 tokens.",
                    summarySi = "තුරුම්පුව තේරූ පිලට එරෙහිව ආරක්ෂක පිල කැපීම් 5ක් හෝ වැඩි ගණනක් දිනූ විට ටෝකන් 2ක් ලැබේ.",
                    detailsEn = "If the defending partnership wins 5 or more tricks against the trump callers, it is called KASSAPUWA! Defenders are awarded 2 tokens.",
                    detailsSi = "තුරුම්පුව තේරූ පිලට ජයගැනීමට නොදී ප්‍රතිවාදී පිල කැපීම් 5ක් හෝ ඊට වැඩි ගණනක් ලබාගත් විට එය 'කස්සපුව' ලෙස හඳුන්වන අතර ටෝකන් 2ක් හිමිවේ.",
                    tokenValue = 2,
                    orderIndex = 6
                ),
                OmiRuleEntity(
                    category = "TOKENS",
                    titleEn = "Tied Round & Token Carryover",
                    titleSi = "සම ලකුණු (4-4) සහ ටෝකන් ඉදිරියට ගෙනයාම",
                    summaryEn = "A 4-4 tie carries over 1 token to the next round.",
                    summarySi = "දෙපිලම 4-4 ලෙස සම වුවහොත් ටෝකන් බෙදී නොයන අතර ඊළඟ වටයේ ජයග්‍රාහකයාට හිමිවේ.",
                    detailsEn = "If both partnerships win 4 tricks each (4-4 tie), no team receives tokens. Instead, 1 token carries over into the next round, raising the stakes!",
                    detailsSi = "දෙපිරිසම වට 4 බැගින් දිනා සම වුවහොත් (4-4), කිසිදු පිලකට ටෝකන් හිමි නොවන අතර ඊළඟ වටයට එකතු වී ඊළඟ වටයේ ජයග්‍රාහකයාට ද්විත්ව ටෝකන් ලැබේ.",
                    tokenValue = 1,
                    orderIndex = 7
                )
            )
            ruleDao.insertRules(defaultRules)
        }
    }
}
